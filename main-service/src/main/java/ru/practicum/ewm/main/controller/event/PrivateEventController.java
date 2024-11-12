package ru.practicum.ewm.main.controller.event;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.model.enums.Status;
import ru.practicum.ewm.main.service.event.EventService;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    public final EventService eventService;

    public PrivateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Получение событий, добавленных текущим пользователем.
     *
     * @param userId id текущего пользователя.
     * @param from   Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size   Количество элементов в наборе.
     * @return Список событий в формате ДТО.
     */
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getAll(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос /users{}/events с параметрами: from = {}, size = {}", userId, from, size);

        int page = from / size;
        return eventService.getAllPrivate(userId, page, size);
    }

    /**
     * Получение полной информации о событии, добавленном текущим пользователем.
     *
     * @param userId  Идентификатор пользователя.
     * @param eventId Идентификатор события.
     * @return Полная информация о событии.
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto get(@PathVariable Long userId,
                            @PathVariable Long eventId) {
        log.info("Получен GET-запрос  /users{}/events/{}", userId, eventId);
        return eventService.get(userId, eventId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя.
     *
     * @param userId  Идентификатор пользователя.
     * @param eventId Идентификатор события.
     * @return Список запросов.
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Получен GET-запрос /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequests(userId, eventId);
    }

    /**
     * Добавление нового события.
     *
     * @param userId   Идентификатор пользователя.
     * @param eventDto Добавляемое событие.
     * @return Добавленное событие в формате ДТО.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto eventDto) {
        log.info("Получен POST-запрос /users/{}/events c новым событием: {}", userId, eventDto.getTitle());
        return eventService.create(userId, eventDto);
    }

    /**
     * Изменение события, добавленного текущим пользователем.
     *
     * @param userId   Идентификатор пользователя.
     * @param eventId  Идентификатор события.
     * @param eventDto Данные для изменения информации о событии.
     * @return Событие с обновленной информацией.
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest eventDto) {
        log.info("Получен PATCH-запрос  /users/{}/events/{eventId}" +
                " c обновлённым событием id = {}: {}", userId, eventId, eventDto);
        return eventService.update(userId, eventId, eventDto);
    }

    /**
     * Изменение статуса(подтверждена/отменена) заявок на участие в событии текущего пользователя.
     *
     * @param userId  Идентификатор пользователя.
     * @param eventId Идентификатор события.
     * @param request Новый статус для заявок на участие в событии текущего пользователя.
     * @return Заявка на участие с обновленным статусом.
     */
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Получен PATCH-запрос /users/{}/events/{eventId}/requests" +
                " на обновление статуса события id = {}: {}", userId, eventId, request);
        if (Status.from(request.getStatus()) == null) {
            throw new ConflictException("Статус не подтвержден.");
        }
        return eventService.updateRequestStatus(userId, eventId, request);
    }
}