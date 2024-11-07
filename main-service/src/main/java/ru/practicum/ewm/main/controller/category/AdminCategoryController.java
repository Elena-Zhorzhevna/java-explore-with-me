package ru.practicum.ewm.main.controller.category;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    public final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Получен POST-запрос /admin/categories на добавление новой категории: {}", dto.getName());
        return categoryService.create(dto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto update(@RequestBody @Valid CategoryDto dto,
                                              @PathVariable Long catId) {
        log.info("Получен PATCH-запрос /admin/categories/{} на изменение категории: {}", catId, dto.getName());
        return categoryService.update(dto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("Получен DELETE-запрос /admin/categories/{} на удаление категории ", catId);
        categoryService.delete(catId);
    }
}