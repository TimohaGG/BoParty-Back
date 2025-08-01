package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Units;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUnitRepos extends JpaRepository<Units, Long> {
}
