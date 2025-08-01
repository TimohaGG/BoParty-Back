package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Position> positions;

    @ManyToOne
    private User user;

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public CategoryResponseDto toResponseDto(){
        return CategoryResponseDto.builder()
                .id(id)
                .name(name).build();
    }
}
