package data.service.impl;

import com.google.common.collect.Lists;
import data.calculate.CalculateService;
import data.domain.DataPoint;
import data.dto.PointValue;
import data.dto.TimeValue;
import data.service.PointDataService;
import data.service.PointService;
import data.service.RawPointDataService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Component
public class PointDataServiceImpl implements PointDataService {

    private PointService pointService;

    private RawPointDataService rawService;

    private CalculateService calculateService;

    @Autowired
    public PointDataServiceImpl(PointService pointService, RawPointDataService rawService, CalculateService calculateService) {
        this.pointService = pointService;
        this.rawService = rawService;
        this.calculateService = calculateService;
    }

    @Timed("data.write")
    @Override
    public void write(@NonNull PointValue pointValue) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointValue.getPointId());

        if (pointValue.isGood()) {
            rawService.writeValid(dataPoint, pointValue);
        } else {
            rawService.writeInvalid(dataPoint, pointValue);
        }
    }

    @Timed(value = "data.writeList", histogram = true)
    @Override
    public void write(@NonNull List<PointValue> pointValues) {
        for (PointValue pointValue : pointValues) {
            write(pointValue);
        }
    }

    @Override
    public void revise(@NonNull PointValue pointValue) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointValue.getPointId());

        dataPoint.assertDataTypeIsDouble();

        rawService.writeRevise(dataPoint, pointValue);

        // todo  calc offset
    }

    @Override
    public void offset(@NonNull PointValue pointValue) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointValue.getPointId());

        dataPoint.assertDataTypeIsDouble();

        rawService.writeOffset(dataPoint, pointValue);
    }

    @Override
    public Optional<TimeValue> sample(@NonNull String pointId, @NonNull LocalDateTime time) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        if (dataPoint.isRaw()) {
            return rawService.sampleValid(dataPoint, time);
        } else {
            try {
                if (dataPoint.isInstant()) {
                    return Optional.of(calculateService.calcInstant(dataPoint, time));
                } else if (dataPoint.isPeriod()) {
                    return Optional.of(calculateService.calcPeriod(dataPoint, time));
                } else {
                    throw new RuntimeException("unsupported.");
                }
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<TimeValue> sampleInvalid(@NonNull String pointId, @NonNull LocalDateTime time) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        dataPoint.assertTypeIsRaw();

        return rawService.sampleInvalid(dataPoint, time);
    }

    @Override
    public Optional<TimeValue> read(@NonNull String pointId, @NonNull LocalDateTime time) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        if (dataPoint.isRaw()) {
            return rawService.readValid(dataPoint, time);
        } else {
            if (dataPoint.isInstant()) {
                return Optional.of(calculateService.calcInstant(dataPoint, time));
            } else if (dataPoint.isPeriod()) {
                return Optional.of(calculateService.calcPeriod(dataPoint, time));
            } else {
                throw new RuntimeException("unsupported.");
            }
        }
    }

    @Override
    public List<TimeValue> read(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        if (dataPoint.isRaw()) {
            return rawService.readValid(dataPoint, begin, end);
        } else {
            if (dataPoint.isPeriod()) {
                return calculateService.calcPeriodList(dataPoint, begin, end);
            } else {
                throw new RuntimeException("unsupported.");
            }
        }
    }

    @Override
    public List<TimeValue> readInvalid(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);
        dataPoint.assertTypeIsRaw();

        return rawService.readInvalid(dataPoint, begin, end);
    }

    @Override
    public List<TimeValue> readArchive(String pointId, LocalDateTime begin, LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        return rawService.readArchive(dataPoint, begin, end);
    }

    @Override
    public List<TimeValue> readOffset(String pointId, LocalDateTime begin, LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        return rawService.readOffset(dataPoint, begin, end);
    }

    @Override
    public List<TimeValue> readRevise(String pointId, LocalDateTime begin, LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        return rawService.readRevise(dataPoint, begin, end);
    }

    @Override
    public List<TimeValue> sample(String pointId, LocalDateTime begin, LocalDateTime end, int interval) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        if (dataPoint.isRaw()) {
            List<TimeValue> r = read(pointId, begin.minusSeconds(interval - 1), end);

            Map<LocalDateTime, TimeValue> aMap =
                    r.stream().collect(Collectors.toMap(TimeValue::getTime, Function.identity()));
            ConcurrentSkipListMap<LocalDateTime, TimeValue> map = new ConcurrentSkipListMap<>(aMap);

            List<LocalDateTime> timeList = Lists.newArrayList();
            for (LocalDateTime time = begin; time.isBefore(end); time = time.plusSeconds(interval)) {
                timeList.add(time);
            }

            return timeList.stream().map(time -> {
                Map.Entry<LocalDateTime, TimeValue> entry = map.floorEntry(time);
                if (entry == null) {
                    return null;
                } else {
                    TimeValue item = entry.getValue();
                    item.setTime(time);
                    return item;
                }
            }).collect(Collectors.toList());
        } else {
            if (dataPoint.isInstant()) {
                List<LocalDateTime> timeList = Lists.newArrayList();
                for (LocalDateTime time = begin; time.isBefore(end); time = time.plusSeconds(interval)) {
                    timeList.add(time);
                }

                return timeList.stream()
                        .map(time -> sample(pointId, time).orElse(new TimeValue(time, null)))
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("unsupported.");
            }
        }
    }
}
