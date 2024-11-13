package ru.practicum.ewm.main.dto.compilation;

import lombok.*;
import ru.practicum.ewm.main.dto.event.EventShortDto;

import java.util.ArrayList;
import java.util.List;


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
    @Builder.Default
    private boolean pinned = false;

    /**
     * Список событий входящих в подборку.
     */
    @Builder.Default
    private List<EventShortDto> events = new ArrayList<>();
}