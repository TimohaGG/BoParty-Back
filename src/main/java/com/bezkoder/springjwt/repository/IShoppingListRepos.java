package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IShoppingListRepos extends JpaRepository<ShoppingList, Long> {
    ShoppingList findShoppingListByOrderId(Long order_id);


}
