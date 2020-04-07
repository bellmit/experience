package cn.com.servyou.gxfx.basic.model;

import cn.com.servyou.gxfx.model.Fplx;
import org.joda.time.YearMonth;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class FpDataParamsTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getFplx()
     */
    @Test
    public void testGetFplx() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: setFplx(Fplx fplx)
     */
    @Test
    public void testSetFplx() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getBegin()
     */
    @Test
    public void testGetBegin() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: setBegin(YearMonth begin)
     */
    @Test
    public void testSetBegin() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getEnd()
     */
    @Test
    public void testGetEnd() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: setEnd(YearMonth end)
     */
    @Test
    public void testSetEnd() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: cols()
     */
    @Test
    public void testCols() throws Exception {
        Assert.assertEquals(1, new FpDataParams(Fplx.zp, YearMonth.now(), YearMonth.now()).cols().size());
        Assert.assertEquals(1, new FpDataParams(Fplx.pp, YearMonth.now(), YearMonth.now()).cols().size());
        Assert.assertEquals(2, new FpDataParams(Fplx.all, YearMonth.now(), YearMonth.now()).cols().size());

        Assert.assertEquals(2, new FpDataParams(Fplx.zp, YearMonth.now().minusMonths(1), YearMonth.now()).cols().size());
        Assert.assertEquals(2, new FpDataParams(Fplx.pp, YearMonth.now().minusMonths(1), YearMonth.now()).cols().size());
        Assert.assertEquals(4, new FpDataParams(Fplx.all, YearMonth.now().minusMonths(1), YearMonth.now()).cols().size());
    }


} 
