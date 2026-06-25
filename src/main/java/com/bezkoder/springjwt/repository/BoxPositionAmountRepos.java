package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Box.BoxPositionAmount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxPositionAmountRepos extends JpaRepository<BoxPositionAmount, Long> {
}
