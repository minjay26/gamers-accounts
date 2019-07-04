package org.minjay.gamers.accounts.data.repository;

import org.minjay.gamers.accounts.data.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);
}
