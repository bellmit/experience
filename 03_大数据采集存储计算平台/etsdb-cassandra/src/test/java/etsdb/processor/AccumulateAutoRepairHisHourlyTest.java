package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.of;
import static org.mockito.Mockito.*;

public class AccumulateAutoRepairHisHourlyTest {

  private MetricService metricService;
  private HourlyMetricWriter hourlyMetricWriter;

  @Before
  public void before() throws Exception {}

  @After
  public void after() throws Exception {}

  /**
   * Method: checkAndRepair(@NotNull DataPoint dataPoint)
   */
  @Test
  public void testCheckAndRepair() throws Exception {
    metricService = mock(MetricService.class);
    hourlyMetricWriter = mock(HourlyMetricWriter.class);
    AccumulateAutoRepairHisHourly repairHisHourly =
        new AccumulateAutoRepairHisHourly(metricService, hourlyMetricWriter);
    LocalDateTime ldt = of(2016, 1, 1, 8, 32);

    when(metricService.sample(eq("r.abc"), any(LocalDateTime.class))).thenReturn(
        new DataPoint("r.abc", ldt.minusHours(1), "3.14", 192));
    repairHisHourly.checkAndRepair(new DataPoint("r.abc", ldt, "314", 192));
    verify(hourlyMetricWriter, times(0)).write(any(DataPoint.class));

    when(metricService.sample(eq("r.abc"), any(LocalDateTime.class))).thenReturn(
        new DataPoint("r.abc", ldt.minusHours(2), "3.14", 192));
    repairHisHourly.checkAndRepair(new DataPoint("r.abc", ldt, "314", 192));
    verify(hourlyMetricWriter, times(1)).write(any(DataPoint.class));

    when(metricService.sample(eq("r.abc"), any(LocalDateTime.class))).thenReturn(
        new DataPoint("r.abc", ldt.minusHours(30), "3.14", 192));
    repairHisHourly.checkAndRepair(new DataPoint("r.abc", ldt, "314", 192));
    verify(hourlyMetricWriter, times(1 + 29)).write(any(DataPoint.class));
  }


}
