package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.main.dto.location.LocationDto;

import java.time.LocalDateTime;

/**
 * Данные для изменения информации о событии.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateEventRequest {

    /**
     * Новая аннотация.
     */
    @Length(min = 20, max = 2000)
    private String annotation;
    /**
     * Новая категория.
     */
    private Long category;
    /**
     * Новое описание.
     */
    @Length(min = 20, max = 7000)
    private String description;
    /**
     * Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    /**
     * Широта и долгота места проведения события.
     */
    @Valid
    private LocationDto location;
    /**
     * Новое значение флага о платности мероприятия.
     */
    private Boolean paid;
    /**
     * Новый лимит пользователей.
     */
    @PositiveOrZero
    private Long participantLimit;
    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    private Boolean requestModeration;
    /**
     * Новый заголовок.
     */
    @Size(min = 3, max = 120)
    private String title;
}