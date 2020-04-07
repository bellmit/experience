package etsdb.services;

import com.google.common.collect.Lists;
import etsdb.domains.DataPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CassandraMetricServiceTest {

    private MetricService metricService = new CassandraMetricService("10.30.22.17", "10.30.22.23");

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: write(DataPoint dp)
     */
    @Test
    public void testWriteDp() throws Exception {
//        DataPoint dp = new DataPoint("r.abc", System.currentTimeMillis(), "3.14");
//        metricService.write(dp);

        DataPoint sample = metricService.sample("r.repair.metric");
        System.out.println(sample);

//        metricService.read("r.abc", LocalDateTime.of(2017, 7, 10, 14, 0));
    }

    /**
     * Method: write(List<DataPoint> dps)
     */
    @Test
    public void testWriteDps() throws Exception {
        int count = 100000;
        long time = System.currentTimeMillis();
        ArrayList<DataPoint> list = Lists.newArrayList();
        String val = "3.141592654";
        for (int i = 0; i < count; i++) {
            DataPoint dp = new DataPoint("r.abc", time - i * 1000, val);
            list.add(dp);
        }

        long l = System.currentTimeMillis();
//        list.stream().forEach(metricService::write);
//        System.out.println(System.currentTimeMillis() - l);
//
//        l = System.currentTimeMillis();
//        list.parallelStream().forEach(metricService::write);
//        System.out.println(System.currentTimeMillis() - l);

//        l = System.currentTimeMillis();
        metricService.write(list);
        System.out.println(System.currentTimeMillis() - l);

        System.out.println("finish.");
    }

    /**
     * Method: sample(@NotNull String metric, long timestamp, boolean valid)
     */
    @Test
    public void testSample() throws Exception {
        System.out.println(MetricService.metric("r.abc.hourly"));
    }

    /**
     * Method: read(@NotNull String metric, long start, long end, boolean onlyValid)
     */
    @Test
    public void testRead() throws Exception {
        List<DataPoint> list = Arrays.asList("r.abc", "r.abc").parallelStream().map(m -> metricService.sample(m)).collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * Method: revise(DataPoint dp)
     */
    @Test
    public void testRevise() throws Exception {
//TODO: Test goes here... 
    }


    /**
     * Method: fromResultSet(ResultSet r, boolean valid)
     */
    @Test
    public void testFromResultSet() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraMetricService.getClass().getMethod("fromResultSet", ResultSet.class, boolean.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: insertStmt(DataPoint dp)
     */
    @Test
    public void testInsertStmt() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraMetricService.getClass().getMethod("insertStmt", DataPoint.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: sampleStmt(boolean valid)
     */
    @Test
    public void testSampleStmt() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraMetricService.getClass().getMethod("sampleStmt", boolean.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: queryStmt(boolean valid)
     */
    @Test
    public void testQueryStmt() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = CassandraMetricService.getClass().getMethod("queryStmt", boolean.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
