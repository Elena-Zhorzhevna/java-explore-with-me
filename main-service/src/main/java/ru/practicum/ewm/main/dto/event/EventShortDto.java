package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.ewm.main.dto.user.UserShortDto;
import ru.practicum.ewm.main.dto.category.CategoryDto;

import java.time.LocalDateTime;

/**
 * Краткая информация о событии.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {

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
    @NotBlank
    private CategoryDto category;

    /**
     * Количество одобренных заявок на участие в данном событии.
     */
    private long confirmedRequests;

    /**
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Пользователь (краткая информация).
     */
    @NotBlank
    private UserShortDto initiator;

    /**
     * Нужно ли оплачивать участие.
     */
    @NotBlank
    @Builder.Default
    private Boolean paid = false;

    /**
     * Заголовок.
     */
    @NotBlank
    private String title;

    /**
     * Количество просмотров события.
     */
    private Long views;
}