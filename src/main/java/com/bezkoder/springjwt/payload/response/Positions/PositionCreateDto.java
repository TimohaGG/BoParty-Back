package com.bezkoder.springjwt.payload.response.Positions;

import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionCreateDto {
    private String name;
    private int weight;
    private int price;

    private long categoryId;

    private String ingredients;


}
