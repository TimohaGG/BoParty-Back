package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IShoppingListItemRepos extends JpaRepository<ShoppingListItem, Long> {
//    ShoppingListItem findById(long id);
}
