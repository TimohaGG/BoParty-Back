package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepos extends JpaRepository<Menu, Long> {
    Menu getOrderById(Long id);
    List<Menu> findAllByUserId(Long userId);
    List<Menu> findAllByUserIdAndTemporaryTrue(Long userId);
    List<Menu> findAllByUserIdAndTemporaryFalse(Long userId);

    @Query(
            """
select new com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse(
o.id,o.date, o.totalPrice, o.client, o.isPayed) from Menu o""")
    Page<MenuCardResponse> findAllForList(Pageable pageable);

    @Query("""
select COUNT(o) from Menu o""")
    Integer findTotalOrdersAmount();
}
