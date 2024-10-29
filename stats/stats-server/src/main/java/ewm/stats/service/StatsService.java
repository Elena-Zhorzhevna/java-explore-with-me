package ewm.stats.service;

import ewm.StatDto;
import ewm.stats.model.ParamHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void create(ParamHit paramHit);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
