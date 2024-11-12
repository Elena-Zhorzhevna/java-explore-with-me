package ewm.stats.service;

import ewm.ParamHitDto;
import ewm.StatDto;
import ewm.stats.exception.InvalidTimeRangeException;
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

    /**
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     */
    @Override
    @Transactional
    public void create(ParamHitDto paramHitDto) {
        Objects.requireNonNull(paramHitDto, "Параметр не может быть равен нулю.");
        repository.save(ParamHitMapper.toParamHit(paramHitDto));
        log.info("Просмотр сохранен.");
    }

    /**
     * Получение статистики по посещениям.
     *
     * @param start  Дата и время начала диапазона, за который нужно выгрузить статистику.
     * @param end    Дата и время конца диапазона, за который нужно выгрузить статистику.
     * @param uris   Список uri для которых нужно выгрузить статистику.
     * @param unique Список uri для которых нужно выгрузить статистику.
     */
    @Override
    public List<StatDto> getStats(String start, String end, List<String> uris, boolean unique) {

        if (start == null || start.isEmpty()) {
            throw new InvalidTimeRangeException("Параметр start обязателен.");
        }

        if (end == null || end.isEmpty()) {
            throw new InvalidTimeRangeException("Параметр end обязателен.");
        }

        final LocalDateTime startTime = decodeTime(start);
        final LocalDateTime endTime = decodeTime(end);

        checkTime(startTime, endTime);

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getUniqueHits(startTime, endTime);

            } else {
                return repository.getHits(startTime, endTime);
            }

        } else {
            if (unique) {
                return repository.getUniqueHitsByUris(startTime, endTime, uris);

            } else {
                return repository.getHitsByUris(startTime, endTime, uris);
            }
        }
    }

    /**
     * Метод для декодирования строки времени из URL-кодировки.
     */
    private LocalDateTime decodeTime(String time) {
        return LocalDateTime.parse(URLDecoder.decode(time, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
    }

    /**
     * Метод для проверки, что время начала раньше времени окончания.
     */
    private void checkTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidTimeRangeException("Время начала не может быть позже времени окончания.");
        }
    }
}