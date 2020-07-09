package data.calculate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import data.domain.DataPoint;
import data.dto.TimeValue;
import data.service.RawPointDataService;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class CalcMemDb {
    private RawPointDataService rawService;

    private Map<String, ConcurrentSkipListMap<LocalDateTime, Double>> rawPointValueMap = Maps.newConcurrentMap();

    CalcMemDb(RawPointDataService rawService) {
        this.rawService = rawService;
    }

    /**
     * 缓存这些点，这一个时间点的采样值，供计算 瞬时值 使用
     *
     * @param rawPoints 原始点
     * @param time      时间
     */
    void cacheForInstantCalc(Set<DataPoint> rawPoints, LocalDateTime time) {
        for (DataPoint p : rawPoints) {
            TimeValue value = rawService.sampleValid(p, time).orElseThrow(() -> new RuntimeException(String.format("sample point: %s, time: %s has non value.", p.getId(), time)));

            ConcurrentSkipListMap<LocalDateTime, Double> m = new ConcurrentSkipListMap<>();
            m.put(value.getTime(), value.doubleValue());

            rawPointValueMap.put(p.getId(), m);
        }
    }

    void cacheContinuous(Set<DataPoint> continuousSet, LocalDateTime begin, LocalDateTime end) {
        for (DataPoint p : continuousSet) {
            List<TimeValue> timeValues = rawService.readValid(p, begin, end);
            Optional<TimeValue> timeValue = rawService.sampleValid(p, begin);

            HashSet<TimeValue> set = Sets.newHashSet(timeValues);
            timeValue.ifPresent(set::add);

            Map<LocalDateTime, Double> map = set.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TimeValue::getTime, TimeValue::doubleValue));

            rawPointValueMap.put(p.getId(), new ConcurrentSkipListMap<>(map));
        }
    }

    void cachePeriod(Set<DataPoint> periodSet, LocalDateTime begin, LocalDateTime end) {
        for (DataPoint p : periodSet) {
            List<TimeValue> timeValues = rawService.readValid(p, begin, end);

            Map<LocalDateTime, Double> map = timeValues.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(TimeValue::getTime, TimeValue::doubleValue));

            rawPointValueMap.put(p.getId(), new ConcurrentSkipListMap<>(map));
        }
    }

    void cacheInstant(Set<DataPoint> instantSet, LocalDateTime begin, LocalDateTime end) {
        for (DataPoint p : instantSet) {
            List<TimeValue> validList = rawService.readValid(p, begin.minusHours(1), end.plusHours(1));
            List<TimeValue> offsetList = rawService.readOffset(p, begin, end);

            Map<LocalDateTime, Double> map = Maps.newHashMap();
            for (int i = 0; i < validList.size() - 1; i++) {
                TimeValue cur = validList.get(i);
                TimeValue next = validList.get(i + 1);
                map.put(cur.getTime(), next.doubleValue() - cur.doubleValue());
            }

            for (TimeValue o : offsetList) {
                if (map.containsKey(o.getTime())) {
                    map.put(o.getTime().plusSeconds(1), o.doubleValue());
                } else {
                    map.put(o.getTime(), o.doubleValue());
                }
            }

            rawPointValueMap.put(p.getId(), new ConcurrentSkipListMap<>(map));
        }
    }

    /**
     * 取单个值，永远用sample；
     * <p>
     * sample出的值，时间 <= @Param time
     *
     * @param pointId 点的ID
     * @param time    时间
     * @return 该时间的采样值
     */
    @Nullable
    public TimeValue sample(String pointId, LocalDateTime time) {
        ConcurrentSkipListMap<LocalDateTime, Double> map = rawPointValueMap.get(pointId);
        if (map != null) {
            Map.Entry<LocalDateTime, Double> entry = map.floorEntry(time);
            if (entry != null) {
                return new TimeValue(time, entry.getValue());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public TimeValue read(String pointId, LocalDateTime time) {
        ConcurrentSkipListMap<LocalDateTime, Double> map = rawPointValueMap.get(pointId);
        if (map != null) {
            Double aDouble = map.get(time);
            if (aDouble != null) {
                return new TimeValue(time, aDouble);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<TimeValue> read(String pointId, LocalDateTime begin, LocalDateTime end) {
        ConcurrentSkipListMap<LocalDateTime, Double> map = rawPointValueMap.get(pointId);
        if (map != null) {
            return map.subMap(begin, end).entrySet().stream()
                    .map(e -> new TimeValue(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
