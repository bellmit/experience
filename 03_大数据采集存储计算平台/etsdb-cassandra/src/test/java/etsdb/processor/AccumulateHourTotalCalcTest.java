package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static etsdb.services.AccumulatedMetricService.HOURLY_SUFFIX;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccumulateHourTotalCalcTest {
  private static final String METRIC = "r.abc";
  private static final String HOUR_METRIC = "r.abc" + HOURLY_SUFFIX;
  private AccumulateHourTotalCalc totalCalc;
  private DataPoint current;
  private AccumulateHourSampleCache sampleCache;
  private MetricService metricService;

  @Before
  public void before() throws Exception {
    metricService = mock(MetricService.class);
    sampleCache = mock(AccumulateHourSampleCache.class);
    current = new DataPoint(METRIC, of(2016, 1, 1, 8, 32, 18, 255), "3.14", 192);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: thisHourTotal()
   */
  @Test
  public void testThisHourTotal() throws Exception {
    // 1. (7:00, 9:00] has nun value
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(null);
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.thisHourTotal());

    // 2. (8:00, 9:00] not, (7:00, 8:00] has data
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 7, 57, 18, 255), "3.1", 192));
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(null);
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 8, 0), valueOf(3.14 - 3.1), 192),
        totalCalc.thisHourTotal());

    // 3. (8:00, 9:00] has, (7:00, 8:00] has too, cur < sample
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 7, 57, 18, 255), "3.1", 192));
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 50, 18, 255), "3.6", 192));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.thisHourTotal());

    // 4. (8:00, 9:00] has, (7:00, 8:00] has too, cur >= sample
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 7, 57, 18, 255), "3.0", 192));
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 5, 18, 255), "3.1", 192));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 8, 0), valueOf(3.14 - 3.0), 192),
        totalCalc.thisHourTotal());

    // 5. (7:00, 8:00] non, (8:00, 9:00] current >= sample
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 15), "3.12", 192));
    when(metricService.read(METRIC, of(2016, 1, 1, 8, 0, 0, 1), of(2016, 1, 1, 9, 0), true))
        .thenReturn(
            Collections.singletonList(new DataPoint(METRIC, of(2016, 1, 1, 8, 1), "3.1", 192)));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 8, 0), valueOf(3.14 - 3.10), 170),
        totalCalc.thisHourTotal());

    // 6. (7:00, 8:00] non, (8:00, 9:00] current < sample && current > first
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 50), "3.2", 192));
    when(metricService.read(METRIC, of(2016, 1, 1, 8, 0, 0, 1), of(2016, 1, 1, 9, 0), true))
        .thenReturn(
            Collections.singletonList(new DataPoint(METRIC, of(2016, 1, 1, 8, 1), "3.1", 192)));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.thisHourTotal());

    // 7. (7:00, 8:00] non, (8:00, 9:00] current < sample && current <= first
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 8, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 50, 18, 255), "3.6", 192));
    when(metricService.read(METRIC, of(2016, 1, 1, 8, 0, 0, 1), of(2016, 1, 1, 9, 0), true))
        .thenReturn(
            Collections.singletonList(new DataPoint(METRIC, of(2016, 1, 1, 8, 40), "3.2", 192)));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 8, 0), valueOf(3.6 - 3.14), 170),
        totalCalc.thisHourTotal());
  }

  /**
   * Method: nextHourTotal()
   */
  @Test
  public void testNextHourTotal() throws Exception {
    // 1. (8:00, 9:00] sample == null, (9:00, 10:00] sample == null
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 10, 0))).thenReturn(null);
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.nextHourTotal());

    // 2. (8:00, 9:00] sample == null, (9:00, 10:00] sample != null
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 10, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 9, 50, 18, 255), "7.6", 192));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 9, 0), valueOf(7.6 - 3.14), 192),
        totalCalc.nextHourTotal());

    // 3. (8:00, 9:00] sample != null, (9:00, 10:00] sample == null
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 50, 18, 255), "3.6", 192));
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 10, 0))).thenReturn(null);
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.nextHourTotal());

    // 4. (8:00, 9:00] sample != null, (9:00, 10:00] sample != null, cur >= sample
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(null);
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 10, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 9, 50, 18, 255), "7.6", 192));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertEquals(new DataPoint(HOUR_METRIC, of(2016, 1, 1, 9, 0), valueOf(7.6 - 3.14), 192),
        totalCalc.nextHourTotal());

    // 5. (8:00, 9:00] sample != null, (9:00, 10:00] sample != null, cur < sample
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 9, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 8, 50, 18, 255), "3.6", 192));
    when(sampleCache.sample(METRIC, of(2016, 1, 1, 10, 0))).thenReturn(
        new DataPoint(METRIC, of(2016, 1, 1, 9, 50, 18, 255), "7.6", 192));
    totalCalc = new AccumulateHourTotalCalc(metricService, sampleCache, current);
    assertNull(totalCalc.nextHourTotal());
  }


}
