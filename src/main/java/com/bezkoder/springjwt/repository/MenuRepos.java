package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.payload.response.Menu.MinMenuResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface MenuRepos extends JpaRepository<Menu, Long> {
    Menu getOrderById(Long id);
    List<Menu> findAllByUserId(Long userId);
    List<Menu> findAllByUserIdAndTemporaryTrue(Long userId);
    List<Menu> findAllByUserIdAndTemporaryFalse(Long userId);


    @Query("""
select new com.bezkoder.springjwt.payload.response.Menu.MinMenuResp(
o.id,o.date,o.client,o.totalPrice,o.temporary
) from Menu o where MONTH(o.date) = MONTH(CURRENT_DATE) AND YEAR(o.date) = YEAR(CURRENT_DATE) order by o.date desc 
""")
    List<MinMenuResp> findAllMin();

    @Query(
            """
select new com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse(
o.id,o.date, o.totalPrice, o.client, o.isPayed, o.temporary) from Menu o where o.date >=:startDate AND o.user.id = :userId""")
    Page<MenuCardResponse> findAllForList(@Param("startDate")LocalDateTime startDate,@Param("userId")long userId, Pageable pageable);


    @Query(
            """
select new com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse(
o.id,o.date, o.totalPrice, o.client, o.isPayed, o.temporary) from Menu o where o.date <:startDate AND o.user.id = :userId""")
    Page<MenuCardResponse> findAllForListArchive(@Param("startDate")LocalDateTime startDate,@Param("userId")long userId, Pageable pageable);

    Page<Menu> findAllByDateAfter(LocalDateTime date, Pageable pageable);
    Page<Menu> findAllByDateStartingWith(LocalDateTime date, Pageable pageable);

    @Query("""
select COUNT(o) from Menu o""")
    Integer findTotalOrdersAmount();
    @Query("""
select COUNT(o) from Menu o where o.date >=:startDate""")
    Integer findTotalFutureOrders(@Param("startDate")LocalDateTime startDate);


    @Query("""
select COUNT(o) from Menu o where o.date <:startDate""")
    Integer findTotalArchiveOrders(@Param("startDate")LocalDateTime startDate);
}
