package com.excenergy.tagdataserv.disk;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * LevelDB Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class PureLevelDBTest {
    public static final String FIRST_KEY = "1";
    public static final String SECOND_KEY = "2";
    public static final String THIRD_KEY = "3";
    public static final String LAST_KEY = "4";

    public static final String FIRST_VALUE = "firstValue";
    public static final String SECOND_VALUE = "secondValue";
    public static final String THIRD_VALUE = "thirdValue";
    public static final String LAST_VALUE = "lastValue";
    private DB db;
    private DB iteratorDB;
    private DBIterator iterator;

    public PureLevelDBTest() throws Exception {
        Options options = new Options();
        System.out.println("blockRestartInterval = " + options.blockRestartInterval());
        System.out.println("compressionType = " + options.compressionType());
        System.out.println("writeBufferSize = " + options.writeBufferSize());
        System.out.println("blockSize = " + options.blockSize());
        System.out.println("cacheSize = " + options.cacheSize());
        System.out.println("createIfMissing = " + options.createIfMissing());
        System.out.println("errorIfExists = " + options.errorIfExists());
        System.out.println("logger = " + options.logger());
        System.out.println("maxOpenFiles = " + options.maxOpenFiles());
        System.out.println("paranoidChecks = " + options.paranoidChecks());
        System.out.println("verifyChecksums = " + options.verifyChecksums());

        options.createIfMissing(true);
        options.compressionType(CompressionType.SNAPPY);

        db = JniDBFactory.factory.open(new File("testdb"), options);
        //        clearDB(db);
        db.close();

        iteratorDB = JniDBFactory.factory.open(new File("testiterator"), options);
        //        clearDB(iteratorDB);
        init(iteratorDB);
        iteratorDB.close();
    }

    private void init(DB db) throws IOException {
        String[] keys = new String[]{FIRST_KEY, SECOND_KEY, THIRD_KEY, LAST_KEY};
        String[] values = new String[]{FIRST_VALUE, SECOND_VALUE, THIRD_VALUE, LAST_VALUE};

        WriteBatch writeBatch = db.createWriteBatch();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = values[i];
            writeBatch.put(key.getBytes(), value.getBytes());
        }
        db.write(writeBatch);
        writeBatch.close();
    }

    @Before
    public void before() throws Exception {
    }

    private void clearDB(DB db) throws IOException {
        DBIterator iterator = db.iterator();
        iterator.seekToFirst();
        while (iterator.hasNext()) {
            db.delete(iterator.next().getKey());
        }
        iterator.close();
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testPutGetDelete() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        options.compressionType(CompressionType.SNAPPY);

        db = JniDBFactory.factory.open(new File("testdb"), options);

        db.put("testPutGetDelete".getBytes(), "testPutValue".getBytes());
        db.close();
        db = JniDBFactory.factory.open(new File("testdb"), options);
        assertTrue(Arrays.equals(db.get("testPutGetDelete".getBytes()), "testPutValue".getBytes()));
        db.delete("testPutGetDelete".getBytes());
        db.close();
        db = JniDBFactory.factory.open(new File("testdb"), options);
        assertNull(db.get("testPutGetDelete".getBytes()));
        testWriteBatch();

        db.close();
    }

    private void testWriteBatch() throws Exception {
        String[] keys = new String[]{FIRST_KEY, SECOND_KEY, THIRD_KEY, LAST_KEY};
        String[] values = new String[]{FIRST_VALUE, SECOND_VALUE, THIRD_VALUE, LAST_VALUE};

        WriteBatch writeBatch = db.createWriteBatch();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = values[i];
            writeBatch.put(key.getBytes(), value.getBytes());
        }
        db.write(writeBatch);
        writeBatch.close();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = values[i];
            assertTrue(Arrays.equals(db.get(key.getBytes()), value.getBytes()));
        }
    }

    @Test
    public void testIterator() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        options.compressionType(CompressionType.SNAPPY);

        iteratorDB = JniDBFactory.factory.open(new File("testiterator"), options);
        iterator = iteratorDB.iterator();
        testSeek();
        testSeekToFirst();
        testSeekToLast();
        testHasNext();
        testHasPrev();
        testNext();
        testPrev();
        testPeekNext();
        testPeekPrev();
        iterator.close();
        iteratorDB.close();
    }

    public void testSeek() throws Exception {
        iterator.seek(SECOND_KEY.getBytes());
        assertTrue(iterator.hasPrev());
        assertTrue(iterator.hasNext());
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), SECOND_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), SECOND_VALUE.getBytes()));

        iterator.seek(FIRST_KEY.getBytes());
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), FIRST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), FIRST_VALUE.getBytes()));

        iterator.seek(LAST_KEY.getBytes());
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), LAST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), LAST_VALUE.getBytes()));

        //        iterator.seek("5".getBytes()); LevelDB 或 LevelDBJNI的Bug，当跳到最后一个之后，hasPrev也是false
        //        assertTrue(iterator.hasNext());
        //        assertTrue(iterator.hasPrev());
    }

    public void testSeekToFirst() throws Exception {
        iterator.seekToFirst();
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), FIRST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), FIRST_VALUE.getBytes()));

        iterator.seek(LAST_KEY.getBytes());

        iterator.seekToFirst();
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), FIRST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), FIRST_VALUE.getBytes()));
    }

    public void testSeekToLast() throws Exception {
        iterator.seekToLast();
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), LAST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), LAST_VALUE.getBytes()));
    }

    public void testHasPrev() throws Exception {
        iterator.seekToFirst();
        assertFalse(iterator.hasPrev());

        iterator.peekNext();
        assertFalse(iterator.hasPrev());

        iterator.next();
        assertTrue(iterator.hasPrev());

        iterator.seekToLast();
        assertTrue(iterator.hasPrev());
    }

    public void testHasNext() throws Exception {
        iterator.seekToLast();
        iterator.next();
        assertFalse(iterator.hasNext());

        iterator.seekToFirst();
        assertTrue(iterator.hasNext());

        iterator.seek(SECOND_KEY.getBytes());
        assertTrue(iterator.hasNext());
    }

    public void testPrev() throws Exception {
        iterator.seekToLast();
        assertTrue(Arrays.equals(iterator.prev().getKey(), THIRD_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.prev().getValue(), SECOND_VALUE.getBytes()));
    }

    public void testNext() throws Exception {
        iterator.seekToFirst();
        assertTrue(Arrays.equals(iterator.next().getKey(), FIRST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.next().getValue(), SECOND_VALUE.getBytes()));
    }

    public void testPeekPrev() throws Exception {
        iterator.seekToLast();
        assertTrue(Arrays.equals(iterator.peekPrev().getKey(), THIRD_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekPrev().getValue(), THIRD_VALUE.getBytes()));
    }

    public void testPeekNext() throws Exception {
        iterator.seekToFirst();
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), FIRST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), FIRST_VALUE.getBytes()));

        iterator.seekToLast();
        assertTrue(Arrays.equals(iterator.peekNext().getKey(), LAST_KEY.getBytes()));
        assertTrue(Arrays.equals(iterator.peekNext().getValue(), LAST_VALUE.getBytes()));
    }

} 
