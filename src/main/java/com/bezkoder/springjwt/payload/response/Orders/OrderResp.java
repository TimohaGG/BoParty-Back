package com.bezkoder.springjwt.payload.response.Orders;

import com.bezkoder.springjwt.models.Menu.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResp {
    long id;
    Status status;
    LocalDateTime date;
    int shoppingSum;
    int staffSum;
    double taxSum;
    double totalSum;
    int salarySum;

}
