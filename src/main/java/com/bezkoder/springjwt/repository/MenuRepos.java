package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse;
import com.bezkoder.springjwt.payload.response.Menu.MinMenuResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("""
select new com.bezkoder.springjwt.payload.response.Menu.MinMenuResp(
o.id, o.date, o.client, o.totalPrice, o.temporary
) from Menu o
where o.user.id = :userId
  and o.temporary = false
order by o.date desc
""")
    List<MinMenuResp> findAllMinByUserId(@Param("userId") Long userId);

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


    @Query(
            """
select new com.bezkoder.springjwt.payload.response.Menu.MenuCardResponse(
o.id,o.date, o.totalPrice, o.client, o.isPayed, o.temporary) from Menu o 
where o.user.id = :userId
    AND (
    
    (:name IS NOT NULL 
        AND :name <> '' 
        AND LOWER(o.client) LIKE LOWER(CONCAT('%', :name, '%')))
        
    OR
    
    (:date IS NOT NULL 
        AND DATE(o.date) = :date)
)
""")
    List<MenuCardResponse> findAllByClientOrDate(@Param("userId")long userId, @Param("name")String name, @Param("date") LocalDate date);
}
