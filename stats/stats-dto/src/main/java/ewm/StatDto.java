package ewm;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatDto {
    private String app;
    private String uri;
    private Long hits;
}
