package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.main.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    boolean existsByIdAndInitiatorId(Long id, Long userId);

    List<Event> findByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String annotation,
                                                                                      String description);

    Set<Event> findAllByIdIn(Set<Long> eventIds);
    List<Event> findByCategoryIdIn(List<Long> categoryIds);

    List<Event> findByPaid(Boolean paid);

    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
}