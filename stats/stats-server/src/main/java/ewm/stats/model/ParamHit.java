package ewm.stats.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Класс сущности, которая представляет собой информации о том, что на uri конкретного сервиса был отправлен
 * запрос пользователем.
 */
@Data
@Entity
@Table(name = "hits")
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParamHit {
    /**
     * Идентификатор объекта ParamHit.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор сервиса для которого записывается информация.
     */
    @NotBlank
    @Size(max = 64)
    private String app;

    /**
     * URI для которого был осуществлен запрос.
     */
    @NotBlank
    @Size(max = 128)
    private String uri;

    /**
     * IP-адрес пользователя, осуществившего запрос.
     */
    @NotBlank
    @Size(max = 32)
    private String ip;

    /**
     * Дата и время, когда был совершен запрос к эндпоинту.
     */
    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}