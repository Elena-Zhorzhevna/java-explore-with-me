package ru.practicum.ewm.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;
import java.util.Set;

public interface EventService {
    /**
     * Admin
     */

    List<EventFullDto> getAll(RequestParamForEvent param);

    //EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest eventDto);

    /**
     * Private
     */

    Set<EventShortDto> getAllPrivate(Long userId, Integer from, Integer size);

    EventFullDto get(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    /**
     * Public
     */
    Set<EventShortDto> getAllPublic(RequestPublicParamForEvent param);

    EventFullDto getById(Long id, HttpServletRequest request);
}
