package ru.practicum.ewm;

import ewm.ParamHitDto;
import ewm.StatDto;

import java.util.List;

/**
 * Интерфейс HTTP-клиента для работы с сервисом статистики.
 */
public interface StatsClient {
    /**
     * Метод для отправки ParamHit на сервер статистики.
     */
    void hit(ParamHitDto endpointHitDto);

    /**
     * Метод для получения статистики с сервера.
     */
    List<StatDto> getStats(String start, String end, List<String> uris, boolean unique);
}