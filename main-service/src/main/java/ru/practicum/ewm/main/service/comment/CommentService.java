package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.dto.comment.UpdateCommentRequest;

import java.util.List;

public interface CommentService {

    CommentDto updateByAdmin(Long commentId, UpdateCommentRequest updateCommentRequest);

    void deleteByAdmin(Long commentId);

    List<CommentDto> getAllByUserId(Long userId);

    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateByUser(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest);

    CommentDto getById(Long commentId, Long userId);

    void deleteByUser(Long commentId, Long userId);

    List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size);
}
