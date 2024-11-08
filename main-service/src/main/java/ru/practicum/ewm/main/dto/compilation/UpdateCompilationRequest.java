package ru.practicum.ewm.main.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    private Set<Long> events = new HashSet<>();
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}