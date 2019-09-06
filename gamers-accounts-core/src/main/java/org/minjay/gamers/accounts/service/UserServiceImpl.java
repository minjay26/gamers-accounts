package org.minjay.gamers.accounts.service;

import org.apache.commons.lang3.StringUtils;
import org.minjay.gamers.accounts.core.UserException;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.accounts.mail.MailSendSupport;
import org.minjay.gamers.accounts.service.model.EmailVerificationCode;
import org.minjay.gamers.accounts.service.model.ModifyPasswordDto;
import org.minjay.gamers.accounts.service.model.UserRegisterDto;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.minjay.gamers.accounts.mail.MailSendSupport.VERIFICATION_CODE_KEY_PREFIX;

@Service
public class UserServiceImpl extends EntityServiceSupport<User, Long, UserRepository> implements UserService {

    private final ValueOperations<String, String> valueOperations;
    private final RedisTemplate redisTemplate;

    @Autowired
    public UserServiceImpl(UserRepository repository,
                           ValueOperations<String, String> valueOperations,
                           RedisTemplate redisTemplate) {
        super(repository);
        this.valueOperations = valueOperations;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public LoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getRepository().findByUsername(username);
        if (user == null) {
            return null;
        }
        LoginUser.LoginUserBuilder builder = new LoginUser.LoginUserBuilder().username(username)
                .userId(user.getId())
                .password(user.getPassword())
                .authorities(Arrays.asList(new SimpleGrantedAuthority("a")));
        return builder.build();
    }

    @Override
    public void modifyPasswordApply(User user, EmailVerificationCode verificationCode) {
        String verificationCodeKey = VERIFICATION_CODE_KEY_PREFIX + verificationCode.getType() +
                ":" + verificationCode.getEmail();
        String found = valueOperations.get(verificationCodeKey);

        if (StringUtils.equals(verificationCode.getCode(), found)) {
            valueOperations.set("modify:password:" + user.getId(), user.getUsername(), 60, TimeUnit.MINUTES);
            return;
        }

        throw UserException.verificationIncorrectException();
    }

    @Transactional
    @Override
    public void modifyPassword(User user, ModifyPasswordDto dto) {
        if (StringUtils.isBlank(valueOperations.get("modify:password:" + user.getId()))) {
            throw UserException.modifyPasswordFailedException();
        }

        if (!StringUtils.equals(dto.getNewPassword(), dto.getConfirmPassword())) {
            throw UserException.modifyPasswordFailedException();
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw UserException.modifyPasswordFailedException();
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        getRepository().save(user);
    }

    @Transactional
    @Override
    public void register(UserRegisterDto dto) {
        User found = getRepository().findByUsername(dto.getUsername());
        if (found != null) {
            throw UserException.usernameExistException();
        }

        String verificationCodeKey = VERIFICATION_CODE_KEY_PREFIX + MailSendSupport.TEMPLATE_BIND_EMAIL
                + ":" + dto.getEmail();
        String code = valueOperations.get(verificationCodeKey);
        if (!StringUtils.equals(code, dto.getCode())) {
            throw UserException.verificationIncorrectException();
        }

        if (!StringUtils.equals(dto.getPassword(), dto.getConfirmPassword())) {
            throw new RuntimeException();
        }

        User user = new User();
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        getRepository().save(user);
    }

    @Override
    public void updateFromCreateDynamic(User user) {
        user.setDynamicCount(user.getDynamicCount() + 1);
        getRepository().save(user);
    }
}
