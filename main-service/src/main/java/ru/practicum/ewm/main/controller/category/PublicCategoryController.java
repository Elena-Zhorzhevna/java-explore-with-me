package ru.practicum.ewm.main.controller.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/categories")
public class PublicCategoryController {

    public final CategoryService categoryService;

    @Autowired
    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Получение категорий.
     *
     * @param from Количество категорий, которые нужно пропустить для формирования текущего набора.
     * @param size Количество категорий в наборе.
     * @return Список категорий.
     */
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос c параметрами: from = {}, size = {}", from, size);
        return categoryService.getAll(from, size);
    }

    /**
     * Получение информации о категории по ее идентификатору.
     *
     * @param catId Идентификатор категории.
     * @return Категория в формате ДТО.
     */
    @GetMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto get(@PathVariable Long catId) {
        log.info("Получен запрос GET /categories/{}", catId);
        return categoryService.getById(catId);
    }
}