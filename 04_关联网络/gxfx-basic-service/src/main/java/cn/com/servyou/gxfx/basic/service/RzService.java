package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
public interface RzService {
    /**
     * 查询人员关联企业
     *
     * @param id      企业ID
     * @param rzTypes 任职类型
     * @return 人员关联企业
     */
    Set<String> searchRyglqy(String id, Collection<RzType> rzTypes);

    /**
     * 查询任职
     *
     * @param qyIds   企业集合
     * @param rzTypes 任职类型
     * @param tClass  返回类型，Rz或其子类
     * @param <T>     任职或其子类
     * @return 任职边
     */
    <T extends Rz> Set<T> readRz(Collection<String> qyIds, Collection<RzType> rzTypes, Class<T> tClass);

    /**
     * 查询任职
     * <p>
     * 在 @param newIds 集合中任职的 自然人，同时在 @param allIds 集合 中 2家以上企业任职
     *
     * @param newIds  企业集合
     * @param allIds  企业集合
     * @param rzTypes 任职类型
     * @param tClass  返回类型，Rz或其子类
     * @param <T>     任职或其子类
     * @return 任职边
     */
    <T extends Rz> Set<T> readRz(Collection<String> newIds, Collection<String> allIds, Collection<RzType> rzTypes, Class<T> tClass);

    /**
     * 查询所有的任职类型
     *
     * @return 所有任职类型
     */
    List<Map<String, String>> getAllWorkType();
}
