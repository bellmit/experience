package cn.com.servyou.gxfx.basic.repository;

import cn.com.servyou.gxfx.model.ArangoDbInitUtil;
import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 */
public class RzRepositoryTest {
    private RzRepository repository = new RzRepository(ArangoDbInitUtil.buildDataBaseAndInit("10.199.137.116", 8529, "root", "123", 1, "gxfx_dev"));

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: searchRyglqy(String id, Set<RzType> rzTypes, Class<T> tClass)
     */
    @Test
    public void testSearchRyglqy() throws Exception {
        Set<String> strings = repository.searchRyglqy(null, null);
        Set<String> nsrTests = repository.searchRyglqy("Vertex/CQGS11500100000000518035", Sets.newHashSet(RzType.fr, RzType.bsr));
        System.out.println(nsrTests.size());
    }

    @Test
    public void testReadRz1() throws Exception {
        Set<Rz> rzs = repository.readRz(Arrays.asList("Vertex/330100188000518035", "Vertex/91370782L02520790E", "Vertex/CQGS11500100000000518035"), Arrays.asList(RzType.fr, RzType.cw), Rz.class);
        System.out.println(rzs);
    }

    @Test
    public void testReadRz2() throws Exception {
        Set<Rz> rzs = repository.readRz(Arrays.asList("Vertex/330100188000518035", "Vertex/91370782L02520790E"), Arrays.asList("Vertex/330100188000518035", "Vertex/91370782L02520790E", "Vertex/CQGS11500100000000518035"), Arrays.asList(RzType.fr, RzType.cw), Rz.class);
        System.out.println(rzs);
    }


    /**
     * Method: allType(Collection<RzType> rzTypes)
     */
    @Test
    public void testAllType() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = RzRepository.getClass().getMethod("allType", Collection<RzType>.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
