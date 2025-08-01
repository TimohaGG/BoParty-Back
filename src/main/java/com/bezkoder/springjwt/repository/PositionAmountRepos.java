package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.PositionAmount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionAmountRepos extends JpaRepository<PositionAmount, Long> {
}
