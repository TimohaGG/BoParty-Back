package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionsRepos extends JpaRepository<Position, Long> {
    @Query("""
    SELECT p FROM Position p WHERE p.category.id=:categoryId AND (p.archived IS NULL OR p.archived = false)
""")
    List<Position> findAllByCategoryId(@Param("categoryId") Long categoryId);

    Optional<Position> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Position p WHERE LOWER(REPLACE(p.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
    Optional<Position> findByNameIgnoreCaseAndWhitespace(@Param("name") String name);

    @Query("""
    SELECT p FROM Position p WHERE p.category.company.id=:companyId AND (p.archived IS NULL OR p.archived = false)
""")
    List<Position> findAllByCategoryCompanyId(@Param("companyId") Long companyId);

    @Query("""
    SELECT p FROM Position p WHERE p.category.company.id=:companyId AND p.archived = true
""")
    List<Position> findArchivedByCategoryCompanyId(@Param("companyId") Long companyId);

    @Query("""
    SELECT p FROM Position p WHERE p.accessible AND p.category.id=:categoryId AND (p.archived IS NULL OR p.archived = false)
""")
    List<Position> findAccessibleByCategoryId(@Param("categoryId")Long categoryId);
}
