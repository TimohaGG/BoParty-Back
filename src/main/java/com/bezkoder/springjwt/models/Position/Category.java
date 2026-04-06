package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Position> positions;

    @ManyToOne
    private User user;

    @ColumnDefault(value = "0")
    private int sortingOrder;

    public CategoryResponseDto toResponseDto(){
        return CategoryResponseDto.builder()
                .id(id)
                .name(name)
                .userId(user.getId())
                .build();
    }
}
