package ewm.stats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParamHit {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @FutureOrPresent
    private LocalDateTime timestamp;
}
