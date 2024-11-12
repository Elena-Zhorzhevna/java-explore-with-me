package ru.practicum.ewm.main.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.service.user.UserService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {
    private final UserService userService;

    public AdminUsersController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки), либо о конкретных (учитываются указанные идентификаторы)
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список.
     *
     * @param ids  Список идентификаторов пользователей.
     * @param from Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size Количество элементов в наборе.
     * @return
     */
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос GET /admin/users");
        return userService.getAll(ids, from, size);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUserRequest Данные добавляемого пользователя.
     * @return Добавленный пользователь в формате ДТО.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto save(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Получен POST-запрос /admin/users на добавление пользователя: {}", newUserRequest.getEmail());
        return userService.save(newUserRequest);
    }

    /**
     * Удаление пользователя.
     *
     * @param userId Идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос /admin/users/{}", userId);
        userService.delete(userId);
    }
}