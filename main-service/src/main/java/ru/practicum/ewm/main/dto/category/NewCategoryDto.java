package ru.practicum.ewm.main.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCategoryDto {
    @NotBlank
    @Size(max = 255)
    private String name;
}
