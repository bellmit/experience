package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FpFilterParams;
import cn.com.servyou.gxfx.basic.model.SxyType;
import cn.com.servyou.gxfx.basic.repository.FpRepository;
import cn.com.servyou.gxfx.basic.service.FpService;
import cn.com.servyou.gxfx.model.ArangoDbInitUtil;
import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Fplx;
import org.joda.time.YearMonth;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 */
public class FpServiceImplTest {
    private FpService service = new FpServiceImpl(new FpRepository(ArangoDbInitUtil.buildDataBaseAndInit("10.199.137.116", 8529, "root", "123", 1, "gxfx_dev")));

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: searchSxy(Collection<String> vIds, FpFilterParams params, Class<T> tClass)
     */
    @Test
    public void testSearchSxy() throws Exception {
        Set<String> nsrTests = service.searchSxy(Arrays.asList("Vertex/10013101001140020406", "Vertex/10113507010000015793"), new FpFilterParams(Fplx.zp, new YearMonth(2016, 1), new YearMonth(2018, 9), SxyType.sy, FpFilterParams.FilterType.top, 3));
        System.out.println(nsrTests.size());
    }

    /**
     * Method: readJxze(Collection<String> vIds, FpDataParams params)
     */
    @Test
    public void testReadJxze() throws Exception {
    }

    /**
     * Method: readXxze(Collection<String> vIds, FpDataParams params)
     */
    @Test
    public void testReadXxze() throws Exception {
    }

    /**
     * Method: readPl(Collection<String> fromSet, Collection<String> toSet, FpDataParams params, Class<T> tClass)
     */
    @Test
    public void testReadPl() throws Exception {
        Set<Fp> pls = service.readPl(Arrays.asList("Vertex/914401031909289680", "Vertex/10113501000022839181", "Vertex/10113507010000015793"), Arrays.asList("Vertex/914401031909289680", "Vertex/10113501000022839181", "Vertex/10113507010000015793"), new FpDataParams(Fplx.zp, new YearMonth(2018, 1), new YearMonth(2018, 2)), Fp.class);
        System.out.println(pls.size());
    }


} 
