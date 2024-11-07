package ru.practicum.ewm.main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart; // начало диапазона для фильтрации по дате
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;   // конец диапазона для фильтрации по дате
    private Boolean paid; // фильтрация по параметру "платное ли событие"
    //private Boolean onlyAvailable;
}
