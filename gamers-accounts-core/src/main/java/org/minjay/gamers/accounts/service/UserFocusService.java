package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.UserFocus;

import java.util.List;

public interface UserFocusService extends EntityService<UserFocus, Long> {

    List<UserFocus> getAllFocus(Long userId);

}
