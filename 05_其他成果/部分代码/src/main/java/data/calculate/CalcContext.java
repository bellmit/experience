package data.calculate;

import com.google.common.collect.Sets;
import data.domain.DataPoint;
import data.service.RawPointDataService;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class CalcContext {
    private final Set<DataPoint> rawPoints = Sets.newHashSet();

    @Getter
    private final CalcMemDb db;

    CalcContext(RawPointDataService rawService) {
        this.db = new CalcMemDb(rawService);
    }

    public void addRawPoint(DataPoint point) {
        this.rawPoints.add(point);
    }

    void cacheRawData(LocalDateTime time) {
        db.cacheForInstantCalc(rawPoints, time);
    }

    void cacheRawData(LocalDateTime begin, LocalDateTime end) {
        db.cacheContinuous(rawPoints.stream()
                .filter(DataPoint::isContinuous)
                .collect(Collectors.toSet()), begin, end);

        db.cachePeriod(rawPoints.stream()
                .filter(DataPoint::isPeriod)
                .collect(Collectors.toSet()), begin, end);

        db.cacheInstant(rawPoints.stream()
                .filter(DataPoint::isInstant)
                .collect(Collectors.toSet()), begin, end);
    }
}
