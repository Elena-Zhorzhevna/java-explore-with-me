package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequests(Long userId);
    ParticipationRequestDto create(Long userId, Long eventId);
    ParticipationRequestDto update(Long userId, Long requestId);
}
