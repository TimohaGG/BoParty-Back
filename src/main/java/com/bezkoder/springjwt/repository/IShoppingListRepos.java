package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IShoppingListRepos extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findShoppingListByOrderId(Long order_id);


}
