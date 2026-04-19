package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.payload.request.Menus.MenuInfoRequest;
import com.bezkoder.springjwt.payload.response.Menu.MenuInfoResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuAdditionalInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Menu order;

    private String title;
    private String description;

    private int price;


    @Override
    public String toString() {
        return title + "," + description + "," + price;
    }


    public MenuInfoResponse toResponse() {
        return MenuInfoResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .price(price)
                .build();
    }

    public static MenuAdditionalInfo parse(MenuInfoRequest data){
        return MenuAdditionalInfo.builder()
                .title(data.getTitle())
                .description(data.getDescription())
                .price(data.getPrice())
                .build();
    }

}
