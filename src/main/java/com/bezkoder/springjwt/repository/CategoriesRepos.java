package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface CategoriesRepos extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(long userId);

}
