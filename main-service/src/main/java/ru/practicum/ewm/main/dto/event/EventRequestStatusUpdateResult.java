package ru.practicum.ewm.main.dto.event;

import lombok.*;
import ru.practicum.ewm.main.dto.request.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    /**
     * Список одобренных заявок на участие в событии.
     */
    private List<ParticipationRequestDto> confirmedRequests;
    /**
     * Список отклоненных заявок на участие в событии.
     */
    private List<ParticipationRequestDto> rejectedRequests;
}