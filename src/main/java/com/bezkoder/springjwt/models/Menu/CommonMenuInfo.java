package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.payload.response.Menu.MenuCommonInfoResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonMenuInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private int price;

    public MenuCommonInfoResponse toResponse(){
        return MenuCommonInfoResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .price(price)
                .build();
    }
}
