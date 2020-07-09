package data.service;

import lombok.NonNull;
import period.dto.PeriodType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface EntityDataService {
    /**
     * 写实体的期间值
     *
     * @param entityId    实体ID
     * @param periodType  期间类型，不能为INSTANT 或 CONTINUOUS
     * @param paramValues 参数及其对应的值，Key: parameterId，Value：该参数在该时间的值
     */
    void write(@NonNull String entityId, @NonNull PeriodType periodType, @NonNull LocalDateTime time, @NonNull Map<String, ?> paramValues);

    /**
     * 读实体的期间值（从库中读取，非实时计算）
     *
     * @param entityId   实体ID
     * @param periodType 期间类型，不能为INSTANT 或 CONTINUOUS
     * @param time       要读取的时间
     * @param params     参数ID集合
     * @return 参数对应的值，Key: parameterId，Value：该参数在该时间的值
     */
    Map<String, Double> readArchive(@NonNull String entityId, @NonNull PeriodType periodType, @NonNull LocalDateTime time, @NonNull Collection<String> params);

    /**
     * 读实体的期间值（实时计算）
     *
     * @param entityId   实体ID
     * @param periodType 期间类型，不能为INSTANT 或 CONTINUOUS
     * @param time       要读取的时间
     * @param params     参数ID集合
     * @return 参数对应的值，Key: parameterId，Value：该参数在该时间的值
     */
    Map<String, Double> readCalc(@NonNull String entityId, @NonNull PeriodType periodType, @NonNull LocalDateTime time, @NonNull Collection<String> params);

    /**
     * 读实体的期间值（从库中读取，非实时计算）
     *
     * @param entityId   实体ID
     * @param periodType 期间类型，不能为INSTANT 或 CONTINUOUS
     * @param time       要读取的时间
     * @return 参数对应的值，Key: parameterId，Value：该参数在该时间的值
     */
    Map<String, Double> readArchive(@NonNull String entityId, @NonNull PeriodType periodType, @NonNull LocalDateTime time);

    /**
     * 读实体的期间值（实时计算）
     *
     * @param entityId   实体ID
     * @param periodType 期间类型，不能为INSTANT 或 CONTINUOUS
     * @param time       要读取的时间
     * @return 参数对应的值，Key: parameterId，Value：该参数在该时间的值
     */
    Map<String, Double> readCalc(@NonNull String entityId, @NonNull PeriodType periodType, @NonNull LocalDateTime time);

    /**
     * 归档
     *
     * @param periodType 期间类型，不能为INSTANT 或 CONTINUOUS
     * @param time       归档的时间
     */
    void archive(PeriodType periodType, LocalDateTime time);

    void deleteData(String measurement, String entityId);
}
