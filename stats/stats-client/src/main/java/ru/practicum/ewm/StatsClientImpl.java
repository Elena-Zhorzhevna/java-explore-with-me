package ru.practicum.ewm;

import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;

public class StatsClientImpl implements StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestClient restClient;

    public StatsClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }
}
