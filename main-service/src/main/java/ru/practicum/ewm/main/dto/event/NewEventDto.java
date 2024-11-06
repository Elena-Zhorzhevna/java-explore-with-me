package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.ewm.main.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    /**
     * Краткое описание события.
     */
    private String annotation;

    /**
     * Категория, к которой относится событие.
     */
    @JsonProperty("id")
    private Long category;

    /**
     * Полное описание события.
     */
    private String description;

    /**
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Широта и долгота места проведения событий.
     */
    private Location location;

    /**
     * Нужно ли оплачивать участие.
     */
    private Boolean paid;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
     */
    private Long participantLimit;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    private Boolean requestModeration;

    /**
     * Заголовок.
     */
    private String title;
}
