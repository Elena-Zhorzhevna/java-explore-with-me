package ru.practicum.ewm.main.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventShortDto;
import ru.practicum.ewm.main.dto.event.RequestPublicParamForEvent;
import ru.practicum.ewm.main.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Получение всех опубликованных событий с возможностью фильтрации.
     *
     * @param text          Текст для поиска в содержимом аннотации и подробном описании события.
     * @param categories    Список идентификаторов категорий в которых будет вестись поиск.
     * @param paid          Поиск только платных/бесплатных событий.
     * @param rangeStart    Дата и время не раньше которых должно произойти событие.
     * @param rangeEnd      Дата и время не позже которых должно произойти событие.
     * @param onlyAvailable Только события у которых не исчерпан лимит запросов на участие.
     * @param sort          Вариант сортировки: по дате события или по количеству просмотров.
     * @param from          Количество событий, которые нужно пропустить для формирования текущего набора.
     * @param size          Количество событий в наборе.
     * @return Список событий в формате ДТО.
     */
    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "eventDate") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {

        log.info("Получен запрос GET /events c параметрами: text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        RequestPublicParamForEvent param = RequestPublicParamForEvent.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .request(request)
                .build();

        return eventService.getAllPublic(param);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору.
     *
     * @param id Идентификатор события.
     * @return Подробная информация о событии.
     */
    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен запрос GET /events/{}", id);

        return eventService.getById(id, request);
    }
}