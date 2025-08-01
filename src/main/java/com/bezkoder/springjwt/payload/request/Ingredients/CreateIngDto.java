package com.bezkoder.springjwt.payload.request.Ingredients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateIngDto {
    public String name;
    public long categoryId;
    public long userId;
}
