package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.StaffCategory;
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
public class StaffCategoryResponse {
    private Long id;
    private String name;
    private String code;

    public static StaffCategoryResponse builtIn(String name, String code) {
        return StaffCategoryResponse.builder()
                .id(null)
                .name(name)
                .code(code)
                .build();
    }

    public static StaffCategoryResponse from(StaffCategory category) {
        return StaffCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .build();
    }
}
