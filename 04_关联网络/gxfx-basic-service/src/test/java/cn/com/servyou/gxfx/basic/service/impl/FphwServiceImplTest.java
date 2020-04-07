package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.HwJe;
import cn.com.servyou.gxfx.basic.model.QyTop;
import cn.com.servyou.gxfx.basic.service.FphwService;
import cn.com.servyou.gxfx.model.Fplx;
import org.joda.time.YearMonth;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class FphwServiceImplTest {

    @Autowired
    private FphwService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: qyTop(@NonNull String nsrdzdah, int top, @NonNull FpDataParams params)
     */
    @Test
    public void testQyTop() throws Exception {
        QyTop qyTop = service.qyTop("10013101001010026144", 5, new FpDataParams(Fplx.zp, new YearMonth(2016, 1), new YearMonth(2018, 10)));
        System.out.println(qyTop.getJxTop().size());
        System.out.println(qyTop.getXxTop().size());
    }

    /**
     * Method: jyTop(@NonNull String xfnsrdzdah, @NonNull String gfnsrdzdah, int top, @NonNull FpDataParams params)
     */
    @Test
    public void testJyTop() throws Exception {
        List<HwJe> jyTop = service.jyTop("12345", "12345", 5, new FpDataParams(Fplx.zp, new YearMonth(2018, 1), new YearMonth(2018, 10)));
        System.out.println(jyTop.size());
    }


} 
