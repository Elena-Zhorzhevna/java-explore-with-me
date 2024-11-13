package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;

import java.util.Optional;
import java.util.Set;


public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    boolean existsByIdAndInitiatorId(Long id, Long userId);

    Set<Event> findAllByIdIn(Set<Long> eventIds);

    boolean existsByCategory(Category category);
}