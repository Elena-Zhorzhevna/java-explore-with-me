package ru.practicum.ewm.main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;

/**
 * Класс, представляющий комментарий.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    /**
     * Идентификатор комментария.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, оставивший комментарий.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Событие, к которому оставлен комментарий.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Текст комментария.
     */
    @Column(name = "comment_text")
    private String commentText;

    /**
     * Дата и время создания комментария.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created")
    LocalDateTime created;

    /**
     * Статус комментария.
     */
    @Column
    @Enumerated(EnumType.STRING)
    Status status;
}