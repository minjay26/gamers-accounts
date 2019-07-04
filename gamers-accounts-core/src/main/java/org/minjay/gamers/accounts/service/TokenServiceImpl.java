package org.minjay.gamers.accounts.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    @Override
    public String generateAndSave(UserDetails userDetails) {
        String token = UUID.randomUUID().toString();
        preCheck(userDetails);
        Algorithm algorithm = Algorithm.HMAC256(jwtSalt);
        Date date = new Date(System.currentTimeMillis() + tokenTtl * 1000);  //设置1小时后过期
        String jwt = JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
        String key = "accounts:jwt:" + token;
        valueOperations.set(key, jwt);
        redisTemplate.expireAt(key, date);
        valueOperations.set(TOKEN_REDIS_KEY_PREFIX + userDetails.getUsername(), token);
        return token;
    }

    @Override
    public String getJwt(String token) {
        return redisTemplate.opsForValue().get(JWT_REDIS_KEY_PREFIX + token);
    }

    private void preCheck(UserDetails userDetails) {
        String userTokenKey = TOKEN_REDIS_KEY_PREFIX + userDetails.getUsername();
        if (redisTemplate.hasKey(userTokenKey)) {
            String oldToken = valueOperations.get(userTokenKey);
            String jwtKey = JWT_REDIS_KEY_PREFIX + oldToken;
            redisTemplate.delete(jwtKey);
        }
    }
}
