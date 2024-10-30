package ewm.stats.service;

import ewm.ParamHitDto;
import ewm.StatDto;


import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void create(ParamHitDto paramHitDto);

    List<StatDto> getStats(String start, String end, List<String> uris, boolean unique);

}
