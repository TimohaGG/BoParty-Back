package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepos extends JpaRepository<Category, Long> {
}
