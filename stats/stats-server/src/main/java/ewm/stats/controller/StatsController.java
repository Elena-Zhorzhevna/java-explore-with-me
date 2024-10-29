package ewm.stats.controller;

import ewm.StatDto;
import ewm.stats.model.ParamHit;
import ewm.stats.service.StatsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping
public class StatsController {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsServiceImpl statsService;

    @Autowired
    public StatsController(StatsServiceImpl statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid ParamHit paramHit, HttpServletRequest request) {
        log.info("Запрос к эндпоинту '{}' на добавление статистики {}", request.getRequestURI(), paramHit);
        statsService.create(paramHit);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatDto> stats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique,
            HttpServletRequest request) {

        log.info("Запрос на получение статистики к эндпоинту '{}'", request.getRequestURI());
        return statsService.getStats(LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                LocalDateTime.parse(end, DATE_TIME_FORMATTER), uris, unique);
    }
}