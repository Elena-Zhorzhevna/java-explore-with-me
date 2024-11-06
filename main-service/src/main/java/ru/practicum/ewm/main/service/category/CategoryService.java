package ru.practicum.ewm.main.service.category;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long catId);

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(CategoryDto dto, Long catId);

    void delete(Long catId);
}
