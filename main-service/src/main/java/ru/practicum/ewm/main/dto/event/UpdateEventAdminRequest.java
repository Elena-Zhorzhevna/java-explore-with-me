package ru.practicum.ewm.main.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.main.model.enums.StateAdminAction;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    /**
     * Действие администратора, опубликовать или отклонить.
     */
    private StateAdminAction stateAction;

    public UpdateEventAdminRequest(StateAdminAction stateAction) {
        this.stateAction = stateAction;
    }
}
