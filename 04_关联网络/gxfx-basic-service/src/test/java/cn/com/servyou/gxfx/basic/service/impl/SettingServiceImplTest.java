package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.service.SettingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class SettingServiceImplTest {
    @Autowired
    private SettingService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: loadSettings(String userId)
     */
    @Test
    public void testLoadSettings() throws Exception {
        Map<String, String> xkca = service.loadSettings("xkca");
        System.out.println(xkca);
    }

    /**
     * Method: saveSettings(final String userId, Map<String, String> settings)
     */
    @Test
    public void testSaveSettings() throws Exception {
        service.saveSettings("test", service.loadSettings("xkca"));
    }


} 
