package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static etsdb.processor.AccumulateHourSampleCache.hourDateTime;
import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccumulateHourSampleCacheTest {
  private AccumulateHourSampleCache hourSampleCache;
  private MetricService metricService;

  @Before
  public void before() throws Exception {
    metricService = mock(MetricService.class);
    hourSampleCache = new AccumulateHourSampleCache(metricService);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: put(DataPoint dataPoint)
   */
  @Test
  public void testPut() throws Exception {

  }

  /**
   * Method: sample(String metric, LocalDateTime ldt)
   */
  @Test
  public void testSample() throws Exception {
    when(metricService.sample("r.abc", of(2016, 1, 1, 9, 0, 0))).thenReturn(null);
    assertNull(hourSampleCache.sample("r.abc", of(2016, 1, 1, 9, 0, 0)));

    DataPoint dp2 = new DataPoint("r.abc", of(2016, 1, 1, 8, 0), "3.14", 192);
    when(metricService.sample("r.abc", of(2016, 1, 1, 9, 0, 0))).thenReturn(dp2);
    assertNull(hourSampleCache.sample("r.abc", of(2016, 1, 1, 9, 0, 0)));

    DataPoint dp = new DataPoint("r.abc", of(2016, 1, 1, 9, 0), "3.14", 192);
    when(metricService.sample("r.abc", of(2016, 1, 1, 9, 0))).thenReturn(dp);
    assertEquals(dp, hourSampleCache.sample("r.abc", of(2016, 1, 1, 9, 0, 0)));

    when(metricService.sample("r.abc", of(2016, 1, 1, 9, 0, 0))).thenReturn(null);
    assertEquals(dp, hourSampleCache.sample("r.abc", of(2016, 1, 1, 9, 0, 0)));
  }

  /**
   * Method: hourDateTime(LocalDateTime ldt)
   */
  @Test
  public void testHourDateTime() throws Exception {
    assertEquals(of(2016, 1, 1, 7, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 0, 0, 0)));
    assertEquals(of(2016, 1, 1, 7, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 0, 0)));
    assertEquals(of(2016, 1, 1, 7, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 0)));

    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 0, 0, 1)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 0, 59, 100)));

    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 2, 0)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 12, 0)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 32, 0)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 52, 0)));

    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 8, 59, 59)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 9, 0, 0, 0)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 9, 0, 0)));
    assertEquals(of(2016, 1, 1, 8, 0, 0, 0), hourDateTime(of(2016, 1, 1, 9, 0)));
    assertEquals(of(2016, 1, 1, 9, 0, 0, 0), hourDateTime(of(2016, 1, 1, 9, 0, 0, 1)));
  }
}
