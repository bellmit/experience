package com.excenergy.tagdataserv;

import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.*;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertNotNull;

/**
 * TagFactory Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TagFactoryTest {

    private TagFactory tagFactory = TagFactory.getInstance();
    private ArrayList<TSource> sourceList;
    private List<Device> deviceList;
    private ArrayList<TClient> tClients;
    private ArrayList<RealTag> realTagList;

    @Before
    public void before() throws Exception {
        realTagList = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
            RealTag realTag = new RealTag(i, "r.name" + i);
            realTag.setMergeInterval(Tag.HOURLY);
            realTag.setCacheMax(100);
            realTag.setPublic(true);
            realTag.setPrInterval(900);
            realTag.setValType(Tag.DOUBLE);
            realTag.setCompressAcc(60);
            realTag.setTolerance(3);
            realTagList.add(realTag);
        }

        ArrayList<VirtualTag> virtualTagList = new ArrayList<>();
        for (int i = 200; i < 300; i++) {
            VirtualTag v = new VirtualTag(i, "v.name" + i);
            v.setMergeInterval(Tag.DAILY);
            v.setCacheMax(100);
            v.setPublic(true);
            v.setValType(Tag.DOUBLE);
            v.setTolerance(3);
            virtualTagList.add(v);
        }

        Device device1 = new Device(1, "", "", "", (byte) 0x01, 1, realTagList, virtualTagList);
        Device device2 = new Device(2, "", "", "", (byte) 0x01, 1, realTagList, virtualTagList);
        Device device3 = new Device(3, "", "", "", (byte) 0x01, 1, realTagList, virtualTagList);
        Device device4 = new Device(4, "", "", "", (byte) 0x01, 1, realTagList, virtualTagList);
        Device device5 = new Device(5, "", "", "", (byte) 0x01, 1, realTagList, virtualTagList);

        deviceList = new ArrayList<>();
        deviceList.add(device1);
        deviceList.add(device2);
        deviceList.add(device3);
        deviceList.add(device4);
        deviceList.add(device5);

        sourceList = new ArrayList<>();
        sourceList.add(new TSource(1, "source1", "desc", TSource.WNC, "192.168.1.1", 1234, deviceList));
        sourceList.add(new TSource(2, "source1", "desc", TSource.WNC, "192.168.1.1", 1234, deviceList));
        sourceList.add(new TSource(3, "source1", "desc", TSource.WNC, "192.168.1.1", 1234, deviceList));

        tClients = new ArrayList<>();
        tClients.add(new TClient(1, "name1", "2", "", "192.168.1.1"));
        tClients.add(new TClient(2, "name2", "3", "", "192.168.1.2"));
        tClients.add(new TClient(3, "name3", "1", "", "192.168.1.3"));
        tClients.add(new TClient(4, "name4", "4", "", "192.168.1.4"));
        tClients.add(new TClient(5, "name5", "6", "", "192.168.1.5"));

        TagMeta tagMeta = new TagMeta(sourceList, tClients);

        tagFactory.put(tagMeta);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
        assertNotNull(TagFactory.getInstance());
    }

    /**
     * Method: checkHandle(int handle)
     */
    @Test
    public void testCheckHandle() throws Exception {
        TagFactory tagFactory = TagFactory.getInstance();
        try {
            tagFactory.checkHandle(-1); // tag not exist
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000002"));
        }
    }

    /**
     * Method: get(int handle)
     */
    @Test
    public void testGet() throws Exception {
        assertNotNull(tagFactory.get(111));
    }

    /**
     * Method: put(TagMeta tagMeta)
     */
    @Test
    public void testPutTagMeta() throws Exception {
        // before 中
    }

    /**
     * Method: put(Tag tag)
     */
    @Test
    public void testPutTag() throws Exception {
        RealTag realTag = new RealTag(555, "r.dfjakfdds");
        realTag.setMergeInterval(Tag.HOURLY);
        realTag.setCacheMax(100);
        realTag.setPublic(true);
        realTag.setPrInterval(900);
        realTag.setValType(Tag.DOUBLE);
        realTag.setCompressAcc(60);
        realTag.setTolerance(3);
        tagFactory.put(realTag);
        assertTrue(realTag == tagFactory.get(555));
    }

    /**
     * Method: getSource(int sourceId)
     */
    @Test
    public void testGetSource() throws Exception {
        TSource source = tagFactory.getSource(1);
        TestCase.assertTrue(sourceList.contains(source));
    }

    /**
     * Method: getClient(String name)
     */
    @Test
    public void testGetClient() throws Exception {
        TClient client = tagFactory.getClient("name1");
        TestCase.assertTrue(tClients.contains(client));
    }

    /**
     * Method: getClientIpSet()
     */
    @Test
    public void testGetClientIpSet() throws Exception {
        Set<String> clientIpSet = tagFactory.getClientIpSet();
        String[] strings = {"192.168.1.1", "192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5"};
        List<String> stringList = Arrays.asList(strings);
        TestCase.assertTrue(clientIpSet.containsAll(stringList));
        TestCase.assertTrue(stringList.containsAll(clientIpSet));
    }

    /**
     * Method: enumTag(String regex)
     */
    @Test
    public void testEnumTag() throws Exception {
        List<Tag> tags = tagFactory.enumTag("r.");
        TestCase.assertTrue(tags.containsAll(realTagList));
        TestCase.assertTrue(realTagList.containsAll(tags));
    }

    /**
     * Method: enumAll()
     */
    @Test
    public void testEnumAll() throws Exception {
        List<TSource> tSources = tagFactory.enumAll();
        assertTrue(tSources == sourceList);
    }

} 
