package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepos extends JpaRepository<Role, Long> {
}
