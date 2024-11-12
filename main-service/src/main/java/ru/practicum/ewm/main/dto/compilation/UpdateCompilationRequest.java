package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    /**
     * Список идентификаторов событий входящих в подборку.
     */
    @Builder.Default
    private Set<Long> events = new HashSet<>();
    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    @Builder.Default
    private Boolean pinned = false;
    /**
     * Заголовок подборки.
     */
    @Size(min = 1, max = 50)
    private String title;
}