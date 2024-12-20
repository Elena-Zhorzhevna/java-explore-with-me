package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    /**
     * Категория, к которой относится событие.
     */
    @NotNull
    private Long category;

    /**
     * Полное описание события.
     */
    @NotBlank
    @Size(min = 20, max = 7000)
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
    @Builder.Default
    private Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
     */
    @PositiveOrZero
    @Builder.Default
    private Long participantLimit = 0L;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Заголовок.
     */
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}