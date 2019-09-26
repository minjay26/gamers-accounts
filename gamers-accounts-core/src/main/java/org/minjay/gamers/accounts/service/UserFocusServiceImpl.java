package org.minjay.gamers.accounts.service;

import org.minjay.gamers.accounts.data.domain.UserFocus;
import org.minjay.gamers.accounts.data.repository.UserFocusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFocusServiceImpl extends EntityServiceSupport<UserFocus, Long, UserFocusRepository> implements UserFocusService {

    @Autowired
    public UserFocusServiceImpl(UserFocusRepository repository) {
        super(repository);
    }

    @Override
    public List<UserFocus> getAllFocus(Long userId) {
        return getRepository().findAllByFromUserId(userId);
    }
}
