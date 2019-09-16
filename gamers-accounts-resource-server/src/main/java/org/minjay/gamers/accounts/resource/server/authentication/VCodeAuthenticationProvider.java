package org.minjay.gamers.accounts.resource.server.authentication;

import org.apache.commons.lang3.StringUtils;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.mail.MailSendSupport;
import org.minjay.gamers.accounts.service.UserService;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.minjay.gamers.accounts.mail.MailSendSupport.VERIFICATION_CODE_KEY_PREFIX;

@Component
public class VCodeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        VCodeAuthenticationToken token = (VCodeAuthenticationToken) authentication;
        String verificationCodeKey = VERIFICATION_CODE_KEY_PREFIX + MailSendSupport.TEMPLATE_LOGIN_EMAIL
                + ":" + token.getEmail();
        String code = valueOperations.get(verificationCodeKey);
        if (!StringUtils.equals(code, token.getVcode())) {
            throw new BadCredentialsException("VCode login fail");
        }
        User user = userService.findByEmail(token.getEmail());
        LoginUser loginUser = new LoginUser.LoginUserBuilder()
                .userId(user.getId())
                .username(user.getUsername())
                .password("PROTECT")
                .authorities(new ArrayList<>())
                .build();
        return new UsernamePasswordAuthenticationToken(loginUser, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(VCodeAuthenticationToken.class);
    }
}
