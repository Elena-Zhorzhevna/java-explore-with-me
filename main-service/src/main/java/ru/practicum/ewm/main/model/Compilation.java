package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий подборку событий.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "compilations")
public class Compilation {

    /**
     * Идентификатор подборки событий.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заголовок подборки.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Закреплена ли подборка на главной странице сайта.
     */
    @Column(name = "pinned")
    @Builder.Default
    private boolean pinned = false;

    /**
     * Список событий, входящих в подборку.
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_compilation",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events = new HashSet<>();
}