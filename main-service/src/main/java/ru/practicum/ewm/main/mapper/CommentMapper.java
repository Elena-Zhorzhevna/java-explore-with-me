package ru.practicum.ewm.main.mapper;

import ru.practicum.ewm.main.dto.comment.CommentDto;
import ru.practicum.ewm.main.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.model.*;
import ru.practicum.ewm.main.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .ownerId(comment.getOwner().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .status(comment.getStatus())
                .build();
    }

    public static Comment mapToComment(NewCommentDto dto) {
        return Comment.builder()
                .commentText(dto.getCommentText())
                .build();
    }

    public static Comment mapToComment(Event event, User owner, CommentDto dto) {

        Status status = event.getParticipantLimit() == 0 ||
                !event.getRequestModeration() ? Status.CONFIRMED : Status.PENDING;

        return Comment.builder()
                .id(dto.getId())
                .owner(owner)
                .event(event)
                .commentText(dto.getCommentText())
                .created(LocalDateTime.now())
                .status(status)
                .build();
    }

    public static List<CommentDto> mapToDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}