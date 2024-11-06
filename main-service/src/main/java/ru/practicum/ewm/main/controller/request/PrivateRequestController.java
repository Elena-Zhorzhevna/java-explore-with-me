package ru.practicum.ewm.main.controller.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.service.request.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    public PrivateRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("Получен GET-запрос /users/{}/requests", userId);
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        log.info("Получен POST-запрос /users/{}/requests c новым запросом на участие в Event с id = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestsId}/cancel")
    public ParticipationRequestDto update(@PathVariable Long userId, @PathVariable Long requestsId) {
        log.info("Получен PATCH-запрос /users/{}/requests/{requestsId}/cancel" +
                " c отменой запроса id = {}", userId, requestsId);
        return requestService.update(userId, requestsId);
    }
}