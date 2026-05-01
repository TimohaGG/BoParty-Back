package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.MenuAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAdditionalInfoRepos extends JpaRepository<MenuAdditionalInfo, Long> {
    List<MenuAdditionalInfo> findAllByOrderId(Long orderId);
}
