package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.tdap.web.PagerBean;

import java.util.Collection;
import java.util.Map;

/**
 * @author lpp
 * 2018-10-22
 */
public interface NsrService {
    /**
     * ������˰�˵��ӵ����ţ���ѯ����˰�˵Ļ�����Ϣ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @return ��˰�˻�����Ϣ
     */
    Nsr getNsr(String nsrdzdah);

    /**
     * ����һ����˰�˵��ӵ����ţ�������һ����˰�˵Ļ�����Ϣ
     *
     * @param nsrdzdahs ��˰�˵��ӵ����ż���
     * @return ��˰�˻�����ϢMap
     */
    Map<String, Nsr> getNsrBatch(Collection<String> nsrdzdahs);

    /**
     * ������˰��
     *
     * @param search     ��������
     * @return ��ҳBean
     */
    PagerBean<Nsr> search(Nsr.SearchCondition search);
}
