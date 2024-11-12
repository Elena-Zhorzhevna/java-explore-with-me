package ru.practicum.ewm.main.controller.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.RequestParamForEvent;
import ru.practicum.ewm.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.main.model.enums.State;
import ru.practicum.ewm.main.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {
    public final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Поиск событий.
     *
     * @param users      Список id пользователей, чьи события нужно найти.
     * @param states     Список состояний в которых находятся искомые события.
     * @param categories Список id категорий в которых будет вестись поиск.
     * @param rangeStart Дата и время не раньше которых должно произойти событие.
     * @param rangeEnd   Дата и время не позже которых должно произойти событие.
     * @param from       Количество событий, которые нужно пропустить для формирования текущего набора.
     * @param size       Количество событий в наборе.
     * @return Список событий в формате ДТО.
     */
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventFullDto> getAll(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<String> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Получен GET-запрос /admin/events");

        List<State> statesEnum = null;
        if (states != null) {
            statesEnum = states.stream().map(State::from).filter(Objects::nonNull).collect(Collectors.toList());
        }

        RequestParamForEvent param = RequestParamForEvent.builder()
                .users(users)
                .states(statesEnum)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getAll(param);
    }

    /**
     * Редактирование данных любого события и его статуса(отклонение/публикация) администратором.
     *
     * @param eventId     Идентификатор пользователя.
     * @param updateEvent Данные для изменения информации о событии.
     * @return Событие с обновленными данными в формате ДТО.
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Получен PATCH-запрос /admin/events/{} на изменение события.", eventId);
        return eventService.updateByAdmin(eventId, updateEvent);
    }
}