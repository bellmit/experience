package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.MyGraph;
import cn.com.servyou.tdap.web.PagerBean;

/**
 * @author lpp
 * 2018-11-29
 */
public interface MyGraphService {
    /**
     * ��ѯĳ�û��Ĺ�ϵͼ
     *
     * @param userId    �û�ID
     * @param orderBy   �����ֶ�
     * @param pageSize  ��ҳ��С
     * @param pageIndex ��ѯҳ��
     * @return ָ����ҳ���б�����
     */
    PagerBean<MyGraph> getByUserId(String userId, MyGraph.OrderBy orderBy, int pageSize, int pageIndex);

    /**
     * ��ѯָ��ͼ
     *
     * @param graphId ͼID
     * @return ͼ
     */
    MyGraph get(String graphId);

    /**
     * ɾ��ͼ
     *
     * @param graphId ͼID
     */
    void delete(String graphId);

    /**
     * �����µ�ͼ
     *
     * @param graph ͼ
     */
    void add(MyGraph graph);

    /**
     * �޸�ͼ
     *
     * @param graph ͼ
     */
    void update(MyGraph graph);

    /**
     * ����ͼ������ �� �޸�
     *
     * @param graph ͼ
     */
    void save(MyGraph graph);
}
