package etsdb.processor;

import etsdb.domains.RepairInfo;
import etsdb.services.MetricService;
import etsdb.services.RepairMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public class RepairRecorder {
    private static final Logger logger = LoggerFactory.getLogger(RepairRecorder.class);
    private static RepairRecorder instance;
    private final RepairMetric repairMetric;
    private RepairInfo repairInfo = new RepairInfo();

    private RepairRecorder(MetricService metricService) {
        this.repairMetric = RepairMetric.getInstance(metricService);
        String property = System.getProperty("etsdb.processor.RepairRecorder.cycle", "1");
        Integer cycle = Integer.valueOf(property);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new WriteRecordsThread(), 1,
                cycle, TimeUnit.SECONDS);
    }

    public static synchronized RepairRecorder getInstance(MetricService metricService) {
        if (instance == null) {
            instance = new RepairRecorder(metricService);
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
                    RepairInfo info = repairInfo.reset();
                    repairMetric.write(info);
                }
            } catch (Exception e) {
                logger.error("write repair metric error.", e);
            }
        }
    }
}
