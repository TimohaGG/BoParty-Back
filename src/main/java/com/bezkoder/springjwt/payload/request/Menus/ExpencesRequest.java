package com.bezkoder.springjwt.payload.request.Menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpencesRequest {
    private Long id;
    private Long menuId;
    private int cook;
    private List<ExpencesWaiterRequest> waiters = new ArrayList<>();
    private List<OtherExpencesRequest> otherExpences = new ArrayList<>();
    private List<ShoppingSumRequest> shoppingSums = new ArrayList<>();
}
