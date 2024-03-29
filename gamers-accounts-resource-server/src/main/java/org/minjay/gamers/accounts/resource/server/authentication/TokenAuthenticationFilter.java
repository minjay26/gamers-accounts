package org.minjay.gamers.accounts.resource.server.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.apache.commons.lang3.StringUtils;
import org.minjay.gamers.accounts.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RequestMatcher requiresAuthenticationRequestMatcher;
    private RequestMatcher requiresBeareTokenRequestMatcher;
    private RequestMatcher ignoreAuthenticationRequestMatcher;
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public TokenAuthenticationFilter(String... noRequireAuthenticationPath) {
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("x-auth-token");
        this.requiresBeareTokenRequestMatcher = new RequestHeaderRequestMatcher("Authorization");

        List<RequestMatcher> matchers = new ArrayList<>();
        for (String path : noRequireAuthenticationPath) {
            matchers.add(new AntPathRequestMatcher(path));
        }
        this.ignoreAuthenticationRequestMatcher = new AndRequestMatcher(matchers);
    }

    protected String getTokenOrUserId(HttpServletRequest request) {
        if (StringUtils.isNotBlank(request.getHeader("x-auth-token"))) {
            return request.getHeader("x-auth-token");
        }
        return request.getParameter("userId");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!requiresAuthentication(request, response)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authResult = null;
        AuthenticationException failed = null;
        String token = getTokenOrUserId(request);
        String jwt;
        if (StringUtils.isBlank(token)) {
            jwt = request.getHeader("Authorization");
        } else {
            jwt = tokenService.getJwt(token);
        }

        if (StringUtils.isBlank(jwt)) {
            failed = new InsufficientAuthenticationException("JWT is Empty");
        } else {
            try {
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(jwt));
                authResult = this.getAuthenticationManager().authenticate(authToken);
            } catch (JWTDecodeException e) {
                logger.error("JWT format error", e);
                failed = new InsufficientAuthenticationException("JWT format error", failed);
            } catch (InternalAuthenticationServiceException e) {
                logger.error(
                        "An internal error occurred while trying to authenticate the user.",
                        failed);
                failed = e;
            } catch (AuthenticationException e) {
                failed = e;
            } catch (Exception e) {
                unsuccessfulAuthentication(request, response, failed);
                return;
            }
        }

        if (authResult != null) {
            successfulAuthentication(request, response, filterChain, authResult);
        } else if (!permissiveRequest(request)) {
            unsuccessfulAuthentication(request, response, failed);
            return;
        }

        filterChain.doFilter(request, response);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) {
        if (ignoreAuthenticationRequestMatcher.matches(request)) {
            return false;
        }
        return requiresAuthenticationRequestMatcher.matches(request) || requiresBeareTokenRequestMatcher.matches(request)
                || request.getParameterMap().containsKey("userId");
    }

    protected boolean permissiveRequest(HttpServletRequest request) {
        if (permissiveRequestMatchers == null)
            return false;
        for (RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if (permissiveMatcher.matches(request))
                return true;
        }
        return false;
    }

    public void setPermissiveUrl(String... urls) {
        if (permissiveRequestMatchers == null)
            permissiveRequestMatchers = new ArrayList<>();
        for (String url : urls)
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
    }

    public void setAuthenticationSuccessHandler(
            AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(
            AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

}
