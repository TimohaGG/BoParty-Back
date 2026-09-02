package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
    Optional<Company> findFirstByDefaultForPublicTrue();
    List<Company> findAllByDefaultForPublicTrue();
}
