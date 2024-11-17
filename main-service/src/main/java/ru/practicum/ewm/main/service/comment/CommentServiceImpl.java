package ru.practicum.ewm.main.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;

import ru.practicum.ewm.main.mapper.CommentMapper;

import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.State;
import ru.practicum.ewm.main.model.enums.Status;
import ru.practicum.ewm.main.repository.CommentRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
                              EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Обновление комментария администратором.
     *
     * @param commentId            Идентификатор комментария.
     * @param updateCommentRequest Данные для обновления.
     * @return Обновленный комментарий в формате ДТО.
     */
    @Override
    public CommentDto updateByAdmin(Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комментарий с id = %s", commentId)));

        if (updateCommentRequest.getCommentText() != null) {
            commentToUpdate.setCommentText(updateCommentRequest.getCommentText());
            log.info("Обновлен комментарий: {}", updateCommentRequest.getCommentText());
        }

        try {
            commentRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Обновлен комментарий: {}", commentToUpdate.getCommentText());
        return CommentMapper.mapToCommentDto(commentToUpdate);
    }

    /**
     * Удаление комментария по идентификатору администратором.
     *
     * @param commentId Идентификатор комментария.
     */
    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            log.info("Удален комментарий с id = {}", commentId);
            commentRepository.deleteById(commentId);
        }
    }

    /**
     * Получение всех комментариев пользователя, идентификатор которого указан.
     *
     * @param ownerId Идентификатор пользователя, написавшего комментарий
     * @return Список комментариев указанного пользователя в формате ДТО.
     */
    @Override
    public List<CommentDto> getAllByUserId(Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            return CommentMapper.mapToDtoList(commentRepository.findAllByOwnerId(ownerId));
        } else {
            throw new NotFoundException(String.format("Не найден пользователь с id = %s", ownerId));
        }
    }

    /**
     * Добавление комментария пользователем.
     *
     * @param userId        Идентификатор пользователя, добавляющего комментарий.
     * @param eventId       Идентификатор события, к которому добавляется комментарий.
     * @param newCommentDto Добавляемый комментарий в формате ДТО.
     * @return Добавленный комментарий в формате ДТО.
     */
    @Transactional
    @Override
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с id = %s", userId)));

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %s", eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя добавить комментарий к неопубликованному событию.");
        }

        Comment comment = CommentMapper.mapToComment(newCommentDto);
        comment.setOwner(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(Status.PENDING);
        log.info("Добавлен комментарий: {} с id пользователя = {}", newCommentDto.getCommentText(), userId);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    /**
     * Обновление комментария пользователем.
     *
     * @param userId               Идентификатор пользователя.
     * @param commentId            Идентификатор комментария.
     * @param updateCommentRequest Данные для обновления.
     * @return Обновленный комментарий в формате ДТО.
     */
    @Override
    @Transactional
    public CommentDto updateByUser(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с id = %s", userId)));

        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комментарий с id = %s", commentId)));

        if (!commentToUpdate.getOwner().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id = " + userId + " не является создателем комментария " +
                    "с id = " + commentId);
        }

        if (updateCommentRequest.getCommentText() != null) {
            commentToUpdate.setCommentText(updateCommentRequest.getCommentText());
        }
        return CommentMapper.mapToCommentDto(commentToUpdate);
    }

    /**
     * Получение комментария по идентификатору пользователем, добавившим комментарий.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     * @return Комментарий в формате ДТО.
     */
    @Override
    public CommentDto getById(Long commentId, Long userId) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);
        if (!userId.equals(comment.getOwner().getId())) {
            throw new ConflictException("Пользователь с id = " + userId + " не является создателем комментария " +
                    "с id = " + commentId);
        }
        return CommentMapper.mapToCommentDto(comment);
    }

    /**
     * Удаление пользователем комментария по его идентификатору.
     *
     * @param commentId Идентификатор комментария.
     * @param userId    Идентификатор пользователя.
     */
    @Override
    @Transactional
    public void deleteByUser(Long commentId, Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с id = %s", userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комментарий с id = %s", commentId)));

        if (!comment.getOwner().getId().equals(userId)) {
            throw new ConflictException("У вас нет прав на удаление этого комментария.");
        }
        commentRepository.deleteById(commentId);
    }

    /**
     * Получение всех комментариев по идентификатору события.
     *
     * @param eventId Идентификатор события.
     * @param from    Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size    Количество элементов в наборе.
     * @return Список событий в формате ДТО.
     */
    public List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        if (from == null || size == null) {
            log.info("Получение комментариев, если доп. параметры не указаны.");
            return commentRepository.findAllByEventId(eventId).stream()
                    .map(CommentMapper::mapToCommentDto)
                    .collect(Collectors.toList());
        }
        validatePagesRequest(from, size);
        Pageable page = PageRequest.of(from, size);
        log.info("Получение комментариев с введенными параметрами.");
        return commentRepository.findAllByEventId(eventId, page).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден."));
    }

    private void validatePagesRequest(Integer pageNum, Integer pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            String message = "Ошибка: неверно указано количество страниц или размер страницы.";
            log.warn(message);
            throw new ConditionsNotMetException(message);
        }
    }
}