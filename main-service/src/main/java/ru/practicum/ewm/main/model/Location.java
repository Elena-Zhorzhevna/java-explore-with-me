package ru.practicum.ewm.main.model;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Встраиваемый класс - Широта и долгота места проведения события.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {
    /**
     * Широта проведения события.
     */
    private Float lat;
    /**
     * Долгота проведения события.
     */
    private Float lon;
}