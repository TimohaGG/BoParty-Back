package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.StaffCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffCategoryRepos extends JpaRepository<StaffCategory, Long> {
    boolean existsByCodeIgnoreCase(String code);

//    Optional<StaffCategory> findByCodeIgnoreCase(String code);
}
