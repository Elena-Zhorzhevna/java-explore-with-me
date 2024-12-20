package ru.practicum.ewm.main.service.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.CompilationMapper;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Добавление новой подборки событий.
     *
     * @param newCompilationDto Добавляемая подборка событий в формате ДТО.
     * @return Добавленная подборка событий в формате ДТО.
     */
    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.mapNewCompilationDtoToCompilation(newCompilationDto);
        compilation.setEvents(getAllEvents(newCompilationDto.getEvents()));
        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        log.info("Добавлена подборка событий: {}", compilation.getTitle());
        return CompilationMapper.mapToDto(compilation);
    }

    /**
     * Удаление подборки событий.
     *
     * @param compId Идентификатор подборки событий.
     */
    @Override
    @Transactional
    public void delete(Long compId) {

        if (compilationRepository.existsById(compId)) {
            log.info("Удалена подборка событий с id = {}", compId);
            compilationRepository.deleteById(compId);
        }
    }

    /**
     * Обновление информации о подборке событий.
     *
     * @param compId                   Идентификатор подборки.
     * @param updateCompilationRequest Данные для обновления подборки.
     * @return Обновленная подборка событий в формате ДТО.
     */
    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationToUpdate = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдена подборка с id = %s", compId)));

        if (updateCompilationRequest.getPinned() != null) {
            compilationToUpdate.setPinned(updateCompilationRequest.getPinned());
            log.info("Обновлено поле pinned: {}", updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null) {
            compilationToUpdate.setTitle(updateCompilationRequest.getTitle());
            log.info("Обновлено поле title: {}", updateCompilationRequest.getTitle());
        }

        compilationToUpdate.setEvents(getAllEvents(updateCompilationRequest.getEvents()));

        try {
            compilationRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }

        log.info("Обновлена подборка: {}", compilationToUpdate.getTitle());
        return CompilationMapper.mapToDto(compilationToUpdate);
    }

    /**
     * Получение подборок событий.
     *
     * @param pinned Закрепленные/не закрепленные подборки.
     * @param from   Количество элементов, которые нужно пропустить для формирования текущего набора.
     * @param size   Количество элементов в наборе.
     * @return Список подборок в формате ДТО.
     */
    @Override
    public List<CompilationDto> getAllPublic(Boolean pinned, Integer from, Integer size) {

        if (from == null || size == null || from < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters.");
        }

        Page<Compilation> compilationsPage;
        if (pinned != null) {
            compilationsPage = compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size,
                    size, Sort.by(Sort.Direction.ASC, "id")));
        } else {
            compilationsPage = compilationRepository.findAll(PageRequest.of(from / size, size,
                    Sort.by(Sort.Direction.ASC, "id")));
        }

        List<Compilation> compilations = compilationsPage.getContent();

        log.info("Retrieved compilations: size={} (total elements: {})", compilations.size(),
                compilationsPage.getTotalElements());

        return CompilationMapper.mapToDtoList(compilations);
    }

    /**
     * Получение подборки событий по идентификатору.
     *
     * @param comId Идентификатор подборки событий.
     * @return Подборка событий в формате ДТО.
     */
    @Override
    public CompilationDto getById(Long comId) {
        final Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id = %s не найдена.", comId)));
        log.info("Получена подборка событий: {}", compilation.getTitle());
        return CompilationMapper.mapToDto(compilation);
    }

    private Set<Event> getAllEvents(Set<Long> eventIds) {
        if (eventIds == null) {
            return Set.of();
        }
        return eventRepository.findAllByIdIn(eventIds);
    }
}