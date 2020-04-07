package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AccumulateHourTotalProcessorTest {

  private AccumulateHourTotalProcessor totalProcessor;
  private MetricService metricService;
  private HourlyMetricWriter writer;

  @Before
  public void before() throws Exception {
    writer = mock(HourlyMetricWriter.class);
    totalProcessor = new AccumulateHourTotalProcessor(metricService, writer);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: process(String metric, DataPoint hourTotal)
   */
  @Test
  public void testProcess() throws Exception {
    // TODO: Test goes here...
  }


  /**
   * Method: isLeap(@NotNull DataPoint hourTotal)
   */
  @Test
  public void testIsLeap() throws Exception {
    Method method = totalProcessor.getClass().getDeclaredMethod("isLeap", DataPoint.class);
    method.setAccessible(true);
    metricService = mock(MetricService.class);
    totalProcessor = new AccumulateHourTotalProcessor(metricService, writer);
    LocalDateTime ldt = of(2016, 1, 1, 8, 0);
    assertTrue((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "-3.14",
        192)));
    assertFalse((boolean) method.invoke(totalProcessor,
        new DataPoint("r.abc.hourly", ldt, "0", 192)));

    assertFalse((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "3.14",
        192)));
    verify(metricService, times(1)).read("r.abc.hourly", ldt.minusMonths(1), ldt, true);

    assertFalse((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "3.14",
        192)));
    verify(metricService, times(2)).read("r.abc.hourly", ldt.minusMonths(1), ldt, true);


    metricService = mock(MetricService.class);
    totalProcessor = new AccumulateHourTotalProcessor(metricService, writer);
    List<DataPoint> list = new ArrayList<>();
    for (LocalDateTime dt = ldt; dt.plusDays(3).isAfter(ldt); dt = dt.minusHours(1)) {
      list.add(new DataPoint("r.abc.hourly", dt, "3.14", 192));
    }
    when(
        metricService.read(eq("r.abc.hourly"), any(LocalDateTime.class), any(LocalDateTime.class),
            eq(true))).thenReturn(list);
    assertFalse((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "3.14",
        192)));
    assertFalse((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "313",
        192)));
    assertFalse((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "314",
        192)));
    assertTrue((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "315",
        192)));
    assertTrue((boolean) method.invoke(totalProcessor, new DataPoint("r.abc.hourly", ldt, "320",
        192)));
  }

  /**
   * Method: getAvg(DataPoint hourTotal)
   */
  @Test
  public void testGetAvg() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = AccumulateHourTotalProcessor.getClass().getMethod("getAvg",
     * DataPoint.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

  /**
   * Method: avg(DataPoint hourTotal)
   */
  @Test
  public void testAvg() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = AccumulateHourTotalProcessor.getClass().getMethod("avg",
     * DataPoint.class); method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

}
