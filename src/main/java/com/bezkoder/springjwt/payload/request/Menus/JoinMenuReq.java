package com.bezkoder.springjwt.payload.request.Menus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinMenuReq {
    Long[] ordersIds;
}
