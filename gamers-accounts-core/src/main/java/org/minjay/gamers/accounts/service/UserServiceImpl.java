package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.accounts.security.LoginUser;
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
        LoginUser loginUser = new LoginUser(username, user.getPassword(), Arrays.asList(new SimpleGrantedAuthority("a")));
        return loginUser;
    }
}
