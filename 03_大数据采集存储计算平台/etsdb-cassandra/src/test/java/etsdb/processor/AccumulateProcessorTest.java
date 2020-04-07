package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.CassandraMetricService;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AccumulateProcessorTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: process(@NotNull DataPoint dataPoint)
     */
    @Test
    public void testProcess() throws Exception {
        MetricService metricService = new CassandraMetricService("10.30.22.17");
        AccumulateProcessor processor = new AccumulateProcessor(metricService);
        processor.process(new DataPoint("r.abc", LocalDateTime.now(), "3.14", 192));
    }
}
