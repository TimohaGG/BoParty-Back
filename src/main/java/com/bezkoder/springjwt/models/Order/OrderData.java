package com.bezkoder.springjwt.models.Order;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.Status;
import com.bezkoder.springjwt.payload.response.Orders.OrderResp;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Deprecated
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @OneToOne(cascade = CascadeType.ALL)
    public Menu menu;

    public Status status;

    public LocalDateTime date(){
        return menu != null ? menu.getDate() : null;
    }

    public int shoppingSum;
    public int staffSum;
    public double taxSum(){
        return menu!= null ? menu.getTaxPercentageCalc() : 0;
    };


    public int totalSum(){
        return menu != null ? menu.getTotalPrice() : 0;
    }

    public int salarySum(){
        return menu != null ? (int)
                (menu.getTotalPrice() - staffSum - shoppingSum - taxSum())
                : 0;
    }

    public static OrderResp toOrderResp(OrderData orderData){
        return OrderResp.builder()
                .id(orderData.id)
                .status(orderData.status)
                .totalSum(orderData.totalSum())
                .salarySum(orderData.salarySum())
                .shoppingSum(orderData.getShoppingSum())
                .taxSum(orderData.taxSum())
                .date(orderData.date())
                .staffSum(orderData.getStaffSum())
                .build();
    }

}

