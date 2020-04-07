package etsdb.services;

import etsdb.domains.DataPoint;
import etsdb.domains.RepairInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RepairMetric {
    private static final String METRIC = "r.repair.metric";
    private static RepairMetric instance;
    private final MetricService metricService;

    private RepairMetric(MetricService metricService) {
        this.metricService = metricService;
    }

    public static synchronized RepairMetric getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new RepairMetric(metricService);
        }
        return instance;
    }

    public void write(RepairInfo info) {
        DataPoint dp =
                new DataPoint(METRIC, LocalDateTime.now(), RepairInfo.encoder(info), DataPoint.OK);
        metricService.write(dp);
    }

    public List<RepairInfo> read(LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = metricService.read(METRIC, begin, end, true);
        return dps.stream().map(RepairInfo::fromDataPoint).collect(Collectors.toList());
    }

    public Optional<RepairInfo> sample(LocalDateTime ldt) {
        DataPoint dp = metricService.sample(METRIC, ldt);
        if (dp == null) {
            return Optional.empty();
        } else {
            return Optional.of(RepairInfo.decoder(dp.getVal()));
        }
    }
}
