package org.minjay.gamers.accounts.data.repository;

import org.minjay.gamers.accounts.data.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * UserRepository
 *
 * @author minjay
 * @date 2019/7/3 21:44
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);
}
