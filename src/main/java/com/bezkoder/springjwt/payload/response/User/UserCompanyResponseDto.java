package com.bezkoder.springjwt.payload.response.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyResponseDto {
    private Long id;
    private String username;
    private String email;
    private Long companyId;
    private String companyName;
}
