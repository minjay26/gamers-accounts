package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.service.model.EmailVerificationCode;
import org.minjay.gamers.accounts.service.model.ModifyPasswordDto;
import org.minjay.gamers.accounts.service.model.UserRegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends EntityService<User, Long>, UserDetailsService {

    void modifyPasswordApply(User user, EmailVerificationCode verificationCode);

    void modifyPassword(User user, ModifyPasswordDto dto);

    void register(UserRegisterDto dto);
}
