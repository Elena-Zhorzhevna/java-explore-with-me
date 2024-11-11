package ru.practicum.ewm.main.service.event;

import ewm.ParamHitDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.main.dto.event.*;

import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.EventMapper;
import ru.practicum.ewm.main.mapper.RequestMapper;
import ru.practicum.ewm.main.model.*;

import ru.practicum.ewm.main.model.enums.State;
import ru.practicum.ewm.main.model.enums.StateAdminAction;
import ru.practicum.ewm.main.model.enums.StateUserAction;
import ru.practicum.ewm.main.model.enums.Status;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


import static ru.practicum.ewm.main.model.enums.Status.CONFIRMED;
import static ru.practicum.ewm.main.model.enums.Status.REJECTED;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Value("${ewm.service.name}")
    private String serviceName;

    public EventServiceImpl(EventRepository eventRepository, CategoryRepository categoryRepository,
                            UserRepository userRepository, RequestRepository requestRepository,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
    }

    /**
     * Admin
     */
    @Override
    public List<EventFullDto> getAll(RequestParamForEvent param) {
        if (param.getRangeStart() != null && param.getRangeEnd() != null) {
            if (param.getRangeStart().isAfter(param.getRangeEnd())) {
                throw new ConditionsNotMetException("Диапазон дат неверный: rangeStart не может быть позже rangeEnd.");
            }
        }

        List<Event> events = eventRepository.findAll();

        if (param.getUsers() != null && !param.getUsers().isEmpty()) {
            events = events.stream()
                    .filter(event -> param.getUsers().contains(event.getInitiator().getId()))
                    .collect(Collectors.toList());
        }

        if (param.getStates() != null && !param.getStates().isEmpty()) {
            events = events.stream()
                    .filter(event -> param.getStates().contains(event.getState()))
                    .collect(Collectors.toList());
        }

        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            events = events.stream()
                    .filter(event -> param.getCategories().contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        if (param.getRangeStart() != null) {
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(param.getRangeStart()))
                    .collect(Collectors.toList());
        }

        if (param.getRangeEnd() != null) {
            events = events.stream()
                    .filter(event -> event.getEventDate().isBefore(param.getRangeEnd()))
                    .collect(Collectors.toList());
        }

        int from = param.getFrom();
        int size = param.getSize();
        return events.stream()
                .skip(from)
                .limit(size)
                .map(EventMapper::mapEventToEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        log.info("Начало обработки запроса на обновление события с id = {}", eventId);
        log.info("Полученные данные для обновления: {}", updateEvent);

        checkEventDate(updateEvent.getEventDate());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id = {} не найдено", eventId);
                    return new NotFoundException("Событие с id = " + eventId + " не найдено.");
                });

        log.info("Событие найдено: {}", event);

        if (State.PUBLISHED.equals(event.getState()) &&
                StateAdminAction.PUBLISH_EVENT.equals(updateEvent.getStateAction())) {
            log.error("Невозможно опубликовать событие с id = {}: оно уже опубликовано.", eventId);
            throw new ConflictException("Невозможно опубликовать событие: оно уже опубликовано.");
        }

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
            log.info("Обновлено поле title: {}", updateEvent.getTitle());
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
            log.info("Обновлено поле annotation: {}", updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
            log.info("Обновлено поле description: {}", updateEvent.getDescription());
        }

        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
            log.info("Обновлено поле eventDate: {}", updateEvent.getEventDate());
        }

        if (updateEvent.getPaid() != null && !State.REJECTED.equals(event.getState())) {
            event.setPaid(updateEvent.getPaid());
            log.info("Обновлено поле paid: {}", updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
            log.info("Обновлено поле participantLimit: {}", updateEvent.getParticipantLimit());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
            log.info("Обновлено поле requestModeration: {}", updateEvent.getRequestModeration());
        }

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Не найдена категория с id=%d",
                            updateEvent.getCategory())));
            event.setCategory(category);
            log.info("Обновлено поле category: {}", category.getName());
        }

        if (updateEvent.getLocation() != null) {
            LocationDto locationDto = updateEvent.getLocation();
            event.setLocation(new Location(locationDto.getLat(), locationDto.getLon()));
            log.info("Обновлено поле location: lat = {}, lon = {}", locationDto.getLat(), locationDto.getLon());
        }

        if (State.PUBLISHED.equals(event.getState())) {
            log.error("Невозможно отменить событие с id = {}: оно уже опубликовано", eventId);
            throw new ConflictException("Невозможно отменить событие с id = " + eventId + ": оно уже опубликовано.");
        }

        if (State.REJECTED.equals(event.getState())) {
            log.error("Невозможно изменить событие с id = {}: оно отменено.", eventId);
            throw new ConflictException("Событие с id = " + eventId + " отменено и не может быть изменено.");
        }

        if (updateEvent.getStateAction() != null) {
            log.info("Обрабатываем изменение состояния события: {}", updateEvent.getStateAction());
            if (StateAdminAction.PUBLISH_EVENT.equals(updateEvent.getStateAction())) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                log.info("Статус события изменен на PUBLISHED.");
            } else if (StateAdminAction.REJECT_EVENT.equals(updateEvent.getStateAction())) {
                event.setState(State.REJECTED);
                log.info("Статус события изменен на REJECTED.");
            }
        }

        log.info("Сохраняем обновленное событие...");
        Event updatedEvent = eventRepository.save(event);
        log.info("Событие успешно сохранено с новым состоянием: {}", updatedEvent);

        EventFullDto updatedEventDto = EventMapper.mapEventToEventFullDto(updatedEvent);
        log.info("Обновленное событие преобразовано в DTO: {}", updatedEventDto);

        return updatedEventDto;
    }

    /**
     * Private
     */
    @Override
    public List<EventShortDto> getAllPrivate(Long userId, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Event> eventsList = eventRepository.findAll(pageable).getContent();
        List<EventShortDto> eventShorts = new ArrayList<>(EventMapper.toEventShortDtoList(eventsList));

        log.info("Длина списка событий: {}", eventShorts.size());
        return eventShorts;
    }


    @Override
    public EventFullDto get(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найдено событие с id = %s и userId = %s", eventId, userId)));
        log.info("Получены события: {}", event.getId());
        return EventMapper.mapEventToEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException(
                    String.format("Не найдено событие с id = %s и userId = %s", eventId, userId));
        }
        return RequestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        log.info("Создание события для пользователя с id {} и категории с id {}", userId, eventDto.getCategory());

        checkEventDate(eventDto.getEventDate());

        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден.");
                });

        final Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> {
                    log.error("Категория с id {} не найдена", eventDto.getCategory());
                    return new NotFoundException("Категория с id = " + eventDto.getCategory() + " не найдена.");
                });

        Event newEvent = EventMapper.mapNewEventDtoToEvent(eventDto, initiator, category);
        log.info("Событие успешно замаппировано: {}", newEvent);

        try {
            newEvent = eventRepository.save(newEvent);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Событие успешно сохранено с id {}", newEvent.getId());

        return EventMapper.mapEventToEventFullDto(newEvent);
    }


    @Transactional
    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserDto) {

        Event eventToPatch = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найдено событие с id = %s и userId = %s", eventId, userId)));

        checkEventDate(updateEventUserDto.getEventDate());

        if (updateEventUserDto.getTitle() != null) {
            eventToPatch.setTitle(updateEventUserDto.getTitle());
        }

        if (updateEventUserDto.getAnnotation() != null) {
            eventToPatch.setAnnotation(updateEventUserDto.getAnnotation());
        }

        if (updateEventUserDto.getDescription() != null) {
            eventToPatch.setDescription(updateEventUserDto.getDescription());
        }

        if (updateEventUserDto.getEventDate() != null) {
            eventToPatch.setEventDate(updateEventUserDto.getEventDate());
        }

        if (updateEventUserDto.getPaid() != null) {
            eventToPatch.setPaid(updateEventUserDto.getPaid());
        }

        if (updateEventUserDto.getParticipantLimit() != null) {
            eventToPatch.setParticipantLimit(updateEventUserDto.getParticipantLimit());
        }

        if (updateEventUserDto.getRequestModeration() != null) {
            eventToPatch.setRequestModeration(updateEventUserDto.getRequestModeration());
        }

        if (updateEventUserDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Не найдена категория с id=%d",
                            updateEventUserDto.getCategory())));
            eventToPatch.setCategory(category);
        }

        if (updateEventUserDto.getLocation() != null) {
            LocationDto locationDto = updateEventUserDto.getLocation();
            eventToPatch.setLocation(new Location(locationDto.getLat(), locationDto.getLon()));
        }

        if (eventToPatch.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не должно быть опубликовано.");
        }

        if (updateEventUserDto.getStateAction() != null) {
            switch (updateEventUserDto.getStateAction()) {
                case CANCEL_REVIEW:
                    eventToPatch.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventToPatch.setState(State.PENDING);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(eventToPatch);

        log.info("Обновлено событие: {}", updatedEvent.getTitle());
        return EventMapper.mapEventToEventFullDto(updatedEvent);
    }


    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<Long> requestIds = request.getRequestIds();
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(requestIds);

        String status = request.getStatus();

        if (status.equals(REJECTED.toString())) {
            boolean isConfirmedRequestExists = requests.stream()
                    .anyMatch(r -> r.getStatus().equals(CONFIRMED));
            if (isConfirmedRequestExists) {
                throw new ConflictException("Нельзя отклонить подтвержденные заявки.");
            }

            rejectedRequests = requests.stream()
                    .peek(r -> r.setStatus(REJECTED))
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());

            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найдено событие с id = %s и userId = %s", eventId, userId)));

        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getConfirmedRequests();
        long availableParticipants = participantLimit - approvedRequests;
        long potentialParticipants = requestIds.size();

        if (participantLimit > 0 && approvedRequests + potentialParticipants > participantLimit) {
            throw new ConflictException(String.format("Количество участников события с id=%d достигло предела.",
                    eventId));
        }

        if (status.equals(CONFIRMED.toString())) {

            if (participantLimit == 0L || (availableParticipants >= potentialParticipants &&
                    !event.getRequestModeration())) {
                confirmedRequests = requests.stream()
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с id = %d уже подтвержден.",
                                        r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                event.setConfirmedRequests(approvedRequests + potentialParticipants);
            } else {

                confirmedRequests = requests.stream()
                        .limit(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с id = %d уже подтвержден",
                                        r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());

                rejectedRequests = requests.stream()
                        .skip(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(REJECTED)) {
                                r.setStatus(REJECTED);
                            } else {
                                throw new ConflictException(String.format("Запрос с идентификатором=%d уже отклонен",
                                        r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());

                event.setConfirmedRequests(participantLimit);
            }
        }
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        requestRepository.flush();
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    /**
     * Public
     */
    @Transactional
    @Override
    public List<EventShortDto> getAllPublic(RequestPublicParamForEvent param) {

        EventSearchParams searchParams = EventSearchParams.builder()
                .text(param.getText())
                .categories(param.getCategories())
                .rangeStart(param.getRangeStart())
                .rangeEnd(param.getRangeEnd())
                .paid(param.getPaid())
                .build();

        Set<Event> events = new HashSet<>(findEventsBySearchParams(searchParams));

        List<Event> eventsList = new ArrayList<>(events);

        int fromIndex = param.getFrom();
        int toIndex = Math.min(fromIndex + param.getSize(), eventsList.size());

        List<Event> paginatedEventsList = eventsList.subList(fromIndex, toIndex);

        List<EventShortDto> eventShortsList = EventMapper.toEventShortDtoList(paginatedEventsList);
        log.info("Получено {} событий", eventShortsList.size());

        saveEndpointHit(param.getRequest());
        return eventShortsList;
    }

    @Override
    public EventFullDto getById(Long id, HttpServletRequest request) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %s", id)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id=%d не опубликовано.", id));
        }

        saveEndpointHit(request);
        log.info("Получено событие с id = {}", event.getId());
        long views = (event.getViews() != null) ? event.getViews() : 0L;
        event.setViews(views + 1);
        eventRepository.save(event);
        return EventMapper.mapEventToEventFullDto(event);
    }

    public List<Event> findEventsBySearchParams(EventSearchParams searchParams) {
        if (searchParams.getRangeStart() != null && searchParams.getRangeEnd() != null) {
            if (searchParams.getRangeStart().isAfter(searchParams.getRangeEnd())) {
                throw new ConditionsNotMetException("Диапазон дат неверный: rangeStart не может быть позже rangeEnd");
            }
        }
        List<Event> events = eventRepository.findAll();

        log.debug("Изначально получено {} событий", events.size());

        if (searchParams.getText() != null && !searchParams.getText().isEmpty()) {
            events = events.stream()
                    .filter(event -> event.getAnnotation().contains(searchParams.getText())
                            || event.getDescription().contains(searchParams.getText()))
                    .collect(Collectors.toList());
            log.debug("После фильтрации по тексту получено {} событий", events.size());
        }

        if (searchParams.getCategories() != null && !searchParams.getCategories().isEmpty()) {
            events = events.stream()
                    .filter(event -> searchParams.getCategories().contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
            log.debug("После фильтрации по категориям получено {} событий", events.size());
        }

        if (searchParams.getPaid() != null) {
            events = events.stream()
                    .filter(event -> event.getPaid().equals(searchParams.getPaid()))
                    .collect(Collectors.toList());
            log.debug("После фильтрации по оплате получено {} событий", events.size());
        }

        if (searchParams.getRangeStart() != null || searchParams.getRangeEnd() != null) {
            LocalDateTime rangeStart = searchParams.getRangeStart() != null ? searchParams.getRangeStart() : LocalDateTime.MIN;
            LocalDateTime rangeEnd = searchParams.getRangeEnd() != null ? searchParams.getRangeEnd() : LocalDateTime.MAX;

            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart) &&
                            event.getEventDate().isBefore(rangeEnd))
                    .collect(Collectors.toList());
            log.debug("После фильтрации по датам получено {} событий", events.size());
        }

        return events;
    }

    /**
     * Создание пагинации на основе параметров запроса.
     */
    private Pageable createPageable(String sort, int from, int size) {
        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
    }

    /**
     * Сохранение статистики по запросу.
     */
    private void saveEndpointHit(HttpServletRequest request) {
        ParamHitDto paramHit = ParamHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(serviceName)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.hit(paramHit);
    }

    /**
     * Проверка дат.
     */
    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Поле: eventDate. Ошибка: дата и время запланированного мероприятия " +
                    "не могут быть ранее, чем через 2 часа после текущего момента. Значение: " + eventDate);
        }
    }
}