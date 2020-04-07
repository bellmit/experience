package cn.com.servyou.gxfx.basic.repository;

import cn.com.servyou.gxfx.basic.model.SxyType;
import cn.com.servyou.gxfx.model.ArangoDbInitUtil;
import cn.com.servyou.gxfx.model.Fp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 */
public class FpRepositoryTest {

    private FpRepository repository = new FpRepository(ArangoDbInitUtil.buildDataBaseAndInit("10.199.137.116", 8529, "root", "123", 1, "gxfx_dev"));

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: searchSy(Collection<String> vIds, FpFilterParams params)
     */
    @Test
    public void testSearchTop() throws Exception {
        Set<String> vs = repository.searchTop(Arrays.asList("Vertex/10113501000022839181", "Vertex/10113507010000015793"), SxyType.sy, Arrays.asList("zp201801", "zp201802", "zp201709", "zp201710"), 3);
        System.out.println(vs.size());
    }

    /**
     * Method: searchSy(Collection<String> vIds, FpFilterParams params)
     */
    @Test
    public void testSearchJyzb() throws Exception {
        Set<String> vs = repository.searchJyzb(Arrays.asList("Vertex/10113501000022839181", "Vertex/10113507010000015793"), SxyType.sy, Arrays.asList("zp201801", "zp201802", "zp201709", "zp201710"), 0.2);
        System.out.println(vs.size());
    }

    /**
     * Method: searchSy(Collection<String> vIds, FpFilterParams params)
     */
    @Test
    public void testReadZe() throws Exception {
        Map<String, Double> map = repository.readZe(Arrays.asList("Vertex/10113501000022839181", "Vertex/10113507010000015793"), Arrays.asList("zp201801", "zp201802", "zp201709", "zp201710"), SxyType.sy);
        System.out.println(map.size());
    }

    @Test
    public void testPl() throws Exception {
        Set<Fp> pl = repository.pl(Arrays.asList("Vertex/914401031909289680", "Vertex/10113501000022839181", "Vertex/10113507010000015793"), Arrays.asList("Vertex/10113501000022839181", "Vertex/10113507010000015793"), Arrays.asList("zp201801", "zp201802", "zp201709", "zp201710"), Fp.class);
        System.out.println(pl.size());
    }


} 
