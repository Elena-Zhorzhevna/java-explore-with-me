package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.main.dto.user.UserShortDto;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.model.enums.State;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    /**
     * Идентификатор события.
     */
    private Long id;

    /**
     * Краткое описание события.
     */
    private String annotation;

    /**
     * Категория, к которой относится событие.
     */
    private CategoryDto category;

    /**
     * Количество одобренных заявок на участие в данном событии.
     */
    private long confirmedRequests;

    /**
     * Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

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
     * Пользователь (краткая информация).
     */
    private UserShortDto initiator;

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
     * Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss").
     */
    private LocalDateTime publishedOn;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    private Boolean requestModeration;

    /**
     * Список состояний жизненного цикла события.
     */
    private State state;

    /**
     * Заголовок.
     */
    private String title;

    /**
     * Количество просмотров события.
     */
    private Long views;
}