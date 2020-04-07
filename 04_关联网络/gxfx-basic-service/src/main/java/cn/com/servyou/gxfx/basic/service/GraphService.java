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
     * ͼ��ʼ����ѯ
     *
     * @param params ����
     * @param <Q>    ��ҵ����
     * @param <Z>    ����������
     * @param <F>    ��Ʊ����
     * @param <R>    ��ְ����
     * @return ͼ
     */
    <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphInit(Graph.InitParams<Q, Z, F, R> params);

    /**
     * ͼչ����ѯ
     *
     * @param params ����
     * @param <Q>    ��ҵ����
     * @param <Z>    ����������
     * @param <F>    ��Ʊ����
     * @param <R>    ��ְ����
     * @return ͼ
     */
    <Q extends Vertex, Z extends Vertex, F extends Fp, R extends Rz> Graph<Q, Z, F, R> graphExpand(Graph.ExpandParams<Q, Z, F, R> params);
}
