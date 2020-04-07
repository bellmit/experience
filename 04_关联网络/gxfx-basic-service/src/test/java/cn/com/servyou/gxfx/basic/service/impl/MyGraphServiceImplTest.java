package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.MyGraph;
import cn.com.servyou.gxfx.basic.service.MyGraphService;
import cn.com.servyou.tdap.web.PagerBean;
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
public class MyGraphServiceImplTest {
    @Autowired
    private MyGraphService service;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getByUserId(@NonNull String userId, @NonNull MyGraph.OrderBy orderBy, int pageSize, int pageIndex)
     */
    @Test
    public void testGetByUserId() throws Exception {
        PagerBean<MyGraph> xkca = service.getByUserId("xkca", MyGraph.OrderBy.sj, 10, 1);
        System.out.println(xkca.getData().get(0).getModifyTime());
    }

    /**
     * Method: get(@NonNull String graphId)
     */
    @Test
    public void testGet() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: delete(@NonNull String graphId)
     */
    @Test
    public void testDelete() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: add(@NonNull MyGraph graph)
     */
    @Test
    public void testAdd() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: update(@NonNull MyGraph graph)
     */
    @Test
    public void testUpdate() throws Exception {
        service.update(new MyGraph("0224d195-e8a7-4bcd-9de0-96967f05b50a2", "�ԶԶ�", "", "", null, "10013100001430002459", "�й�����Ʋ����չɷ����޹�˾�Ϻ��зֹ�˾", null, null));
    }

    /**
     * Method: save(MyGraph graph)
     */
    @Test
    public void testSave() throws Exception {
//TODO: Test goes here... 
    }


} 
