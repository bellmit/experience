package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.service.SbService;
import cn.com.servyou.gxfx.basic.view.QySbResponseEntity;
import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class SbServiceImplTest {
    @Autowired
    private SbService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getQySb(String nsrdzdah, String begin, String end)
     */
    @Test
    public void testGetQySb() throws Exception {
        QySbResponseEntity qySb = service.getQySb("1110010111101000050035237", "201601", "201812");
        System.out.println(JSON.toJSONString(qySb));
    }


    /**
     * Method: getZzsEntity(String nsrdzdah, String begin, String end)
     */
    @Test
    public void testGetZzsEntity() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = SbServiceImpl.getClass().getMethod("getZzsEntity", String.class, String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getXfsEntity(String nsrdzdah, String begin, String end)
     */
    @Test
    public void testGetXfsEntity() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = SbServiceImpl.getClass().getMethod("getXfsEntity", String.class, String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getSdsEntity(String nsrdzdah)
     */
    @Test
    public void testGetSdsEntity() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = SbServiceImpl.getClass().getMethod("getSdsEntity", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getQysdsRkse(String nsrdzdah, String year)
     */
    @Test
    public void testGetQysdsRkse() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = SbServiceImpl.getClass().getMethod("getQysdsRkse", String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
