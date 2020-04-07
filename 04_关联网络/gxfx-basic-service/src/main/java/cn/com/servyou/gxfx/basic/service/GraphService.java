package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.Graph;
import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.Vertex;

/**
 * @author lpp
 * 2018-12-03
 */
public interface GraphService {
    /**
     * 图初始化查询
     *
     * @param params 参数
     * @param <Q>    企业类型
     * @param <Z>    责任人类型
     * @param <F>    发票类型
     * @param <R>    任职类型
     * @return 图
     */
    <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphInit(Graph.InitParams<Q, Z, F, R> params);

    /**
     * 图展开查询
     *
     * @param params 参数
     * @param <Q>    企业类型
     * @param <Z>    责任人类型
     * @param <F>    发票类型
     * @param <R>    任职类型
     * @return 图
     */
    <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphExpand(Graph.ExpandParams<Q, Z, F, R> params);
}
