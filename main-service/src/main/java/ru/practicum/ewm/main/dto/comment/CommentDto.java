package ru.practicum.ewm.main.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    /**
     * Идентификатор комментария.
     */
    private Long id;

    /**
     * Текст комментария.
     */
    private String commentText;

    /**
     * Идентификатор пользователя, оставившего комментарий.
     */
    @JsonProperty("owner")
    private Long ownerId;

    /**
     * Идентификатор события, к которому оставлен комментарий.
     */
    @JsonProperty("event")
    private Long eventId;

    /**
     * Дата и время создания комментария.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    /**
     * Статус комментария.
     */
    private Status status;
}