package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.jetbrains.annotations.NotNull;

public class AccumulateProcessor {
    private MetricService metricService;
    private AccumulateHourSampleCache sampleCache;
    private AccumulateHourTotalProcessor hourTotalProcessor;
    private AccumulateAutoRepairHisHourly autoRepairHisHourly;

    public AccumulateProcessor(MetricService metricService) {
        this.metricService = metricService;
        this.sampleCache = new AccumulateHourSampleCache(metricService);
        HourlyMetricWriter hourlyMetricWriter = new HourlyMetricWriter(metricService, RepairRecorder.getInstance(metricService));
        this.hourTotalProcessor = new AccumulateHourTotalProcessor(metricService, hourlyMetricWriter);
        this.autoRepairHisHourly = new AccumulateAutoRepairHisHourly(metricService, hourlyMetricWriter);
    }

    /**
     * realMetric.isAccumulated() && realMetric.getTolerance() <= 3600 && dataPoint.valid()
     *
     * @param dataPoint dataPoint
     */
    public void process(@NotNull DataPoint dataPoint) {
        // 1. calculate hour total
        AccumulateHourTotalCalc hourTotalCalc =
                new AccumulateHourTotalCalc(metricService, sampleCache, dataPoint);

        // 2. check hour total and save them
        hourTotalProcessor.process(hourTotalCalc.thisHourTotal());
        hourTotalProcessor.process(hourTotalCalc.nextHourTotal());

        // 3. check repair hour total auto
        autoRepairHisHourly.checkAndRepair(dataPoint);
    }
}
