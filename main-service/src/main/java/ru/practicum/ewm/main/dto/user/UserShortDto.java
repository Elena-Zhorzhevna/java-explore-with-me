package ru.practicum.ewm.main.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDto {
    @NotNull
    @NotBlank
    private Long id;

    @NotNull
    @NotBlank
    private String name;
}
