package org.minjay.gamers.accounts.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    String generateAndSave(UserDetails userDetails);

    String getJwt(String token);
}
