package org.minjay.gamers.accounts.resource.server.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.minjay.gamers.accounts.resource.server.authentication.VCodeAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public LoginAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
        Assert.notNull(getSuccessHandler(), "AuthenticationSuccessHandler must be specified");
        Assert.notNull(getFailureHandler(), "AuthenticationFailureHandler must be specified");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        if (!StringUtils.hasText(body)) {
            throw new AccessDeniedException("username and password required");
        }

        Map<String, String> map = new ObjectMapper().readValue(body, Map.class);

        AbstractAuthenticationToken authRequest;
        if (map.containsKey("username")) {
            String username = map.get("username");
            String password = map.get("password");
            authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);
        } else {
            authRequest = new VCodeAuthenticationToken(map.get("email"), map.get("vcode"));
        }
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
