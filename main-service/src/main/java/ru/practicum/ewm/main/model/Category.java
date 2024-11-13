package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Класс, представляющий категорию.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Идентификатор категории.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя категории.
     */
    @Size(max = 50)
    @Column(name = "name", nullable = false)
    private String name;
}