package org.minjay.gamers.accounts.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private static final String JWT_REDIS_KEY_PREFIX = "accounts:jwt:";
    private static final String TOKEN_REDIS_KEY_PREFIX = "accounts:token:";

    @Value("${jwt.salt:123456}")
    private String jwtSalt;
    @Value("${token.ttl:1800}")
    private long tokenTtl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String generateAndSave(LoginUser loginUser) {
        return generateOrRefresh(null, loginUser);
    }

    @Override
    public String getJwt(String token) {
        return redisTemplate.opsForValue().get(JWT_REDIS_KEY_PREFIX + token);
    }

    @Async
    @Override
    public String refreshToken(String token, LoginUser loginUser) {
        return generateOrRefresh(token, loginUser);
    }

    private String generateOrRefresh(String oldToken, LoginUser loginUser) {
        if (StringUtils.isEmpty(oldToken)) {
            oldToken = UUID.randomUUID().toString();
        }
        preCheck(loginUser);

        Algorithm algorithm = Algorithm.HMAC256(jwtSalt);
        Date date = new Date(System.currentTimeMillis() + tokenTtl * 1000);
        String jwt;
        try {
            jwt = JWT.create()
                    .withSubject(objectMapper.writeValueAsString(loginUser))
                    .withExpiresAt(date)
                    .withIssuedAt(new Date())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new UnsupportedOperationException();
        }

        String key = "accounts:jwt:" + oldToken;
        valueOperations.set(key, jwt);
        redisTemplate.expireAt(key, date);
        valueOperations.set(TOKEN_REDIS_KEY_PREFIX + loginUser.getUsername(), oldToken);
        return oldToken;

    }

    private void preCheck(LoginUser loginUser) {
        String userTokenKey = TOKEN_REDIS_KEY_PREFIX + loginUser.getUsername();
        if (redisTemplate.hasKey(userTokenKey)) {
            String oldToken = valueOperations.get(userTokenKey);
            String jwtKey = JWT_REDIS_KEY_PREFIX + oldToken;
            redisTemplate.delete(jwtKey);
        }
    }
}
