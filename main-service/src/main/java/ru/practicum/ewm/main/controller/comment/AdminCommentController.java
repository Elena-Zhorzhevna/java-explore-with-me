package ru.practicum.ewm.main.controller.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получение всех комментариев события, идентификатор которого указан.
     *
     * @param eventId Идентификатор события.
     * @param from   Количество комментариев, которые нужно пропустить для формирования текущего набора.
     * @param size   Количество комментариев в наборе.
     * @return Список комментариев в формате ДТО.
     */
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDto> getAllByEventId(@RequestParam Long eventId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /admin/comments");
        return commentService.getAllCommentsByEventId(eventId, from, size);
    }

    /**
     * Обновление комментария.
     *
     * @param commentId            Идентификатор комментария.
     * @param updateCommentRequest Данные для обновления.
     * @return Обновленный комментарий в формате ДТО.
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto update(@PathVariable Long commentId,
                             @RequestBody @Valid UpdateCommentRequest updateCommentRequest) {
        log.info("Получен запрос PATCH /admin/comments/{} на изменение комментария.", commentId);
        return commentService.updateByAdmin(commentId, updateCommentRequest);
    }

    /**
     * Удаление комментария по идентификатору.
     *
     * @param commentId Идентификатор комментария.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("Получен DELETE-запрос /admin/comments/{}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}