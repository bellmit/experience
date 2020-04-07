package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static etsdb.services.AccumulatedMetricService.DAILY_SUFFIX;
import static etsdb.services.AccumulatedMetricService.HOURLY_SUFFIX;

public class HourlyMetricWriterTest {
  private HourlyMetricWriter hourlyMetricWriter;
  private MetricService metricService;
  private RepairRecorder repairRecorder;
  private String metric = "r.abc";
  private String metricHourly = "r.abc" + HOURLY_SUFFIX;
  private String metricDaily = "r.abc" + DAILY_SUFFIX;

  private LocalDateTime ldt = LocalDateTime.of(2016, 1, 1, 0, 0, 0, 0);
  private DataPoint dph = new DataPoint(metricHourly, ldt, "3.14", 192);
  private DataPoint dph1 = new DataPoint(metricHourly, ldt.plusHours(1), "3.14", 192);
  private DataPoint dph2 = new DataPoint(metricHourly, ldt.plusHours(2), "3.14", 192);
  private DataPoint dph3 = new DataPoint(metricHourly, ldt.plusHours(3), "3.14", 192);

  @Before
  public void before() throws Exception {
    metricService = Mockito.mock(MetricService.class);
    repairRecorder = Mockito.mock(RepairRecorder.class);
    hourlyMetricWriter = new HourlyMetricWriter(metricService, repairRecorder);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: write(String metric, DataPoint hourTotal)
   */
  @Test
  public void testWrite() throws Exception {
    Mockito.when(metricService.read(metricHourly, ldt, ldt.withHour(23), false)).thenReturn(
        Collections.emptyList());
    hourlyMetricWriter.write(dph);
    Mockito.verify(metricService, Mockito.times(1)).read(metricHourly, ldt, ldt.withHour(23), false);
    Mockito.verify(metricService, Mockito.times(2)).write(Matchers.any(DataPoint.class));
    Mockito.verify(metricService, Mockito.times(1)).write(dph);
    Mockito.verify(metricService, Mockito.times(1)).write(new DataPoint(metricDaily, ldt, "3.14", 192));
  }

  /**
   * Method: write(String metric, DataPoint hourTotal)
   */
  @Test
  public void testWrite2() throws Exception {
    Mockito.when(metricService.read(metricHourly, ldt, ldt.withHour(23), false)).thenReturn(
        Arrays.asList(dph, dph1, dph2, dph3));
    hourlyMetricWriter.write(dph);
    Mockito.verify(metricService, Mockito.times(1)).read(metricHourly, ldt, ldt.withHour(23), false);
    Mockito.verify(metricService, Mockito.times(2)).write(Matchers.any(DataPoint.class));
    Mockito.verify(metricService, Mockito.times(1)).write(dph);
    Mockito.verify(metricService, Mockito.times(1)).write(
        new DataPoint(metricDaily, ldt, String.valueOf(3.14 * 4), 192));
  }

  /**
   * Method: key(String metric, LocalDateTime ldt)
   */
  @Test
  public void testKey() throws Exception {
    // TODO: Test goes here...
  }


  /**
   * Method: writeDayTotal(String metric, DataPoint hourTotal)
   */
  @Test
  public void testWriteDayTotal() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = HourlyMetricWriter.getClass().getMethod("writeDayTotal", String.class,
     * DataPoint.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

}
