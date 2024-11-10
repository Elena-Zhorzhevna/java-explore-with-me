package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.ewm.main.dto.event.EventShortDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    /**
     * Идентификатор подборки.
     */
    private Long id;
    /**
     * Заголовок подборки.
     */
    private String title;

    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    private boolean pinned = false;

    /**
     * Список событий входящих в подборку.
     */
    private List<EventShortDto> events = new ArrayList<>();
}