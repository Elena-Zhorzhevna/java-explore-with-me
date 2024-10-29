package ewm.stats.service;

import ewm.StatDto;
import ewm.stats.model.ParamHit;
import ewm.stats.repository.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@Validated
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Autowired
    public StatsServiceImpl(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(ParamHit paramHit) {
        Objects.requireNonNull(paramHit, "Параметр не может быть равен нулю.");
        repository.save(paramHit);
        log.info("Просмотр сохранен.");
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Objects.requireNonNull(start, "Время начала сбора данных статистики должно быть указано.");
        Objects.requireNonNull(end, "Время окончания сбора данных статистики должно быть указано.");

        if (uris.isEmpty()) {
            if (unique) {
                return repository.getUniqueHits(start, end);
                //log.info("Получена статистика уникальных просмотров.");
            } else {
                return repository.getHits(start, end);
                //log.info("Получена статистика просмотров.");
            }

        } else {
            if (unique) {
                return repository.getUniqueHitsByUris(start, end, uris);
                //log.info("Получена статистика уникальных просмотров для заданных uri");
            } else {
                return repository.getHitsByUris(start, end, uris);
                //log.info("Получена статистика просмотров для заданных uri");
            }
        }
    }
}

/*
    @Override
    public List<ViewStats> getViewStats(final LocalDateTime start, final LocalDateTime end, final List<String> uris,
            final boolean unique) {
        Objects.requireNonNull(start, "Date and time to gather stats from cannot be null");
        Objects.requireNonNull(end, "Date and time to gather stats to cannot be null");
        if (CollectionUtils.isEmpty(uris)) {
            if (unique) {
                return repository.getUniqueHits(start, end);
            } else {
                return repository.getHits(start, end);
            }
        } else {
            if (unique) {
                return repository.getUniqueHits(start, end, uris);
            } else {
                return repository.getHits(start, end, uris);
            }
        }
    }
 */