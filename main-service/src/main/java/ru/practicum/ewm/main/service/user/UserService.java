package ru.practicum.ewm.main.service.user;

import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;

import java.util.List;

public interface UserService {
    /**
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
     * либо о конкретных (учитываются указанные идентификаторы).
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список.
     */
    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    /**
     * Добавление нового пользователя.
     */
    UserDto save(NewUserRequest newUserRequest);

    /**
     * Удаление пользователя.
     */
    void delete(Long userId);
}
