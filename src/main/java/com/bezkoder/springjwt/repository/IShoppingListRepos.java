package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.ShoppingList;
import com.bezkoder.springjwt.payload.response.Menu.ShoppingListResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IShoppingListRepos extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findShoppingListByOrderId(Long order_id);

    @Query("""
select o
from ShoppingList o
where o.order.user.id = :userId
  and o.order.date >= :startOfMonth
  and o.order.date < :startOfNextMonth
""")
    List<ShoppingList> findAllByUserIdAndCurrentMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("startOfNextMonth") LocalDateTime startOfNextMonth
    );
}
