package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.service.NsrService;
import cn.com.servyou.tdap.web.PagerBean;
import cn.com.servyou.tdap.web.RestResultGenerator;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
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
public class NsrServiceImplTest {
    @Autowired
    private NsrService nsrService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getNsrBatch(String nsrdzdah)
     */
    @Test
    public void testGetNsrNsrdzdah() throws Exception {
        Nsr nsr = nsrService.getNsr("10013101001010017878");
        System.out.println(JSON.toJSONString(nsr));
    }

    /**
     * Method: getNsrBatch(Collection<String> nsrdzdahs)
     */
    @Test
    public void testGetNsrNsrdzdahs() throws Exception {
        Map<String, Nsr> nsrBatch = nsrService.getNsrBatch(Sets.<String>newHashSet("10013101001150046909", "10013101001010052536"));
        System.out.println(JSON.toJSONString(nsrBatch));
    }

    /**
     * Method: search(String name, int pageSize, int pageIndex)
     */
    @Test
    public void testSearch() throws Exception {
        Nsr.SearchCondition s = new Nsr.SearchCondition();
        s.setHyId("123456789");
        PagerBean<Nsr> search = nsrService.search(s);
        System.out.println(JSON.toJSONString(RestResultGenerator.genResult(search)));
        System.out.println(new ObjectMapper().writeValueAsString(RestResultGenerator.genResult(search)));
        System.out.println(search.getTotal());
        System.out.println(search.getSize());
        System.out.println(search.getPageIndex());
        System.out.println(search.getPageNum());
        System.out.println(search.getPageSize());
    }


} 
