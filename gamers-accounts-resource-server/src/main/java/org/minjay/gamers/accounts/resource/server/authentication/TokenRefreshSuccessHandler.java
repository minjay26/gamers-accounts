package org.minjay.gamers.accounts.resource.server.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.minjay.gamers.accounts.service.TokenService;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class TokenRefreshSuccessHandler implements AuthenticationSuccessHandler {

    private static final int tokenRefreshInterval = 300;

    @Autowired
    private TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getToken();
        if (shouldTokenRefresh(jwt.getIssuedAt())) {
            tokenService.refreshToken(request.getParameter("x-auth-token"), (LoginUser) authentication.getPrincipal());
        }
    }

    protected boolean shouldTokenRefresh(Date issueAt) {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(tokenRefreshInterval).isAfter(issueTime);
    }

}
