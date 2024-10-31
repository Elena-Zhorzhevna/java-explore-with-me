package ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Класс представляет обект ParamHit в формате ДТО.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParamHitDto {

    @NotBlank
    @Size(max = 64)
    private String app;
    @NotBlank
    @Size(max = 128)
    private String uri;
    @NotBlank
    @Size(max = 32)
    private String ip;
    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}