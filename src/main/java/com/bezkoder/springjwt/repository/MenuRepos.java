package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepos extends JpaRepository<Menu, Long> {
    Menu getOrderById(Long id);
    List<Menu> findAllByUserId(Long userId);
    List<Menu> findAllByUserIdAndTemporaryTrue(Long userId);
    List<Menu> findAllByUserIdAndTemporaryFalse(Long userId);

}
