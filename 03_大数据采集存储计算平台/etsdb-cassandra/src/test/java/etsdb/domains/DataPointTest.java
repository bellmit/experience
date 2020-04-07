package etsdb.domains; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;

import java.time.LocalDateTime;

/** 
* DataPoint Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 8, 2017</pre> 
* @version 1.0 
*/ 
public class DataPointTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getMetric() 
* 
*/ 
@Test
public void testGetMetric() throws Exception {
    DataPoint dp = new DataPoint("r.abc", LocalDateTime.now(), String.valueOf(Double.NaN), DataPoint.OK);
    System.out.println(dp.doubleVal());
} 

/** 
* 
* Method: setMetric(String metric) 
* 
*/ 
@Test
public void testSetMetric() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getTime() 
* 
*/ 
@Test
public void testGetTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setTime(String time) 
* 
*/ 
@Test
public void testSetTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setTimestamp(long timestamp) 
* 
*/ 
@Test
public void testSetTimestamp() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getTimestamp() 
* 
*/ 
@Test
public void testGetTimestamp() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getDate() 
* 
*/ 
@Test
public void testGetDate() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getDateTime() 
* 
*/ 
@Test
public void testGetDateTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setDateTime(LocalDateTime dateTime) 
* 
*/ 
@Test
public void testSetDateTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getVal() 
* 
*/ 
@Test
public void testGetVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setVal(String val) 
* 
*/ 
@Test
public void testSetValVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getQuality() 
* 
*/ 
@Test
public void testGetQuality() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: quality() 
* 
*/ 
@Test
public void testQuality() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setQuality(Quality quality) 
* 
*/ 
@Test
public void testSetQualityQuality() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: valid() 
* 
*/ 
@Test
public void testValid() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: val() 
* 
*/ 
@Test
public void testVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: boolVal() 
* 
*/ 
@Test
public void testBoolVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: intVal() 
* 
*/ 
@Test
public void testIntVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isDouble() 
* 
*/ 
@Test
public void testIsDouble() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: doubleVal() 
* 
*/ 
@Test
public void testDoubleVal() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: equals(Object o) 
* 
*/ 
@Test
public void testEquals() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: hashCode() 
* 
*/ 
@Test
public void testHashCode() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: toString() 
* 
*/ 
@Test
public void testToString() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: checkValid() 
* 
*/ 
@Test
public void testCheckValid() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: valueOf(int val) 
* 
*/ 
@Test
public void testValueOf() throws Exception { 
//TODO: Test goes here... 
} 


} 
