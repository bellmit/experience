package data.service;

import data.dto.PointValue;
import data.dto.TimeValue;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointDataService {
    /**
     * 写入，采集或手工录入
     *
     * @param pointValue pointValue
     */
    void write(@NonNull PointValue pointValue);

    /**
     * 写入，采集或手工录入
     *
     * @param pointValues pointValues
     */
    void write(@NonNull List<PointValue> pointValues);

    /**
     * 修正
     *
     * @param pointValue pointValue
     */
    void revise(@NonNull PointValue pointValue);

    /**
     * 偏移量，换表时使用，也可以用修正接口实现
     *
     * @param pointValue pointValue
     */
    void offset(@NonNull PointValue pointValue);


    /**
     * 采样，只采有效的
     *
     * @param pointId pointId
     * @param time    time
     * @return 该时间点(包含)前最新的值
     */
    Optional<TimeValue> sample(@NonNull String pointId, @NonNull LocalDateTime time);

    /**
     * 采样，只采无效的
     *
     * @param pointId pointId
     * @param time    time
     * @return 该时间点(包含)前最新的值
     */
    Optional<TimeValue> sampleInvalid(@NonNull String pointId, @NonNull LocalDateTime time);

    /**
     * 读某一时间点的值
     *
     * @param pointId pointId
     * @param time    time
     * @return 该点的值
     */
    Optional<TimeValue> read(@NonNull String pointId, @NonNull LocalDateTime time);

    /**
     * 读一段时间的值
     *
     * @param pointId pointId
     * @param begin   开始时间，包含
     * @param end     结束时间，不包含
     * @return 这段时间内所有的值
     */
    List<TimeValue> read(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 读一段时间的值
     *
     * @param pointId pointId
     * @param begin   开始时间，包含
     * @param end     结束时间，不包含
     * @return 这段时间内所有的值
     */
    List<TimeValue> readInvalid(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 读一段时间的值
     *
     * @param pointId pointId
     * @param begin   开始时间，包含
     * @param end     结束时间，不包含
     * @return 这段时间内所有的值
     */
    List<TimeValue> readArchive(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 读一段时间的值
     *
     * @param pointId pointId
     * @param begin   开始时间，包含
     * @param end     结束时间，不包含
     * @return 这段时间内所有的值
     */
    List<TimeValue> readOffset(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 读一段时间的值
     *
     * @param pointId pointId
     * @param begin   开始时间，包含
     * @param end     结束时间，不包含
     * @return 这段时间内所有的值
     */
    List<TimeValue> readRevise(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 间隔采样
     *
     * @param pointId  pointId
     * @param begin    开始时间，包含
     * @param end      结束时间，不包含
     * @param interval 间隔秒数
     * @return 采样值
     */
    List<TimeValue> sample(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end, int interval);
}
