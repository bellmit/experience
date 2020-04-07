package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
public interface RzService {
    /**
     * ��ѯ��Ա������ҵ
     *
     * @param id      ��ҵID
     * @param rzTypes ��ְ����
     * @return ��Ա������ҵ
     */
    Set<String> searchRyglqy(String id, Collection<RzType> rzTypes);

    /**
     * ��ѯ��ְ
     *
     * @param qyIds   ��ҵ����
     * @param rzTypes ��ְ����
     * @param tClass  �������ͣ�Rz��������
     * @param <T>     ��ְ��������
     * @return ��ְ��
     */
    <T extends Rz> Set<T> readRz(Collection<String> qyIds, Collection<RzType> rzTypes, Class<T> tClass);

    /**
     * ��ѯ��ְ
     * <p>
     * �� @param newIds ��������ְ�� ��Ȼ�ˣ�ͬʱ�� @param allIds ���� �� 2��������ҵ��ְ
     *
     * @param newIds  ��ҵ����
     * @param allIds  ��ҵ����
     * @param rzTypes ��ְ����
     * @param tClass  �������ͣ�Rz��������
     * @param <T>     ��ְ��������
     * @return ��ְ��
     */
    <T extends Rz> Set<T> readRz(Collection<String> newIds, Collection<String> allIds, Collection<RzType> rzTypes, Class<T> tClass);

    /**
     * ��ѯ���е���ְ����
     *
     * @return ������ְ����
     */
    List<Map<String, String>> getAllWorkType();
}
