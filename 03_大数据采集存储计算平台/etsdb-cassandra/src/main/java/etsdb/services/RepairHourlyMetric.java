package etsdb.services;

import etsdb.domains.DataPoint;
import etsdb.domains.RepairHourlyInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RepairHourlyMetric {
    private static final String METRIC = "r.repair.hourly.metric";
    private static RepairHourlyMetric instance;
    private final MetricService metricService;

    private RepairHourlyMetric(MetricService metricService) {
        this.metricService = metricService;
    }

    public static synchronized RepairHourlyMetric getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new RepairHourlyMetric(metricService);
        }
        return instance;
    }

    public void write(RepairHourlyInfo info) {
        DataPoint dp =
                new DataPoint(METRIC, LocalDateTime.now(), RepairHourlyInfo.encoder(info), DataPoint.OK);
        metricService.write(dp);
    }

    public List<RepairHourlyInfo> read(LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = metricService.read(METRIC, begin, end, true);
        return dps.stream().map(RepairHourlyInfo::fromDataPoint).collect(Collectors.toList());
    }

    public Optional<RepairHourlyInfo> sample(LocalDateTime ldt) {
        DataPoint dp = metricService.sample(METRIC, ldt);
        if (dp == null) {
            return Optional.empty();
        } else {
            return Optional.of(RepairHourlyInfo.decoder(dp.getVal()));
        }
    }
}
