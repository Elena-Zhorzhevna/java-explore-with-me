package ru.practicum.ewm;

import ewm.ParamHitDto;
import ewm.StatDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP-клиент для работы с сервисом статистики.
 */
@Service
@Slf4j
public class StatsClientImpl implements StatsClient {
    private final RestClient restClient;

    public StatsClientImpl(@Value("${stats.server.url}") String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    /**
     * Метод для отправки ParamHit на сервер статистики.
     */
    @Override
    public void hit(ParamHitDto paramHitDto) {
        log.debug("Отправка POST-запроса на сервер статистики с hit = {}", paramHitDto);
        try {
            restClient.post()
                    .uri("/hit")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paramHitDto)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Hit отправлен успешно");
        } catch (RestClientException e) {
            log.warn("Ошибка при отправке hit на сервер статистики. Код ошибки: {}, Сообщение: {}",
                    e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            log.error("Неизвестная ошибка при отправке hit на сервер статистики", e);
        }
    }

    /**
     * Метод для получения статистики с сервера.
     */
    @Override
    public List<StatDto> getStats(String start, String end, List<String> uris, boolean unique) {

        log.debug("Отправка GET-запроса на сервер статистики для uris = {}", uris);
        try {
            return restClient.get()
                    .uri(
                            uri -> uri.path("/stats")
                                    .queryParam("start", codeTime(start))
                                    .queryParam("end", codeTime(end))
                                    .queryParam("uris", uris)
                                    .queryParam("unique", unique)
                                    .build()
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            log.warn("Ошибка при получении статистики с сервера статистики, причины : {}", e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении статистики", e);
            return List.of();
        }
    }

    /**
     * Метод для кодирования времени в формате, подходящем для URL
     */
    private String codeTime(String time) {
        return URLEncoder.encode(time, StandardCharsets.UTF_8);
    }
}