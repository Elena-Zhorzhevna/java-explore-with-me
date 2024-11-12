package ewm.stats.service;
import ewm.ParamHitDto;
import ewm.StatDto;
import java.util.List;

/**
 * Интерфейс сервиса статистики.
 */
public interface StatsService {

    void create(ParamHitDto paramHitDto);

    List<StatDto> getStats(String start, String end, List<String> uris, boolean unique);
}