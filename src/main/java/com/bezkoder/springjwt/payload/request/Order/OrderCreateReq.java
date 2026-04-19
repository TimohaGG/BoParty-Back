package com.bezkoder.springjwt.payload.request.Order;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderCreateReq {
    private long menuId;
    private Status status;
    private int shoppingSum;
    private int staffSum;

}
