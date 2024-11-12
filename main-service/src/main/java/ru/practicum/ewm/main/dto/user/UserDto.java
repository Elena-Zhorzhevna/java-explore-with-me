package ru.practicum.ewm.main.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    /**
     * Электронная почта пользователя.
     */
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
    /**
     * Идентификатор пользователя.
     */
    private Long id;
    /**
     * Имя пользователя.
     */
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}