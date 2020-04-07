package etsdb.services;

import etsdb.domains.DataPoint;
import etsdb.domains.SkipInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SkipMetric {
    private static final String METRIC = "r.skip.metric";
    private static SkipMetric instance;
    private final MetricService metricService;

    private SkipMetric(MetricService metricService) {
        this.metricService = metricService;
    }

    public static synchronized SkipMetric getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new SkipMetric(metricService);
        }
        return instance;
    }

    public void write(SkipInfo info) {
        DataPoint dp =
                new DataPoint(METRIC, LocalDateTime.now(), SkipInfo.encoder(info), DataPoint.OK);
        metricService.write(dp);
    }

    public List<SkipInfo> read(LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = metricService.read(METRIC, begin, end, true);
        return dps.stream().map(SkipInfo::fromDataPoint).collect(Collectors.toList());
    }

    public Optional<SkipInfo> sample(LocalDateTime ldt) {
        DataPoint dp = metricService.sample(METRIC, ldt);
        if (dp == null) {
            return Optional.empty();
        } else {
            return Optional.of(SkipInfo.decoder(dp.getVal()));
        }
    }
}
