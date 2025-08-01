package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Position.IngredientAmount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IIngAmountRepos extends JpaRepository<IngredientAmount,Long> {
    public List<IngredientAmount> findByPositionId(Long posId);
    public void deleteAllByIngredientId(Long ingredientId);

    Optional<IngredientAmount> findByPositionIdAndIngredientId(long posId, Long ingId);
}
