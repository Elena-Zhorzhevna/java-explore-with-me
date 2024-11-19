package ru.practicum.ewm.main.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentRequest {
    /**
     * Текст комментария.
     */
    @NotBlank
    @Size(min = 3, max = 2000)
    private String commentText;
}