package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.dto.user.UserShortDto;
import ru.practicum.ewm.main.dto.category.CategoryDto;
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
    @NotBlank
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
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Пользователь (краткая информация).
     */
    @NotBlank
    private UserShortDto initiator;

    /**
     * Широта и долгота места проведения событий.
     */
    @NotBlank
    private LocationDto location;

    /**
     * Нужно ли оплачивать участие.
     */
    @NotBlank
    @Builder.Default
    private Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
     */
    @Builder.Default
    private Long participantLimit = 0L;

    /**
     * Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Список состояний жизненного цикла события.
     */
    private State state;

    /**
     * Заголовок.
     */
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    /**
     * Количество просмотров события.
     */
    private Long views;
}