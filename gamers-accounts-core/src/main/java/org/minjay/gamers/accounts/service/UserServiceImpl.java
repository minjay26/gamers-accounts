package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.security.userdetails.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl extends EntityServiceSupport<User, Long, UserRepository> implements UserService {

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        super(repository);
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
}
