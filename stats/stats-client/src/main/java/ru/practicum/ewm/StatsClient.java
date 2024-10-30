package ru.practicum.ewm;

import ewm.ParamHitDto;
import ewm.StatDto;

import java.util.List;

public interface StatsClient {

    void hit(ParamHitDto endpointHitDto);

    List<StatDto> getStats(String start, String end, List<String> uris, boolean unique);
}