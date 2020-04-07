package cn.com.servyou.dao.sjck;

import cn.com.servyou.gxfx.basic.model.Nsr;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test-config.xml")
public class NsrRepositoryTest {
    @Autowired
    private NsrRepository repository;

    @Test
    public void testGetNsr() {

    }

    @Test
    public void testGetNsrdzdahBatch() {
        Map<String, Nsr> map = repository.getNsrBatch(Sets.newHashSet("1440010114419000129945226", "1330010113301000047912304", "1310010013102002270033351"));
        System.out.println(map.size());
    }

    @Test
    public void testSearch() {
        PageHelper.startPage(1, 10);
        Nsr.SearchCondition s = new Nsr.SearchCondition();
        s.setName(" ");
        s.setHyId("0D0000000");
//        s.setNsrztdm(" 03 ");
//        s.setSwjgdm("13100434500");
//        s.setZczbBegin(1.0);
//        s.setZczbEnd(1000.0);
        List<Nsr> search = repository.search(s);
        System.out.println(search.size());
    }
}
