package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepos extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
