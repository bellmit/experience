package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.CompanySign;
import cn.com.servyou.gxfx.basic.service.SignService;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Map;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class SignServiceImplTest {
    @Autowired
    private SignService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getAllPreSetSign()
     */
    @Test
    public void testGetAllPreSetSign() throws Exception {
        System.out.println(service.getAllPreSetSign().size());
    }

    /**
     * Method: getCompanySign(@NonNull String companyCode)
     */
    @Test
    public void testGetCompanySign() throws Exception {
        System.out.println(service.getCompanySign("1110010111101000049483704").size());
    }

    /**
     * Method: addCompanySign(@NonNull CompanySign companySign)
     */
    @Test
    public void testAddCompanySign() throws Exception {
        service.addCompanySign(new CompanySign("1", "1", "1", "1", "1", "1", null));
    }

    /**
     * Method: deleteCompanySign(@NonNull String companyCode, @NonNull String signId)
     */
    @Test
    public void testDeleteCompanySign() throws Exception {
        service.deleteCompanySign("1", "1");
    }

    /**
     * Method: checkSignName(@NonNull String companyCode, @NonNull String signName)
     */
    @Test
    public void testCheckSignName() throws Exception {
        Boolean b = service.checkSignName("1110010111101000049483704", "±‰∆±");
        System.out.println(b);
    }

    /**
     * Method: getSignMap(@NonNull Collection<String> nsrdzdahSet)
     */
    @Test
    public void testGetSignMap() throws Exception {
        Map<String, Collection<String>> signMap = service.getSignMap(Sets.newHashSet("1110010111101000049483704"));
        System.out.println(signMap.size());
    }


} 
