package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaiterRepos extends JpaRepository<Waiter, Long> {
}
