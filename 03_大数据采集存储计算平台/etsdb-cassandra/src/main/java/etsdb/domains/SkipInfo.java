package etsdb.domains;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.Objects;
import com.google.common.collect.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SkipInfo {
    private LocalDateTime time;
    private Multimap<String, LocalDateTime> skipMap;
    private Map<String, Set<LocalDateTime>> map;

    public SkipInfo() {
    }

    @Deprecated()
    public SkipInfo(Set<String> metrics, LocalDateTime beginTime, LocalDateTime endTime) {
        LocalDateTime beginHour = beginTime.withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> hours = Lists.newArrayList();
        for (LocalDateTime hour = beginHour; hour.isBefore(endTime) || hour.isEqual(endTime); hour = hour.plusHours(1)) {
            hours.add(hour);
        }
        for (String metric : metrics) {
            for (LocalDateTime hour : hours) {
                this.onDataPoint(metric, hour);
            }
        }
    }

    private SkipInfo(Map<String, Set<LocalDateTime>> map) {
        this.map = map;
        this.skipMap = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
        for (Map.Entry<String, Set<LocalDateTime>> entry : map.entrySet()) {
            this.skipMap.putAll(entry.getKey(), entry.getValue());
        }
    }

    public static SkipInfo fromDataPoint(DataPoint dp) {
        SkipInfo info = SkipInfo.decoder(dp.getVal());
        info.time = dp.getDateTime();
        return info;
    }

    public static String encoder(SkipInfo info) {
        Map<String, Set<LocalDateTime>> map = info.getMap();
        return JSON.toJSONString(map);
    }

    public static SkipInfo decoder(String json) {
        Map<String, Set<LocalDateTime>> map = JSON.parseObject(json, new TypeReference<Map<String, Set<LocalDateTime>>>() {
        });
        return new SkipInfo(map);
    }

    public synchronized void onDataPoint(String metric, LocalDateTime ldt) {
        if (skipMap == null) {
            skipMap = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
        }
        skipMap.put(metric, ldt);
    }

    @JSONField(serialize = false)
    public synchronized boolean isEmpty() {
        return skipMap == null || skipMap.isEmpty();
    }

    public synchronized SkipInfo reset() {
        SkipInfo info = new SkipInfo();
        info.setSkipMap(this.skipMap);
        this.skipMap = null;
        return info;
    }

    private void setSkipMap(Multimap<String, LocalDateTime> skipMap) {
        this.skipMap = skipMap;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Map<String, Set<LocalDateTime>> getMap() {
        if (map == null) {
            map = skipMap.asMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkipInfo that = (SkipInfo) o;
        return Objects.equal(skipMap, that.skipMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(skipMap);
    }
}
