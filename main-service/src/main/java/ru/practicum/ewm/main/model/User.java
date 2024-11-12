package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    /**
     * Идентификатор пользователя.
     */
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Электронная почта пользователя.
     */
    @Column(name = "email", nullable = false)
    private String email;
}