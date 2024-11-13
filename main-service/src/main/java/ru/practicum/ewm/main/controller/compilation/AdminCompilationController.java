package ru.practicum.ewm.main.controller.compilation;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.service.compilation.CompilationService;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    public AdminCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    /**
     * Добавление новой подборки событий.
     *
     * @param newCompilationDto Добавляемая подборка событий в формате ДТО.
     * @return Добавленная подборка событий в формате ДТО.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен POST-запрос /admin/compilations c новой подборкой: {}", newCompilationDto.getTitle());
        return compilationService.create(newCompilationDto);
    }

    /**
     * Удаление подборки событий.
     *
     * @param compId Идентификатор подборки событий.
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Получен DELETE-запрос /admin/compilations/{}", compId);
        compilationService.delete(compId);
    }

    /**
     * Обновление информации о подборке событий.
     *
     * @param compId                   Идентификатор подборки.
     * @param updateCompilationRequest Данные для обновления подборки.
     * @return Обновленная подборка событий в формате ДТО.
     */
    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен запрос PATCH /admin/compilations/{} на изменение подборки.", compId);
        return compilationService.update(compId, updateCompilationRequest);
    }
}