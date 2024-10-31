package ewm.stats.controller;

import ewm.ParamHitDto;
import ewm.StatDto;
import ewm.stats.service.StatsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping
public class StatsController {

    private final StatsServiceImpl statsService;

    @Autowired
    public StatsController(StatsServiceImpl statsService) {
        this.statsService = statsService;
    }

    /**
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     * Название сервиса, uri и ip пользователя указаны в теле запроса.
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid ParamHitDto paramHitDto, HttpServletRequest request) {
        log.info("Запрос к эндпоинту '{}' на добавление статистики {}",
                request.getRequestURI(), paramHitDto);
        statsService.create(paramHitDto);
    }

    /**
     * Получение статистики по посещениям.
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatDto> stats(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String start,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique,
            HttpServletRequest request) {

        log.info("Запрос на получение статистики к эндпоинту '{}'", request.getRequestURI());

        return statsService.getStats(start, end, uris, unique);
    }
}