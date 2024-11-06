package ru.practicum.ewm.main.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.main.model.enums.StateUserAction;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {

    private StateUserAction stateAction;  // Действие пользователя (например, отправить на ревью)

    public UpdateEventUserRequest(StateUserAction stateAction) {
        this.stateAction = stateAction;
    }
}