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
     * 查找点集合
     *
     * @param ids    ID集合
     * @param tClass 返回类型
     * @param <T>    Vertex 或其子类
     * @return Map，Key为id，Value为T类型对象
     */
    <T extends Vertex> Map<String, T> vertex(Set<String> ids, Class<T> tClass);
}
