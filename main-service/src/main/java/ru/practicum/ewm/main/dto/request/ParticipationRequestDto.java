package ru.practicum.ewm.main.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    /**
     * Идентификатор заявки на участие в событии.
     */
    private Long id;
    /**
     * Дата и время создания заявки.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    /**
     * Идентификатор события.
     */
    private Long event;
    /**
     * Идентификатор пользователя, отправившего заявку.
     */
    private Long requester;
    /**
     * Статус заявки.
     */
    private Status status;
}