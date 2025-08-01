package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionsRepos extends JpaRepository<Position,Long> {
    List<Position> findAllByCategoryId(Long categoryId);

    List<Position> findAllByNameContainsIgnoreCase(String name);

    Optional<Position> findByName(String name);
    Optional<Position> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Position p WHERE LOWER(REPLACE(p.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    Optional<Position> findByNameIgnoreCaseAndWhitespace(@Param("name") String name);

    List<Position> findAllByCategoryUserId(Long categoryId);


}
