package org.minjay.gamers.accounts.resource.server.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getToken();
        String value = jwt.getSubject();
        try {
            Map<String, Object> authenticationMap = objectMapper.readValue(value, Map.class);
            LoginUser loginUser = new LoginUser.LoginUserBuilder()
                    .userId(Long.parseLong(String.valueOf(authenticationMap.get("userId"))))
                    .username((String) authenticationMap.get("username"))
                    .password("PROTECT")
                    .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                            .collectionToCommaDelimitedString((ArrayList) authenticationMap.get("authorities"))))
                    .build();
            authentication = new JwtAuthenticationToken(loginUser, jwt, null);
        } catch (Exception e) {
            throw new BadCredentialsException("JWT token verify fail", e);
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }

}
