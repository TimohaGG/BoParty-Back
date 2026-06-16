package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.OtherExpences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtherExpencesResponse {
    private Long id;
    private String name;
    private int amount;

    public static OtherExpencesResponse from(OtherExpences otherExpences) {
        return OtherExpencesResponse.builder()
                .id(otherExpences.getId())
                .name(otherExpences.getName())
                .amount(otherExpences.getAmount())
                .build();
    }
}
