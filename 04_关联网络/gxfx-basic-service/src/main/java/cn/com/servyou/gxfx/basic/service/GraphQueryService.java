package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.view.GraphEntity;

/**
 * @author lpp
 * 2018-08-06
 */
public interface GraphQueryService {

    /**
     * 图初始化查询
     *
     * @param params 参数
     * @return 图
     */
    <Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> GraphEntity<Q, Z, F, R> graphInit(GraphEntity.InitParams<Q, Z, F, R> params);

    /**
     * 叶子节点展开
     *
     * @param params 参数
     * @return 图
     */
    <Q extends GraphEntity.QyVertex, Z extends GraphEntity.ZrrVertex, F extends GraphEntity.PlEdge, R extends GraphEntity.RzEdge> GraphEntity<Q, Z, F, R> graphExpand(GraphEntity.ExpandParams<Q, Z, F, R> params);

}
