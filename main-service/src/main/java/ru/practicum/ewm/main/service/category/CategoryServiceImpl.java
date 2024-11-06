package ru.practicum.ewm.main.service.category;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.CategoryMapper;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        if (from == null || size == null) {
            log.info("Получение категорий, если доп. параметры не указаны.");
            return categoryRepository.findAll().stream()
                    .map(CategoryMapper::mapToDto)
                    .collect(Collectors.toList());
        }
        validatePagesRequest(from, size);
        Pageable page = PageRequest.of(from, size);
        log.info("Получение запросов вещей с введенными параметрами.");
        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        if (catId == null) {
            throw new NotFoundException("Категория с id не может быть null.");
        }
        log.info("Попытка получить категорию с id = {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена!"));
        return CategoryMapper.mapToDto(category);
    }

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto dto) {
        Category category = CategoryMapper.mapToCategory(dto);

    /*    try {
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }*/
        CategoryDto savedCategory = CategoryMapper.mapToDto(categoryRepository.save(category));
        log.info("Добавлена категория: {} с id = {}", savedCategory.getName(), savedCategory.getId());
        return savedCategory;
    }

    public CategoryDto update(final CategoryDto dto, final Long catId) {
        final Category category = getCategory(catId);
        Optional.ofNullable(dto.getName()).ifPresent(category::setName);
        final Category savedCategory = categoryRepository.save(category);
        log.info("Обновлена категория с id = {}: {}", savedCategory.getId(), savedCategory);
        return CategoryMapper.mapToDto(savedCategory);
    }


    @Override
    public void delete(Long catId) {
        CategoryDto categoryDto = CategoryMapper.mapToDto(getCategory(catId));
        categoryRepository.deleteById(catId);
        log.info("Категория с id = {} удалена", catId);
    }

    private Category getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена!"));
        return category;
    }


    private void validatePagesRequest(Integer pageNum, Integer pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            String message = "Ошибка: неверно указано количество страниц или размер страницы.";
            log.warn(message);
            throw new ConditionsNotMetException(message);
        }
    }
}