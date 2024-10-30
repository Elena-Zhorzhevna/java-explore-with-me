package ewm.stats.repository;

import ewm.StatDto;
import ewm.stats.model.ParamHit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface StatsRepository extends JpaRepository<ParamHit, Long> {

    /**
     * Получить статистику для всех URI в заданном диапазоне времени.
     */
    @Query(value = """
            select new ewm.StatDto(h.app, h.uri, count(h.ip))
            from ParamHit as h
            where h.timestamp between :start and :end
            group by h.app, h.uri
            order by count(h.ip) desc
            """)
    List<StatDto> getHits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Получить статистику для заданных URI в заданном диапазоне времени.
     */
    @Query(value = """
            select new ewm.StatDto(h.app, h.uri, count(h.ip))
            from ParamHit as h
            where h.uri in :uris and h.timestamp between :start and :end
            group by h.app, h.uri
            order by count(h.ip) desc""")
    List<StatDto> getHitsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                @Param("uris") List<String> uris);

    /**
     * Получить статистику, учитывая только уникальные посещения (только с уникальным ip), для всех URI
     * в заданном диапазоне времени.
     */
    @Query(value = """
            select new ewm.StatDto(h.app, h.uri, count(distinct(h.ip)))
            from ParamHit as h
            where h.timestamp between :start and :end
            group by h.app, h.uri
            order by count(distinct(h.ip)) desc
            """)
    List<StatDto> getUniqueHits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Получить статистику, учитывая только уникальные посещения (только с уникальным ip),
     * для заданных URI в заданном диапазоне времени.
     */
    @Query(value = """
            select new ewm.StatDto(h.app, h.uri, count(distinct(h.ip)))
            from ParamHit as h
            where h.uri in :uris and h.timestamp between :start and :end
            group by h.app, h.uri
            order by count(distinct(h.ip)) desc""")
    List<StatDto> getUniqueHitsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);
}