package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {

    /**
     * Заголовок подборки.
     */
    @NotBlank
    @Size(min = 1,max = 50)
    private String title;
    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    private boolean pinned;
    /**
     * Список идентификаторов событий входящих в подборку.
     */
    private Set<Long> events = new HashSet<>();
}