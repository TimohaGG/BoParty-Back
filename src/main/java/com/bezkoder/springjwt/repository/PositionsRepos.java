package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionsRepos extends JpaRepository<Position,Long> {

    @Query("SELECT p from Position p WHERE p.category.id= :categoryId")
    List<Position> findAllByCategoryId(@Param("categoryId")Long categoryId);

    List<Position> findAllByNameContainsIgnoreCase(String name);

    Optional<Position> findByName(String name);
    Optional<Position> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Position p WHERE LOWER(REPLACE(p.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    Optional<Position> findByNameIgnoreCaseAndWhitespace(@Param("name") String name);

    List<Position> findAllByCategoryUserId(Long categoryId);


    @Query("""
    SELECT p FROM Position p WHERE p.isAccessible AND p.category.id=:categoryId
""")
    List<Position> findAccessibleByCategoryId(@Param("categoryId")Long categoryId);
}
