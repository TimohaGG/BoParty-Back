package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.models.Company.Company;
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

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @ColumnDefault(value = "0")
    private int sortingOrder;

    public CategoryResponseDto toResponseDto(){
        return CategoryResponseDto.builder()
                .id(id)
                .name(name)
                .companyId(company == null ? null : company.getId())
                .companyName(company == null ? null : company.getName())
                .sortingOrder(sortingOrder)
                .build();
    }
}
