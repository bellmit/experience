package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TV;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-23
 */
public interface TVDB {

    /**
     * 写时值
     *
     * @param handle 位号
     * @param tv     新值
     * @throws IOException
     */
    public void put(int handle, TV tv) throws IOException;

    /**
     * 写单个位号的值
     *
     * @param handle 位号
     * @param tvList 该位号的位，可以是任意时间
     */
    public void put(int handle, Set<TV> tvList) throws IOException;

    /**
     * 写单个位号的值
     *
     * @param tvMap Key: 位号; Value: 该位号的位，可以是任意时间
     */
    public void put(Map<Integer, Set<TV>> tvMap) throws IOException;

    /**
     * 读该位号最新实时值
     *
     * @param handle 位号
     * @return 返回历史库中，该位号最新的一条实时值
     */
    public TV get(int handle) throws IOException;

    /**
     * 读该位号指定时间的实时值
     *
     * @param handle    位号
     * @param timestamp 时间，精确到秒
     * @return 准确的返回一条实时值
     */
    public TV get(int handle, long timestamp) throws IOException;

    /**
     * 返回一个新的迭代器，能迭代该位号一段时间内的实时值
     *
     * @param handle    位号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 返回一个迭代器，可以迭代出这一段时间的值
     */
    public NavigableMap<Long, TV> iterator(int handle, long startTime, long endTime) throws IOException;

    /**
     *  删除该位号一段时间的值
     *
     * @param handle    位号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 返回一个迭代器，可以迭代出这一段时间的值
     */
    public boolean delete(int handle, long startTime, long endTime) throws IOException;
}
