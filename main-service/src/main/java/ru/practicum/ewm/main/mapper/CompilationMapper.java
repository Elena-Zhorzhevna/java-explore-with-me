package ru.practicum.ewm.main.mapper;

import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.model.Compilation;

import java.util.List;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation mapNewCompilationDtoToCompilation(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.isPinned())
                .title(dto.getTitle())
                .build();
    }

    public static Compilation mapUpdateCompilationRequestToCompilation(UpdateCompilationRequest dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto mapToDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(EventMapper.toEventShortDtoList(compilation.getEvents().stream().toList()))
                .build();
    }

    public static List<CompilationDto> mapToDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::mapToDto)
                .collect(Collectors.toList());
    }
}