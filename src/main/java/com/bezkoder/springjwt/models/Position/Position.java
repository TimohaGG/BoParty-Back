package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private double price;

    @Builder.Default
    @ColumnDefault("10")
    private int minimumAmount = 10;

    @Builder.Default
    @ColumnDefault("true")
    private boolean isAccessible = true;

    @Column(nullable = true, length = 2000)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "position", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientAmount> ingredients = new ArrayList<>();

    public Position(String name, String description, Double weight, double price, Category category, List<IngredientAmount> ingredients) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.category = category;
        this.ingredients = ingredients;
    }

    public void setIngredients(List<IngredientAmount> ingredients) {
        this.ingredients = new ArrayList<>();
        this.ingredients = ingredients;
    }

    public void addIngredientAmount(IngredientAmount ingredientAmount) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredientAmount);
    }

    public PositionResponseDto toResponseDto() {
        return PositionResponseDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .weight(weight)
                .price(price)
                .minimumAmount(minimumAmount)
                .imgUrl(imgUrl)
                .isAccessible(isAccessible)
                .category(category.toResponseDto())
                .ingredients(ingredients.stream().map(IngredientAmount::toDTO).toList())
                .build();
    }

    public PositionMinDto toMinDto() {
        return PositionMinDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .weight(weight)
                .price(price)
                .minimumAmount(minimumAmount)
                .imgUrl(imgUrl)
                .isAccessible(isAccessible)
                .category(category.toResponseDto())
                .build();
    }
}
