package ewm.stats.mapper;
import ewm.ParamHitDto;
import ewm.stats.model.ParamHit;



public class ParamHitMapper {

    public static ParamHitDto toDto(ParamHit paramHit) {
        if (paramHit == null) {
            return null;
        }
        return new ParamHitDto(
                paramHit.getApp(),
                paramHit.getUri(),
                paramHit.getIp(),
                paramHit.getTimestamp()
        );
    }

    public static ParamHit toParamHit(ParamHitDto paramHitDto) {
        if (paramHitDto == null) {
            return null;
        }
        return ParamHit.builder()
                .app(paramHitDto.getApp())
                .uri(paramHitDto.getUri())
                .ip(paramHitDto.getIp())
                .timestamp(paramHitDto.getTimestamp())
                .build();
    }
}