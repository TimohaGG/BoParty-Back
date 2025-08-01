package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngredientsRepos extends JpaRepository<Ingredient,Long> {
}
