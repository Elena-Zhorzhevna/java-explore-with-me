package ru.practicum.ewm.main.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.main.model.enums.StateUserAction;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {

    /**
     * Действие пользователя.
     */
    private StateUserAction stateAction;

    public UpdateEventUserRequest(StateUserAction stateAction) {
        this.stateAction = stateAction;
    }
}