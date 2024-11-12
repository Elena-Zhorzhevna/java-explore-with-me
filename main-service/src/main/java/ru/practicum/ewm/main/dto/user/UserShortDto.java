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
    /**
     * Идентификатор пользователя.
     */
    @NotNull
    @NotBlank
    private Long id;
    /**
     * Имя пользователя.
     */
    @NotNull
    @NotBlank
    private String name;
}