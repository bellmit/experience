package etsdb.processor;

import etsdb.domains.SkipInfo;
import etsdb.services.MetricService;
import etsdb.services.SkipMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public class SkipRecorder {
    private static final Logger logger = LoggerFactory.getLogger(SkipRecorder.class);
    private static SkipRecorder instance;
    private final SkipMetric skipMetric;
    private SkipInfo skipInfo = new SkipInfo();

    private SkipRecorder(MetricService metricService) {
        this.skipMetric = SkipMetric.getInstance(metricService);
        String property = System.getProperty("etsdb.processor.SkipRecorder.cycle", "1");
        Integer cycle = Integer.valueOf(property);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new WriteRecordsThread(), 1,
                cycle, TimeUnit.SECONDS);
    }

    public static synchronized SkipRecorder getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new SkipRecorder(metricService);
        }
        return instance;
    }

    public void add(String metric, LocalDateTime ldt) {
        checkArgument(Objects.nonNull(metric));
        checkArgument(Objects.nonNull(ldt));

        skipInfo.onDataPoint(metric, ldt);
    }

    private class WriteRecordsThread implements Runnable {
        @Override
        public void run() {
            try {
                if (!skipInfo.isEmpty()) {
                    SkipInfo info = skipInfo.reset();
                    skipMetric.write(info);
                }
            } catch (Exception e) {
                logger.error("write repair metric error.", e);
            }
        }
    }
}
