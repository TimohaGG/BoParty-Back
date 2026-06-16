package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.Expences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpencesResponse {
    private Long id;
    private Long menuId;
    private String client;
    private LocalDateTime date;
    private int cook;
    private List<ExpencesWaiterResponse> waiters;
    private List<OtherExpencesResponse> otherExpences;
    private List<ShoppingSumResponse> shoppingSums;

    public static ExpencesResponse from(Expences expences) {
        return ExpencesResponse.builder()
                .id(expences.getId())
                .menuId(expences.getMenu() == null ? null : expences.getMenu().getId())
                .client(expences.getMenu() == null ? null : expences.getMenu().getClient())
                .date(expences.getMenu() == null ? null : expences.getMenu().getDate())
                .cook(expences.getCook())
                .waiters(expences.getWaiters().stream().map(ExpencesWaiterResponse::from).toList())
                .otherExpences(expences.getOtherExpences().stream().map(OtherExpencesResponse::from).toList())
                .shoppingSums(expences.getShoppingSums().stream().map(ShoppingSumResponse::from).toList())
                .build();
    }
}
