package com.bezkoder.springjwt.repository;

import java.util.Optional;

import com.bezkoder.springjwt.models.User.ERole;
import com.bezkoder.springjwt.models.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
