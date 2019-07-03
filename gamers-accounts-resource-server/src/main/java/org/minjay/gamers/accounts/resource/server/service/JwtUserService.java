package org.minjay.gamers.accounts.resource.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.accounts.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.UUID;

public class JwtUserService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRepository userRepository;

    public JwtUserService() {
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();  //默认使用 bcrypt， strength=10
    }

    public UserDetails getUserLoginInfo(String username) {
        return loadUserByUsername(username);
    }

    public String saveUserLoginInfo(UserDetails user) {
        String salt = "123456ef"; //BCrypt.gensalt();  正式开发时可以调用该方法实时生成加密的salt
        String token = UUID.randomUUID().toString();
        Algorithm algorithm = Algorithm.HMAC256(salt);
        Date date = new Date(System.currentTimeMillis() + 3600 * 1000);  //设置1小时后过期
        String jwt = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
        String key = "accounts:jwt:" + token;
        redisTemplate.opsForValue().set(key, jwt);
        redisTemplate.expireAt(key, date);
        return token;
    }

    public String getJwt(String token) {
        return redisTemplate.opsForValue().get("accounts:jwt:" + token);
    }

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        org.minjay.gamers.accounts.data.domain.User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        LoginUser loginUser = new LoginUser(username, null, null);
        return loginUser;
    }

}
