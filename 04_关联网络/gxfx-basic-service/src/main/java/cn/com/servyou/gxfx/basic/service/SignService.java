package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.model.CompanySign;
import cn.com.servyou.gxfx.basic.model.Sign;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-27
 */
public interface SignService {
    /**
     * ����Ԥ��ı�ʶ
     *
     * @return ����Ԥ��ı�ʶ�б�
     */
    List<Sign> getAllPreSetSign();

    /**
     * ��ѯ�˹���ʶ
     *
     * @param companyCode ��˰�˵��ӵ�����
     * @return ��ҵ�˹���ʶ
     */
    List<CompanySign> getCompanySign(String companyCode);

    /**
     * �����˹���ʶ
     *
     * @param companySign companySign
     */
    void addCompanySign(CompanySign companySign);

    /**
     * ɾ���˹���ʶ
     *
     * @param companyCode ��˰�˵��ӵ�����
     * @param signId      ��ʶID
     */
    void deleteCompanySign(String companyCode, String signId);

    /**
     * ����˹���ʶ�����Ƿ����
     *
     * @param companyCode ��˰�˵��ӵ�����
     * @param signName    ��ʶ����
     * @return ���� true�������� false
     */
    Boolean checkSignName(String companyCode, String signName);

    /**
     * ��ѯһ����ҵ���˹���ʶ
     *
     * @param nsrdzdahSet ��ҵ����
     * @return ��ҵ�˹���ʶ��Key: ��˰�˵��ӵ����ţ�Value���˹���ʶ���Ƽ���
     */
    Map<String, Collection<String>> getSignMap(Collection<String> nsrdzdahSet);
}
