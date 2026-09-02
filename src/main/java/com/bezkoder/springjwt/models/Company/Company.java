package com.bezkoder.springjwt.models.Company;

import com.bezkoder.springjwt.payload.response.Company.CompanyResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Builder.Default
    @ColumnDefault("false")
    @Column(name = "is_default_for_public", nullable = false)
    private Boolean defaultForPublic = false;

    public CompanyResponseDto toResponseDto() {
        return CompanyResponseDto.builder()
                .id(id)
                .name(name)
                .defaultForPublic(defaultForPublic)
                .build();
    }
}
