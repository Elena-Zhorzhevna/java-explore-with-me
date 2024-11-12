package ru.practicum.ewm.main.mapper;

import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.State;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    public static Event mapNewEventDtoToEvent(NewEventDto dto, User user, Category category) {

        if (dto == null) {
            throw new IllegalArgumentException("NewEventDto не может быть null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null");
        }

        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                .paid(dto.getPaid())
                .initiator(user)
                .category(category)
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .state(State.PENDING)
                .views(0L)
                .title(dto.getTitle())
                .build();
    }

    public static Event mapUpdateEventAdminRequestToEvent(UpdateEventAdminRequest dto) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static Event mapUpdateEventUserRequestToEvent(UpdateEventUserRequest dto) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .paid(dto.getPaid())
                .location(dto.getLocation() != null ? new Location(dto.getLocation().getLat(),
                        dto.getLocation().getLon()) : null)
                .participantLimit(dto.getParticipantLimit())
                .title(dto.getTitle())
                .build();
    }

    public static EventFullDto mapEventToEventFullDto(Event event) {

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto mapToEventShortDto(Event event) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> toEventShortDtoList(List<Event> events) {

        if (events == null) {
            return Collections.emptyList();
        }

        return events.stream().map(EventMapper::mapToEventShortDto).collect(Collectors.toList());
    }
}