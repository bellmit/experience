package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.MyGraph;
import cn.com.servyou.tdap.web.PagerBean;

/**
 * @author lpp
 * 2018-11-29
 */
public interface MyGraphService {
    /**
     * 查询某用户的关系图
     *
     * @param userId    用户ID
     * @param orderBy   排序字段
     * @param pageSize  分页大小
     * @param pageIndex 查询页数
     * @return 指定分页的列表数据
     */
    PagerBean<MyGraph> getByUserId(String userId, MyGraph.OrderBy orderBy, int pageSize, int pageIndex);

    /**
     * 查询指定图
     *
     * @param graphId 图ID
     * @return 图
     */
    MyGraph get(String graphId);

    /**
     * 删除图
     *
     * @param graphId 图ID
     */
    void delete(String graphId);

    /**
     * 创建新的图
     *
     * @param graph 图
     */
    void add(MyGraph graph);

    /**
     * 修改图
     *
     * @param graph 图
     */
    void update(MyGraph graph);

    /**
     * 保存图，增加 或 修改
     *
     * @param graph 图
     */
    void save(MyGraph graph);
}
