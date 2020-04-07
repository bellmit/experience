package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * LogMapper Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class LogMapperTest {

    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: encode(Integer handle, TV tv)
     */
    @Test
    public void testEncode() throws Exception {
        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put(LogMapper.encode(3, new TV(dateTime.getMillis(), value)));
        bf.put(LogMapper.encode(3, new TV(dateTime.plusSeconds(1).getMillis(), value)));
        bf.put(LogMapper.encode(3, new TV(dateTime.plusSeconds(2).getMillis(), value)));
        bf.put(LogMapper.encode(3, new TV(dateTime.plusSeconds(3).getMillis(), value)));
        bf.put(LogMapper.encode(3, new TV(dateTime.plusSeconds(4).getMillis(), value)));
        bf.put(LogMapper.encode(3, new TV(dateTime.plusSeconds(5).getMillis(), value)));
        bf.rewind();
        Map<Integer, Set<TV>> decode = LogMapper.decode(bf);
        assertTrue(decode.get(3).size() == 6);
//        assertTrue(decode.get(3).get(0).equals(new TV(dateTime.getMillis(), value)));
//        assertTrue(decode.get(3).get(1).equals(new TV(dateTime.plusSeconds(1).getMillis(), value)));
//        assertTrue(decode.get(3).get(2).equals(new TV(dateTime.plusSeconds(2).getMillis(), value)));
//        assertTrue(decode.get(3).get(3).equals(new TV(dateTime.plusSeconds(3).getMillis(), value)));
//        assertTrue(decode.get(3).get(4).equals(new TV(dateTime.plusSeconds(4).getMillis(), value)));
//        assertTrue(decode.get(3).get(5).equals(new TV(dateTime.plusSeconds(5).getMillis(), value)));

        try {
            LogMapper.decode(null);// test null
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000008"));
        }

        try {
            LogMapper.encode(3, null);// test null
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000008"));
        }
    }

    /**
     * Method: decode(ByteBuffer bf)
     */
    @Test
    public void testDecode() throws Exception {

    }

} 
