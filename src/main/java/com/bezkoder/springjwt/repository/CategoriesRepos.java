package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoriesRepos extends JpaRepository<Category, Long> {
    List<Category> findAllByCompanyId(long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id=:companyId ORDER BY c.sortingOrder ASC")
    List<Category> findAllByCompanyIdOrdered(@Param("companyId") long companyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET company_id = :companyId WHERE user_id = :userId AND company_id IS NULL", nativeQuery = true)
    int assignLegacyUserCategoriesToCompany(@Param("userId") long userId, @Param("companyId") long companyId);

}
