package ru.practicum.ewm.main.service.event;

import ewm.ParamHitDto;
import jakarta.servlet.http.HttpServletRequest;
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

import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.EventMapper;
import ru.practicum.ewm.main.mapper.RequestMapper;
import ru.practicum.ewm.main.model.*;

import ru.practicum.ewm.main.model.enums.State;
import ru.practicum.ewm.main.model.enums.StateAdminAction;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        // Получаем все события
        List<Event> events = eventRepository.findAll();

        // Применяем фильтрацию
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
        // Получаем событие по ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено."));

        // Копируем все non-null поля из updateEvent в event
        copyNonNullProperties(updateEvent, event);

        // Обрабатываем поле категории, если оно указано
        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена."));
            event.setCategory(category);
        }

        // Обрабатываем поле Location: если оно задано, создаем новый объект Location
        if (updateEvent.getLocation() != null) {
            event.setLocation(new Location(updateEvent.getLocation().getLat(), updateEvent.getLocation().getLon()));
        } else {
            event.setLocation(new Location(null, null)); // Если Location не задано, сбрасываем его
        }

        // Если передан статус, обновляем его
        if (updateEvent.getStateAction() != null) {
            // Если действие PUBLISH_EVENT, меняем статус на PUBLISHED
            if (StateAdminAction.PUBLISH_EVENT.equals(updateEvent.getStateAction())) {
                event.setState(State.PUBLISHED);
            }
            // Если действие REJECT_EVENT, меняем статус на REJECTED
            else if (StateAdminAction.REJECT_EVENT.equals(updateEvent.getStateAction())) {
                event.setState(State.REJECTED);
            }
        }

        // Сохраняем обновленное событие
        Event updatedEvent = eventRepository.save(event);

        // Возвращаем DTO обновленного события
        return EventMapper.mapEventToEventFullDto(updatedEvent);
    }

    /**
     * Private
     */

/*    @Override
    public Set<EventShortDto> getAllPrivate(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        Set<EventShortDto> eventShorts = EventMapper.toEventShortDtoList(eventRepository.findAll(page).toSet());
        log.info("Длина списка событий: {}", eventShorts.size());
        return eventShorts;
    }*/

    @Override
    public Set<EventShortDto> getAllPrivate(Long userId, Integer from, Integer size) {
        // Создаем пагинацию
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        // Получаем все события из репозитория с учетом пагинации
        List<Event> eventsList = eventRepository.findAll(page).getContent();

        // Преобразуем List<Event> в Set<EventShortDto>
        Set<EventShortDto> eventShorts = new HashSet<>(EventMapper.toEventShortDtoList(eventsList));

        // Логируем длину списка
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

        // Проверка пользователя
        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден.");
                });

        // Проверка категории
        final Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> {
                    log.error("Категория с id {} не найдена", eventDto.getCategory());
                    return new NotFoundException("Категория с id = " + eventDto.getCategory() + " не найдена.");
                });

        // Маппинг события
        final Event newEvent = EventMapper.mapNewEventDtoToEvent(eventDto, initiator, category);
        log.info("Событие успешно замаппировано: {}", newEvent);

        // Сохранение события
        final Event savedEvent = eventRepository.save(newEvent);
        log.info("Событие успешно сохранено с id {}", savedEvent.getId());

        return EventMapper.mapEventToEventFullDto(savedEvent);
    }

/*    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        checkEventDate(eventDto.getEventDate());
        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id" + userId + " не найден."));
        final Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + eventDto.getCategory() +
                        " не найдена."));
        final Event newEvent = EventMapper.mapNewEventDtoToEvent(eventDto, initiator, category);
        final Event savedEvent = eventRepository.save(newEvent);
        return EventMapper.mapEventToEventFullDto(savedEvent);
    }*/

    @Transactional
    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        Event eventToUpdate = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найдено событие с id = %s и userId = %s", eventId, userId)));

        // Преобразуем DTO в объект Event для обновления
        Event eventUpdate = EventMapper.mapUpdateEventUserRequestToEvent(eventDto);
        checkEventDate(eventUpdate.getEventDate());

        // Копируем все non-null поля из eventDto в eventUpdate
        copyNonNullProperties(eventDto, eventUpdate);

        // Если передан category, обновляем его
        if (eventDto.getCategory() != null) {
            eventUpdate.setCategory(categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Не найдена категория с id=%d",
                            eventDto.getCategory()))));
        }

        // Проверка на состояние события
        if (eventToUpdate.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не должно быть опубликовано.");
        }

        // Если action пользователя CANCEL_REVIEW или SEND_TO_REVIEW, обновляем состояние события
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    eventUpdate.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventUpdate.setState(State.PENDING);
                    break;
            }
        }

        // Сохраняем обновленное событие
        Event updatedEvent = eventRepository.save(eventUpdate);

        // Возвращаем DTO обновленного события
        log.info("Обновлено событие: {}", updatedEvent.getTitle());
        return EventMapper.mapEventToEventFullDto(updatedEvent);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        // Получаем список id заявок из запроса
        List<Long> requestIds = request.getRequestIds();
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(requestIds);

        // Получаем статус обновления
        String status = request.getStatus();

        // Если статус REJECTED, проверяем, можно ли отклонить заявку
        if (status.equals(REJECTED.toString())) {
            boolean isConfirmedRequestExists = requests.stream()
                    .anyMatch(r -> r.getStatus().equals(CONFIRMED));
            if (isConfirmedRequestExists) {
                throw new ConflictException("Нельзя отклонить подтвержденные заявки.");
            }

            // Отклоняем заявки
            rejectedRequests = requests.stream()
                    .peek(r -> r.setStatus(REJECTED))
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());

            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        // Получаем событие по id и проверяем, принадлежит ли оно пользователю
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не найдено событие с id = %s и userId = %s", eventId, userId)));

        Long participantLimit = event.getParticipantLimit();  // Лимит участников
        Long approvedRequests = event.getConfirmedRequests();  // Количество подтвержденных заявок
        long availableParticipants = participantLimit - approvedRequests;  // Доступные места для участников
        long potentialParticipants = requestIds.size();  // Количество новых заявок

        // Проверяем, не достигнут ли лимит участников
        if (participantLimit > 0 && approvedRequests + potentialParticipants > participantLimit) {
            throw new ConflictException(String.format("Количество участников события с id=%d достигло предела.",
                    eventId));
        }

        // Если статус CONFIRMED, подтверждаем заявки
        if (status.equals(CONFIRMED.toString())) {
            // Проверяем, если заявок меньше или доступных мест достаточно
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
                // Если места недостаточно, подтверждаем только доступные заявки и отклоняем остальные
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

                event.setConfirmedRequests(participantLimit);  // Устанавливаем подтвержденные заявки равными лимиту
            }
        }

        // Сохраняем изменения в базе данных
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        requestRepository.flush();  // Обновляем данные в репозитории

        // Возвращаем результат
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    /**
     * Public
     */
    @Transactional
    @Override
    public Set<EventShortDto> getAllPublic(RequestPublicParamForEvent param) {
        // Создаем критерии для поиска событий
        EventSearchParams searchParams = EventSearchParams.builder()
                .text(param.getText())
                .categories(param.getCategories())
                .rangeStart(param.getRangeStart())
                .rangeEnd(param.getRangeEnd())
                .paid(param.getPaid())
                .build();

        // Получаем все события через метод findEventsBySearchParams
        Set<Event> events = new HashSet<>(findEventsBySearchParams(searchParams));

        // Преобразуем Set в List, чтобы можно было использовать subList
        List<Event> eventsList = new ArrayList<>(events);

        // Пагинация: создаем подмножество списка по размеру и смещению
        int fromIndex = param.getFrom();
        int toIndex = Math.min(fromIndex + param.getSize(), eventsList.size());

        // Создаем подсписок (пагинированные события)
        List<Event> paginatedEvents = eventsList.subList(fromIndex, toIndex);

        // Преобразуем события в DTO
        List<EventShortDto> eventShortsList = EventMapper.toEventShortDtoList(paginatedEvents);

        // Логируем количество найденных событий
        log.info("Получено {} событий", eventShortsList.size());

        // Сохраняем статистику запроса
        saveEndpointHit(param.getRequest());

        // Преобразуем List<EventShortDto> в Set<EventShortDto> и возвращаем
        return new HashSet<>(eventShortsList);  // Преобразуем в Set
    }

    // Получаем подробную информацию о событии по его ID
    @Override
    public EventFullDto getById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found with id = %s", id)));

        // Если событие не опубликовано, выбрасываем исключение
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id=%d is not published", id));
        }

        // Сохраняем статистику запроса
        saveEndpointHit(request);

        // Логируем получение события
        log.info("Получено событие с id = {}", event.getId());

        // Увеличиваем счетчик просмотров
        event.setViews(event.getViews() + 1);
        eventRepository.flush(); // Сохраняем изменения

        // Возвращаем DTO с полными данными о событии
        return EventMapper.mapEventToEventFullDto(event);
    }

    public List<Event> findEventsBySearchParams(EventSearchParams searchParams) {

        // Выполняем поиск по тексту в аннотации и описании
        List<Event> events = eventRepository.findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchParams.getText(), searchParams.getText()
        );

        // Фильтруем по категориям, если они есть
        if (searchParams.getCategories() != null && !searchParams.getCategories().isEmpty()) {
            events = events.stream()
                    .filter(event -> searchParams.getCategories().contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        // Фильтруем по оплате, если параметр не null
        if (searchParams.getPaid() != null) {
            events = events.stream()
                    .filter(event -> event.getPaid().equals(searchParams.getPaid()))
                    .collect(Collectors.toList());
        }

        // Фильтруем по датам, если параметры диапазона дат заданы
        if (searchParams.getRangeStart() != null || searchParams.getRangeEnd() != null) {
            LocalDateTime rangeStart = searchParams.getRangeStart() != null ?
                    searchParams.getRangeStart() : LocalDateTime.MIN;
            LocalDateTime rangeEnd = searchParams.getRangeEnd() != null ?
                    searchParams.getRangeEnd() : LocalDateTime.MAX;
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart) &&
                            event.getEventDate().isBefore(rangeEnd))
                    .collect(Collectors.toList());
        }

        return events;
    }

    /**
     * Вспомогательные методы.
     */
    private <T> void copyNonNullProperties(T source, Event target) {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);  // Делаем поле доступным
                Object value = field.get(source);  // Получаем значение поля

                // Если поле не null, копируем его в целевой объект
                if (value != null) {
                    switch (field.getName()) {
                        case "title":
                            target.setTitle((String) value);
                            break;
                        case "annotation":
                            target.setAnnotation((String) value);
                            break;
                        case "description":
                            target.setDescription((String) value);
                            break;
                        case "eventDate":
                            target.setEventDate((LocalDateTime) value);
                            break;
                        case "paid":
                            target.setPaid((Boolean) value);
                            break;
                        case "participantLimit":
                            target.setParticipantLimit((Long) value);
                            break;
                        case "requestModeration":
                            target.setRequestModeration((Boolean) value);
                            break;
                        default:
                            // Добавьте обработку других полей по мере необходимости
                            break;
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Ошибка при копировании поля: " + field.getName(), e);
            }
        }
    }

    // Создание пагинации на основе параметров запроса
    private Pageable createPageable(String sort, int from, int size) {
        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
    }

    // Сохранение статистики по запросу
    private void saveEndpointHit(HttpServletRequest request) {
        ParamHitDto paramHit = ParamHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(serviceName)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.hit(paramHit); // Отправляем статистику
    }

/*    private Pageable createPageable(String sort, int from, int size) {
        // Пагинация: откуда начинаем (from) и сколько элементов (size)
        if (sort == null || sort.equalsIgnoreCase("EVENTDATE")) {
            // Сортируем по дате события
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            // Сортируем по количеству просмотров
            return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        // В случае если сортировка не указана или некорректна, дефолтная сортировка по дате
        return PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
    }*/


    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Поле: eventDate. Ошибка: дата и время запланированного мероприятия не могут " +
                    "быть ранее, чем через 2 часа после текущего момента. Значение: " + eventDate);
        }
    }
}