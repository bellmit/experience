package etsdb.domains; 

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.time.LocalDateTime;

/** 
* RepairInfo Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 5, 2017</pre> 
* @version 1.0 
*/ 
public class RepairInfoTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: encoder(RepairInfo info) 
* 
*/ 
@Test
public void testEncoder() throws Exception {
    RepairInfo info = new RepairInfo(Sets.newHashSet("r.abc", "r.def"), LocalDateTime.now().minusDays(1), LocalDateTime.now());
    String json = RepairInfo.encoder(info);
    System.out.println(json);
    RepairInfo decoder = RepairInfo.decoder(json);
    System.out.println(decoder);
} 

/** 
* 
* Method: decoder(String json) 
* 
*/ 
@Test
public void testDecoder() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: onDataPoint(String metric, LocalDateTime ldt) 
* 
*/ 
@Test
public void testOnDataPoint() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isEmpty() 
* 
*/ 
@Test
public void testIsEmpty() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: reset() 
* 
*/ 
@Test
public void testReset() throws Exception { 
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
* Method: getRepairMap() 
* 
*/ 
@Test
public void testGetRepairMap() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = RepairInfo.getClass().getMethod("getRepairMap"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: setRepairMap(Multimap<String, LocalDateTime> repairMap) 
* 
*/ 
@Test
public void testSetRepairMap() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = RepairInfo.getClass().getMethod("setRepairMap", Multimap<String,.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
