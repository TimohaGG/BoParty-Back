package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order.OrderAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAdditionalInfoRepos extends JpaRepository<OrderAdditionalInfo, Long> {
}
