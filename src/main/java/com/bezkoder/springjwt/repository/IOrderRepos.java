package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order.OrderData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepos extends JpaRepository<OrderData, Long> {

}
