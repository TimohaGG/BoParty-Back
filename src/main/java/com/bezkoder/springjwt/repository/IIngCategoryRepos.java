package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngCategoryRepos extends JpaRepository<IngredientCategory, Long> {
}
