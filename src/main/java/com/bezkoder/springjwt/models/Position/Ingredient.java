package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import com.bezkoder.springjwt.payload.request.Ingredients.IngRequestDto;
import com.bezkoder.springjwt.payload.response.Ingredients.IngredientResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    private IngredientCategory ingCategory;

    public IngredientResponse toIngredientDto() {
        return IngredientResponse.builder()
                .id(Id)
                .name(name)
                .category(ingCategory.toCategoryDto())
                .build();
    }

    public IngRequestDto toAmountRequestDto() {
        return IngRequestDto.builder()
                .id(Id)
                .name(name)
                .category(ingCategory.toCategoryDto())
                .build();
    }
}
