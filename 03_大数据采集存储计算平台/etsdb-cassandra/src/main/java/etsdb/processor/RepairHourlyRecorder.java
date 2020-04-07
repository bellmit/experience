package etsdb.processor;

import etsdb.domains.RepairHourlyInfo;
import etsdb.services.MetricService;
import etsdb.services.RepairHourlyMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public class RepairHourlyRecorder {
    private static final Logger logger = LoggerFactory.getLogger(RepairHourlyRecorder.class);
    private static RepairHourlyRecorder instance;
    private final RepairHourlyMetric repairMetric;
    private RepairHourlyInfo repairInfo = new RepairHourlyInfo();

    private RepairHourlyRecorder(MetricService metricService) {
        this.repairMetric = RepairHourlyMetric.getInstance(metricService);
        String property = System.getProperty("etsdb.processor.RepairRecorder.cycle", "1");
        Integer cycle = Integer.valueOf(property);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new WriteRecordsThread(), 1,
                cycle, TimeUnit.SECONDS);
    }

    public static synchronized RepairHourlyRecorder getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new RepairHourlyRecorder(metricService);
        }
        return instance;
    }

    public void add(String metric, LocalDateTime ldt) {
        checkArgument(Objects.nonNull(metric));
        checkArgument(Objects.nonNull(ldt));

        repairInfo.onDataPoint(metric, ldt);
    }

    private class WriteRecordsThread implements Runnable {
        @Override
        public void run() {
            try {
                if (!repairInfo.isEmpty()) {
                    RepairHourlyInfo info = repairInfo.reset();
                    repairMetric.write(info);
                }
            } catch (Exception e) {
                logger.error("write repair metric error.", e);
            }
        }
    }
}
