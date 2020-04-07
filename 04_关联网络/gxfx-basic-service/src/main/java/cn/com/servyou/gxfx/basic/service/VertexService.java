package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.model.Vertex;

import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
public interface VertexService {
    /**
     * ���ҵ㼯��
     *
     * @param ids    ID����
     * @param tClass ��������
     * @param <T>    Vertex ��������
     * @return Map��KeyΪid��ValueΪT���Ͷ���
     */
    <T extends Vertex> Map<String, T> vertex(Set<String> ids, Class<T> tClass);
}
