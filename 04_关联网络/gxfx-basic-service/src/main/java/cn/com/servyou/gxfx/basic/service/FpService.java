package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FpFilterParams;
import cn.com.servyou.gxfx.model.Fp;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-13
 */
public interface FpService {
    /**
     * 搜索上下游
     *
     * @param vIds   节点集合
     * @param params 过滤参数
     * @return 点集合
     */
    Set<String> searchSxy(Collection<String> vIds, FpFilterParams params);

    /**
     * 读进项占比
     *
     * @param vIds   节点集合
     * @param params 数据参数
     * @return Map<id:                                                               je>
     */
    Map<String, Double> readJxze(Collection<String> vIds, FpDataParams params);

    /**
     * 读销项占比
     *
     * @param vIds   节点集合
     * @param params 数据参数
     * @return Map<id:                                                               je>
     */
    Map<String, Double> readXxze(Collection<String> vIds, FpDataParams params);

    /**
     * 读票流
     *
     * @param fromSet from集合
     * @param toSet   to集合
     * @param params  数据参数
     * @param tClass  返回类型
     * @param <T>     PL或其子类
     * @return 票流
     */
    <T extends Fp> Set<T> readPl(Collection<String> fromSet, Collection<String> toSet, FpDataParams params, Class<T> tClass);
}
