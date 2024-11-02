package ru.practicum.ewm.main.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 *
 * Изменение статуса запроса на участие в событии текущего пользователя.
 */
public class EventRequestStatusUpdateRequest {

    /**
     * Идентификаторы запросов на участие в событии текущего пользователя.
     */
    @NotNull
    private List<Long> requestIds;

    /**
     * Новый статус запроса на участие в событии текущего пользователя.
     */
    @NotBlank
    private String status;
}