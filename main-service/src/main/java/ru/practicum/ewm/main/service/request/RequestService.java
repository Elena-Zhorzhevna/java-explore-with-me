package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     */
    List<ParticipationRequestDto> getRequests(Long userId);

    /**
     * Добавление запроса от текущего пользователя на участие в событии.
     */
    ParticipationRequestDto create(Long userId, Long eventId);

    /**
     * Отмена своего запроса на участие в событии.
     */
    ParticipationRequestDto update(Long userId, Long requestId);
}
