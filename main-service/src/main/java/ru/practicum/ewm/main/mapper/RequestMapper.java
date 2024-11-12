package ru.practicum.ewm.main.mapper;

import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

public static ParticipationRequest mapToRequest(Event event, User requester) {
    Status status = event.getParticipantLimit() == 0 || !event.getRequestModeration() ? Status.CONFIRMED : Status.PENDING;

    return ParticipationRequest.builder()
            .requester(requester)
            .event(event)
            .created(LocalDateTime.now())
            .status(status)
            .build();
}

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}