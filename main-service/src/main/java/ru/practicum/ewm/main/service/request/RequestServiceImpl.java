package ru.practicum.ewm.main.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.RequestMapper;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.ParticipationRequest;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.State;
import ru.practicum.ewm.main.model.enums.Status;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    public RequestServiceImpl(UserRepository userRepository, EventRepository eventRepository,
                              RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях.
     *
     * @param userId Идентификатор пользователя.
     * @return Список запросов на участие в формате Дто.
     */
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (userRepository.existsById(userId)) {
            return RequestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
        } else {
            throw new NotFoundException(String.format("Не найден пользователь с id = %s", userId));
        }
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии.
     *
     * @param userId  Идентификатор пользователя.
     * @param eventId Идентификатор события.
     * @return Добавленный запрос в формате Дто.
     */
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %s", eventId)));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с id = %s", userId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("Запрос на участие пользователя с id=%d в событии с id=%d " +
                    "уже существует.", userId, eventId));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь с id=%d не должен быть инициатором запроса.",
                    userId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Событие с id=%d не опубликовано.", eventId));
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("У события с id=%d достигнут лимит участников. ", eventId));
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(RequestMapper.mapToRequest(event, user)));
    }

    /**
     * Отмена своего запроса на участие в событии.
     *
     * @param userId    Идентификатор пользователя.
     * @param requestId Идентификатор запроса на участие.
     * @return Запрос на участие со статусом CANCELED.
     */
    @Override
    @Transactional
    public ParticipationRequestDto update(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId);
        if (request == null) {
            throw new NotFoundException(String.format("Запрос на участие с id=%d " +
                    "и id пользователя =%d не найден.", requestId, userId));
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
