package ewm;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatDto {
    private String app;
    private String uri;
    private Long hits;
}
