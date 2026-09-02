package com.bezkoder.springjwt.payload.request.Company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkUserCompanyRequest {
    private Long userId;
    private Long companyId;
}
