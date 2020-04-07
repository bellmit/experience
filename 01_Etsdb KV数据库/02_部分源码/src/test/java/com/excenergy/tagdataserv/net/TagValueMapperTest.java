package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TagValueMapper Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TagValueMapperTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: trans(TagValue tagValue)
     */
    @Test
    public void testTransTagValue() throws Exception {
        testType(Tag.INT, 3);
        testType(Tag.DOUBLE, 1.414);
        testType(Tag.DOUBLE, "1.414");
        testType(Tag.STR, "kfdlas;lkjksd");
        testType(Tag.BOOL, true);
        testType(Tag.BOOL, "true");
    }

    public void testType(byte valType, Object val) {
        TagValue tagValue = new TagValue(new DateTime(2014, 7, 7, 7, 7, 7), valType, val, TagValue.GOOD);
        TV trans = TagValueMapper.trans(tagValue);
        TagValue trans1 = TagValueMapper.trans(trans, valType);
        Assert.assertEquals(tagValue, trans1);
    }

    /**
     * Method: trans(TV tv, short valType)
     */
    @Test
    public void testTransForTvValType() throws Exception {
//TODO: Test goes here... 
    }


} 
