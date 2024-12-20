package ru.practicum.ewm.main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.model.enums.State;

import java.time.LocalDateTime;

/**
 * Класс, описывающий событие.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    /**
     * Идентификатор события.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое описание события.
     */
    @Column(name = "annotation", nullable = false)
    private String annotation;

    /**
     * Идентификатор категории, к которой относится событие.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Количество одобренных заявок на участие в данном событии.
     */
    @Column(name = "confirmed_requests")
    private long confirmedRequests;

    /**
     * Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @Column(name = "created_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Полное описание события.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Идентификатор пользователя.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    /**
     * Широта и долгота места проведения событий.
     */
    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "lat", column = @Column(name = "lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "lon"))
    })
    private Location location;

    /**
     * Нужно ли оплачивать участие.
     */
    @Builder.Default
    @Column(name = "paid", nullable = false)
    private Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
     */
    @Column(name = "participant_limit")
    @Builder.Default
    private Long participantLimit = 0L;

    /**
     * Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss").
     */
    @Column(name = "published_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    /**
     * Нужна ли пре-модерация заявок на участие.
     */
    @Column(name = "request_moderation")
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Список состояний жизненного цикла события.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    /**
     * Заголовок.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Количество просмотров события.
     */
    @Column(name = "views")
    private Long views;
}