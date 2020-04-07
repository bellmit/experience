package etsdb.domains;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import etsdb.services.MetricService;
import etsdb.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RepairHourlyInfo {
    private LocalDateTime time;
    private final Multimap<String, LocalDateTime> repairMap = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);
    private Map<String, Set<LocalDateTime>> map;

    public RepairHourlyInfo() {
    }

    @Deprecated()
    public RepairHourlyInfo(Set<String> metrics, LocalDateTime beginTime, LocalDateTime endTime) {
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

    private RepairHourlyInfo(Map<String, Set<LocalDateTime>> map) {
        this.map = map;
        for (Map.Entry<String, Set<LocalDateTime>> entry : map.entrySet()) {
            this.repairMap.putAll(entry.getKey(), entry.getValue());
        }
    }

    public static RepairHourlyInfo fromDataPoint(DataPoint dp) {
        RepairHourlyInfo info = RepairHourlyInfo.decoder(dp.getVal());
        info.time = dp.getDateTime();
        return info;
    }

    public static String encoder(RepairHourlyInfo info) {
        Map<String, Set<LocalDateTime>> map = info.getMap();
        return JSON.toJSONString(map);
    }

    public static RepairHourlyInfo decoder(String json) {
        Map<String, Set<LocalDateTime>> map = JSON.parseObject(json, new TypeReference<Map<String, Set<LocalDateTime>>>() {
        });
        return new RepairHourlyInfo(map);
    }

    public synchronized void onDataPoint(String metric, LocalDateTime ldt) {
        repairMap.put(metric, fixTime(metric, ldt));
    }

    private LocalDateTime fixTime(String metric, LocalDateTime ldt) {
        LocalDateTime time;
        if (MetricService.isHourly(metric)) {
            time = DateUtils.startOfHour(ldt);
        } else if (MetricService.isDaily(metric)) {
            time = DateUtils.startOfDay(ldt);
        } else if (MetricService.isMonthly(metric)) {
            time = DateUtils.startOfMonth(ldt);
        } else if (MetricService.isYearly(metric)) {
            time = DateUtils.startOfYear(ldt);
        } else {
            time = ldt;
        }
        return time;
    }

    @JSONField(serialize = false)
    public synchronized boolean isEmpty() {
        return repairMap == null || repairMap.isEmpty();
    }

    public synchronized RepairHourlyInfo reset() {
        RepairHourlyInfo info = new RepairHourlyInfo();
        if (this.repairMap.size() < 30000) {
            info.setRepairMap(this.repairMap);
            this.repairMap.clear();
        } else {
            Set<String> keySet = Sets.newHashSet(this.repairMap.keySet());
            for (String key : keySet) {
                info.repairMap.putAll(key, this.repairMap.removeAll(key));
                if (info.repairMap.size() > 29000) {
                    break;
                }
            }
        }
        return info;
    }

    private synchronized void setRepairMap(Multimap<String, LocalDateTime> repairMap) {
        this.repairMap.putAll(repairMap);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public synchronized Map<String, Set<LocalDateTime>> getMap() {
        if (map == null) {
            map = repairMap.asMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairHourlyInfo that = (RepairHourlyInfo) o;
        return Objects.equal(repairMap, that.repairMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(repairMap);
    }
}
