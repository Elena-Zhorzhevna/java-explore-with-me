package ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamHitDto {
/*    @NonNull
    private Long id;*/
    @NotBlank
    @Size(max = 64)
    private String app;
    @NotBlank
    @Size(max = 128)
    private String uri;
    @NotBlank
    @Size(max = 32)
    private String ip;
    @NotBlank
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}