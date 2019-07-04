package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends EntityService<User, Long>, UserDetailsService {
}
