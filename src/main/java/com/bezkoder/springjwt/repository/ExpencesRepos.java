package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Menu.Expences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpencesRepos extends JpaRepository<Expences, Long> {
    boolean existsByMenuId(Long menuId);

    boolean existsByMenuIdAndIdNot(Long menuId, long id);

    @Query("""
            select e from Expences e
            where (:startDate is null or e.menu.date >= :startDate)
              and (:endDate is null or e.menu.date <= :endDate)
                          and e.menu.user.id = (:userId)
            order by e.menu.date desc
            """)
    List<Expences> findAllInDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("userId")long userId);

    @Query("""
            select e from Expences e
            where e.menu.user.id = (:userId)
            order by e.menu.date desc
            """)
    List<Expences> findAll(@Param("userId")long userId);
}
