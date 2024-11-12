package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;

/**
 * Заявка на участие в событии.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class ParticipationRequest {

    /**
     * Идентификатор на участие в событии.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Событие.
     */
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Пользователь, отправивший заявку.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * Время и дата создания заявки.
     */
    @Column
    private LocalDateTime created;

    /**
     * Статус заявки.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
}