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
     * Получить общее количество хитов для всех URI в заданном диапазоне времени.
     */
    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits from ParamHit h "
            + "where h.timestamp between :start and :end group by h.app, h.uri "
            + "order by count(h.ip) desc")
    List<StatDto> getHits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Получить количество хитов для заданных URI в заданном диапазоне времени.
     */
    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits from ParamHit h "
            + "where h.uri in (:uris) and h.timestamp between :start and :end group by h.app, h.uri "
            + "order by count(h.ip) desc")
    List<StatDto> getHitsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                @Param("uris") List<String> uris);

    /**
     * Получить общее количество уникальных хитов для всех URI в заданном диапазоне времени.
     */
    @Query("select h.app as app, h.uri as uri, count(distinct(h.ip)) as hits from ParamHit h "
            + "where h.timestamp between :start and :end group by h.app, h.uri "
            + "order by count(distinct(h.ip)) desc")
    List<StatDto> getUniqueHits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Получить количество уникальных хитов для заданных URI в заданном диапазоне времени.
     */
    @Query("select h.app as app, h.uri as uri, count(distinct(h.ip)) as hits from ParamHit h "
            + "where h.uri in (:uris) and h.timestamp between :start and :end group by h.app, h.uri "
            + "order by count(distinct(h.ip)) desc")
    List<StatDto> getUniqueHitsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);
}



/*public interface ParamHitRepository extends JpaRepository<ParamHit, Long> {
    @Query(value =
            "SELECT app AS app, uri AS uri, COUNT(ip) AS views FROM ParamHit" +
            " WHERE uri IN :uris AND (timestamp >= :start AND timestamp <= :end) GROUP BY app, uri"
    )
    List<ParamHitDto> calculateHits(@Param("uris") Collection<String> uris,
                                    @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value =
            "SELECT app AS app, uri AS uri, COUNT(DISTINCT ip) AS views FROM ParamHit" +
            " WHERE uri IN :uris AND (timestamp >= :start AND timestamp < :end) GROUP BY app, uri")
    List<ParamHitDto> calculateUniqueHits(@Param("uris") Collection<String> uris,
                                     @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}*/


