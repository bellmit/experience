package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.service.RzService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class RzServiceImplTest {
    @Autowired
    private RzService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: searchRyglqy(@NonNull String id, @NonNull Collection<RzType> rzTypes)
     */
    @Test
    public void testSearchRyglqy() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: readRz(@NonNull Collection<String> qyMap, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass)
     */
    @Test
    public void testReadRzForQyIdsRzTypesTClass() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: readRz(@NonNull Collection<String> newIds, @NonNull Collection<String> allIds, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass)
     */
    @Test
    public void testReadRzForNewIdsAllIdsRzTypesTClass() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getAllWorkType()
     */
    @Test
    public void testGetAllWorkType() throws Exception {
        List<Map<String, String>> list = service.getAllWorkType();
        System.out.println(list);
    }


} 
