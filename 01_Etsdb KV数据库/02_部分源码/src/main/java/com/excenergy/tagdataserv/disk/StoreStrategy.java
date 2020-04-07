package com.excenergy.tagdataserv.disk;

import java.nio.file.Path;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-27
 */
public interface StoreStrategy {
    /**
     * 得到这个时间点，存储库的路径
     *
     * @param basePath  基于此路径，可以区分采集值路径 和 计算值 路径，也可以根据不同的位号，用不同的路径
     * @param timestamp 存储值的时间点
     * @return 库的路径，如果时间早于上线时间 或 晚于当前时间，则返回null
     */
    public Path path(Path basePath, long timestamp);

    /**
     * 根据一个库的路径，得到时间紧跟此库之后的一个库的路径，例如，按月分为，则返回下一个月的库
     *
     * @param path 已知库的路径
     * @return 下一个库的路径，如果时间 晚于当前时间，则返回null
     */
    public Path next(Path path);

    /**
     * 根据一个库的路径，得到时间紧跟此库之后的一个库的路径，例如，按月分为，则返回上一个月的库
     *
     * @param path 已知库的路径
     * @return 上一个库的路径，如果时间早于上线时间，则返回null
     */
    public Path prev(Path path);
}
