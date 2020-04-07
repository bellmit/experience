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
     * ��ѯĳ�û��Ĺ�ϵͼ
     *
     * @param userId �û�ID
     * @return ����ҳ�Ĺ�ϵͼ�б�
     */
    Page<MyGraph> getByUserId(@Param("userId") String userId);

    /**
     * ��ѯָ���Ĺ�ϵͼ
     *
     * @param graphId ͼID
     * @return ��ϵͼ
     */
    MyGraph get(@Param("graphId") String graphId);

    /**
     * ɾ����ϵͼ
     *
     * @param graphId ͼID
     */
    void delete(@Param("graphId") String graphId);

    /**
     * ���ӹ�ϵͼ
     *
     * @param graph ͼ
     */
    void add(@Param("graph") MyGraph graph);

    /**
     * �޸Ĺ�ϵͼ
     *
     * @param graph ͼ
     */
    void update(@Param("graph") MyGraph graph);
}
