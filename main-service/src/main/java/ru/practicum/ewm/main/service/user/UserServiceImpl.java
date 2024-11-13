package ru.practicum.ewm.main.service.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.user.NewUserRequest;
import ru.practicum.ewm.main.dto.user.UserDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.UserMapper;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
     * либо о конкретных (учитываются указанные идентификаторы).
     * В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список.
     *
     * @param ids  id пользователей.
     * @param from Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size Количество элементов в наборе.
     * @return Список пользователей.
     */
    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<User> users = new ArrayList<>();
        Pageable page = PageRequest.of(from, size);
        log.info("Получение пользователей с введенными параметрами.");
        if (ids == null) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, page);
        }
        log.info("Число пользователей : {}", users.size());
        return UserMapper.mapToUserDtoList(users);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUserRequest Данные добавляемого пользователя.
     * @return Добавленный пользователь в формате UserDto.
     */
    @Transactional
    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Пользователь с такой эл.почтой уже существует.");
        }
        final User user = UserMapper.mapToUser(newUserRequest);
        log.info("Добавлен пользователь с эл.почтой: {}", user.getEmail());
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    /**
     * Удаление пользователя.
     *
     * @param userId Идентификатор пользователя.
     */
    @Transactional
    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.deleteById(userId);
    }
}