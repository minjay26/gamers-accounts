package org.minjay.gamers.accounts.service;

import org.minjay.gamers.security.userdetails.LoginUser;

public interface TokenService {

    String generateAndSave(LoginUser loginUser);

    String getJwt(String token);

    String refreshToken(String token, LoginUser loginUser);
}
