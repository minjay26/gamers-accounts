package org.minjay.gamers.accounts.data.repository;

import org.minjay.gamers.accounts.data.domain.UserFocus;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserFocusRepository extends PagingAndSortingRepository<UserFocus, Long> {

    List<UserFocus> findAllByFromUserId(Long fromUserId);

    List<UserFocus> findAllByToUserId(Long toUserId);
}
