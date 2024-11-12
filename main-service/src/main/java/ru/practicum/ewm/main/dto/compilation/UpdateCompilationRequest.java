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
    @Builder.Default
    private Set<Long> events = new HashSet<>();
    @Builder.Default
    private Boolean pinned = false;
    @Size(min = 1, max = 50)
    private String title;
}