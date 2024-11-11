package ru.practicum.ewm.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    /**
     * Admin
     */

    List<EventFullDto> getAll(RequestParamForEvent param);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto);

    /**
     * Private
     */

    List<EventShortDto> getAllPrivate(Long userId, Integer from, Integer size);

    EventFullDto get(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    /**
     * Public
     */
    List<EventShortDto> getAllPublic(RequestPublicParamForEvent param);

    EventFullDto getById(Long id, HttpServletRequest request);
}
