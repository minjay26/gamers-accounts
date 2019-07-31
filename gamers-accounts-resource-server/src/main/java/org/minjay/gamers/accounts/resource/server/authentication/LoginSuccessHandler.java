package org.minjay.gamers.accounts.resource.server.authentication;

import org.joda.time.DateTime;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.resource.server.util.HttpUtils;
import org.minjay.gamers.accounts.service.TokenService;
import org.minjay.gamers.accounts.service.UserService;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessHandler.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        LOGGER.info("user login success with username : {},userId : {}", loginUser.getUsername(), loginUser.getUserId());

        User user = userService.findById(loginUser.getUserId());
        user.setLastLoginDate(DateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginIp(HttpUtils.getIpAddr(request));
        userService.save(user);

        String token = tokenService.generateAndSave(loginUser);
        response.setHeader("x-auth-token", token);
    }

}
