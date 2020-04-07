package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FpFilterParams;
import cn.com.servyou.gxfx.model.Fp;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-13
 */
public interface FpService {
    /**
     * ����������
     *
     * @param vIds   �ڵ㼯��
     * @param params ���˲���
     * @return �㼯��
     */
    Set<String> searchSxy(Collection<String> vIds, FpFilterParams params);

    /**
     * ������ռ��
     *
     * @param vIds   �ڵ㼯��
     * @param params ���ݲ���
     * @return Map<id:                                                               je>
     */
    Map<String, Double> readJxze(Collection<String> vIds, FpDataParams params);

    /**
     * ������ռ��
     *
     * @param vIds   �ڵ㼯��
     * @param params ���ݲ���
     * @return Map<id:                                                               je>
     */
    Map<String, Double> readXxze(Collection<String> vIds, FpDataParams params);

    /**
     * ��Ʊ��
     *
     * @param fromSet from����
     * @param toSet   to����
     * @param params  ���ݲ���
     * @param tClass  ��������
     * @param <T>     PL��������
     * @return Ʊ��
     */
    <T extends Fp> Set<T> readPl(Collection<String> fromSet, Collection<String> toSet, FpDataParams params, Class<T> tClass);
}
