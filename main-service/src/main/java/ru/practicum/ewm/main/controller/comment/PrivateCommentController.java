package ru.practicum.ewm.main.controller.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Добавление комментария пользователем.
     *
     * @param userId        Идентификатор пользователя, добавляющего комментарий.
     * @param newCommentDto Данные для обновления комментария.
     * @return Обновленный комментарий в формате ДТО.
     */
    @PostMapping()
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto save(@PathVariable Long userId,
                           @RequestParam @Positive Long eventId,
                           @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен POST-запрос /users/{}/comments/{} с новым комментарием: {}", userId, eventId,
                newCommentDto.getCommentText());
        return commentService.create(userId, eventId, newCommentDto);
    }

    /**
     * Обновление комментария пользователем.
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequest updateCommentRequest) {
        return commentService.updateByUser(userId, commentId, updateCommentRequest);
    }

    /**
     * Получение комментария по его идентификатору.
     */
    @GetMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto getById(@PathVariable Long commentId,
                              @PathVariable Long userId) {
        log.info("Получен GET-запрос  /users{}/comments/{}", userId, commentId);
        return commentService.getById(commentId, userId);
    }

    /**
     * Получение всех комментариев пользователя, идентификатор которого указан.
     */
    @GetMapping()
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDto> getAllByUserId(@PathVariable Long userId) {
        log.info("Получен GET-запрос  /users{}/comments", userId);
        return commentService.getAllByUserId(userId);
    }

    /**
     * Удаление комментария по идентификатору.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(
            @PathVariable @NonNull Long commentId,
            @PathVariable @NonNull Long userId) {
        commentService.deleteByUser(commentId, userId);
    }
}