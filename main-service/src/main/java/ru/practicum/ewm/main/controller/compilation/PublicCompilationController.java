package ru.practicum.ewm.main.controller.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    public PublicCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос /compilations c параметрами: pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getAllPublic(pinned, from, size);
    }

    @GetMapping("/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto get(@PathVariable Long comId) {
        log.info("Получен запрос GET /compilations/{}", comId);
        return compilationService.getById(comId);
    }
}