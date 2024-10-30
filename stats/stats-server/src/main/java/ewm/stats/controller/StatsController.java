package ewm.stats.controller;

import ewm.StatDto;
import ewm.stats.model.ParamHit;
import ewm.stats.service.StatsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@RestController
@RequestMapping
public class StatsController {

    @Value("yyyy-MM-dd HH:mm:ss")
    private String dateTimeFormat;
    private final StatsServiceImpl statsService;

    @Autowired
    public StatsController(StatsServiceImpl statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid ParamHit paramHit, HttpServletRequest request) {
        log.info("Запрос к эндпоинту '{}' на добавление статистики {}",
                request.getRequestURI(), paramHit);
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
        return statsService.getStats(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(dateTimeFormat)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(dateTimeFormat)), uris, unique);
    }
}