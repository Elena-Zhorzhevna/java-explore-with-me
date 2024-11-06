package ru.practicum.ewm.main.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventSearchParams {
    private String text;           // текст для поиска в названии и аннотации
    private List<Long> categories; // список категорий для фильтрации
    private LocalDateTime rangeStart; // начало диапазона для фильтрации по дате
    private LocalDateTime rangeEnd;   // конец диапазона для фильтрации по дате
    private Boolean paid; // фильтрация по параметру "платное ли событие"
    //private Boolean onlyAvailable;
}
