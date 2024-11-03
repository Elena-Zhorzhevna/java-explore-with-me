package ru.practicum.ewm.main.dto.event;

import ru.practicum.ewm.main.model.enums.StateAdminAction;

public class UpdateEventAdminRequest extends UpdateEventRequest {
    private StateAdminAction adminAction;
}
