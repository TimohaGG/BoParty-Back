package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepos extends JpaRepository<Orders, Long> {
    Orders getOrderById(Long id);
    List<Orders> findAllByUserId(Long userId);
    List<Orders> findAllByUserIdAndTemporaryTrue(Long userId);
    List<Orders> findAllByUserIdAndTemporaryFalse(Long userId);

}
