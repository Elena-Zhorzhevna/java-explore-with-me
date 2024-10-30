package ewm.stats.service;

import ewm.ParamHitDto;
import ewm.StatDto;
import ewm.stats.mapper.ParamHitMapper;
import ewm.stats.repository.StatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@Validated
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository repository;

    @Autowired
    public StatsServiceImpl(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void create(ParamHitDto paramHitDto) {
        Objects.requireNonNull(paramHitDto, "Параметр не может быть равен нулю.");
        repository.save(ParamHitMapper.toParamHit(paramHitDto));
        log.info("Просмотр сохранен.");
    }

    @Override
    public List<StatDto> getStats(String start, String end, List<String> uris, boolean unique) {
        Objects.requireNonNull(start, "Время начала сбора данных статистики должно быть указано.");
        Objects.requireNonNull(end, "Время окончания сбора данных статистики должно быть указано.");

        final LocalDateTime startTime = decodeTime(start);
        final LocalDateTime endTime = decodeTime(end);

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getUniqueHits(startTime, endTime);
                //log.info("Получена статистика уникальных просмотров.");
            } else {
                return repository.getHits(startTime, endTime);
                //log.info("Получена статистика просмотров.");
            }

        } else {
            if (unique) {
                return repository.getUniqueHitsByUris(startTime, endTime, uris);
                //log.info("Получена статистика уникальных просмотров для заданных uri");
            } else {
                return repository.getHitsByUris(startTime, endTime, uris);
                //log.info("Получена статистика просмотров для заданных uri");
            }
        }
    }

    private LocalDateTime decodeTime(String time) {
        return LocalDateTime.parse(URLDecoder.decode(time, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
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