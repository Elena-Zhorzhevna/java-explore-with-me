package ru.practicum.ewm.main.dto.location;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    /**
     * Широта места проведения события.
     */
    @NotNull
    private Float lat;
    /**
     * Долгота места проведения события.
     */
    @NotNull
    private Float lon;
}