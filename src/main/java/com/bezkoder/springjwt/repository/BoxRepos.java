package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Box.Box;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepos extends JpaRepository<Box, Long> {
}
