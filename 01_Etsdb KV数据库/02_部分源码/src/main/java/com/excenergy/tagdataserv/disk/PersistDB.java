package com.excenergy.tagdataserv.disk;

import java.io.IOException;
import java.util.Map;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public interface PersistDB {

    /**
     * 写入实时值
     *
     * @param key   实时值的Key，Handle
     * @param value 实时值
     * @throws IOException
     */
    public void putReal(byte[] key, byte[] value) throws IOException;

    public void putReal(Map<byte[], byte[]> map) throws IOException;

    /**
     * 读实时值
     *
     * @param key 实时值的Key，Handle
     * @return value 实时值
     * @throws IOException
     */
    public byte[] getReal(byte[] key) throws IOException;

    /**
     * 将封装好的格式写入磁盘
     *
     * @param key   存储格式的 TVRowKey
     * @param value 存储格式的 TVRowValue
     * @throws java.io.IOException 磁盘IO异常
     */
    public abstract void put(TVRowKey key, TVRowValue value) throws IOException;

    /**
     * 将封装好的格式写入磁盘
     *
     * @param map, key: 存储格式的 TVRowKey; value: 存储格式的 TVRowValue
     */
    public abstract void put(Map<TVRowKey, TVRowValue> map) throws IOException;

    /**
     * 从磁盘中查找该位号最新的记录，返回索引
     *
     * @param handle 位号
     * @return 最新存储值的Key
     * @throws IOException 磁盘IO异常
     */
    //    public abstract TVRowKey getLatest(int handle) throws IOException;

    /**
     * 根据Key读取Value
     *
     * @param key 存储格式的 TVRowKey
     * @return 存储格式的 TVRowValue
     * @throws IOException 磁盘IO异常
     */
    public abstract TVRowValue get(TVRowKey key) throws IOException;

    /**
     * 读一段时间，该位号所有的记录值
     *
     * @param handle    位号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 这段时间内，存为库中的值
     * @throws IOException 磁盘IO异常
     */
    public abstract Map<TVRowKey, TVRowValue> get(int handle, long startTime, long endTime) throws IOException;

    /**
     * 删除一段时间，该位号所有的记录值
     *
     * @param handle    位号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 这段时间内，存为库中的值
     * @throws IOException 磁盘IO异常
     */
    public abstract boolean delete(int handle, long startTime, long endTime) throws IOException;
}
