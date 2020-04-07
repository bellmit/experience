package cn.com.servyou.dao.daup;

import cn.com.servyou.gxfx.basic.model.MyGraph;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author lpp
 * 2018-11-29
 */
@Repository
public interface MyGraphRepository {
    /**
     * 查询某用户的关系图
     *
     * @param userId 用户ID
     * @return 带分页的关系图列表
     */
    Page<MyGraph> getByUserId(@Param("userId") String userId);

    /**
     * 查询指定的关系图
     *
     * @param graphId 图ID
     * @return 关系图
     */
    MyGraph get(@Param("graphId") String graphId);

    /**
     * 删除关系图
     *
     * @param graphId 图ID
     */
    void delete(@Param("graphId") String graphId);

    /**
     * 增加关系图
     *
     * @param graph 图
     */
    void add(@Param("graph") MyGraph graph);

    /**
     * 修改关系图
     *
     * @param graph 图
     */
    void update(@Param("graph") MyGraph graph);
}
