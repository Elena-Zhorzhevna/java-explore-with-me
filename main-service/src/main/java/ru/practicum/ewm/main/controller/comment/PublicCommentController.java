package ru.practicum.ewm.main.controller.comment;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {
    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получение всех комментариев по идентификатору события.
     *
     * @param eventId Идентификатор события.
     * @param from    Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size    Количество элементов в наборе.
     * @return Список комментариев в формате ДТО.
     */
    @GetMapping()
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDto> getCommentsByEventId(@PathVariable Long eventId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен GET-запрос /events/{}/comments", eventId);
        return commentService.getAllCommentsByEventId(eventId, from, size);
    }
}