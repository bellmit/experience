package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FphwJyje;
import cn.com.servyou.gxfx.basic.model.HwJe;
import cn.com.servyou.gxfx.basic.model.QyTop;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-19
 */
public interface FphwService {
    /**
     * ��ҵ����Top
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param top      Top��
     * @param params   ���ݲ���
     * @return ��ҵ����Top
     */
    QyTop qyTop(String nsrdzdah, int top, FpDataParams params);

    /**
     * ����Top
     *
     * @param xfnsrdzdah ������˰�˵��ӵ�����
     * @param gfnsrdzdah ������˰�˵��ӵ�����
     * @param top        Top��
     * @param params     ���ݲ���
     * @return ����Top
     */
    List<HwJe> jyTop(String xfnsrdzdah, String gfnsrdzdah, int top, FpDataParams params);

    /**
     * ��������
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param params   ���ۻ���
     * @return �������б�
     */
    List<HwJe> gjhw(String nsrdzdah, FpDataParams params);

    /**
     * ���ۻ���
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param params   ���ۻ���
     * @return �������б�
     */
    List<HwJe> xshw(String nsrdzdah, FpDataParams params);

    /**
     * ���λ��﷢Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param params   ���ݲ���
     * @return ��Ʊ���ｻ�׽���б�
     */
    List<FphwJyje> syHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params);

    /**
     * ���λ��﷢Ʊ
     *
     * @param nsrdzdah ��˰�˵��ӵ�����
     * @param hwSet    ���Ｏ��
     * @param params   ���ݲ���
     * @return ��Ʊ���ｻ�׽���б�
     */
    List<FphwJyje> xyHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params);
}
