package cn.com.servyou.dao.daup;

import cn.com.servyou.gxfx.basic.model.CompanySign;
import cn.com.servyou.gxfx.basic.model.Sign;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-27
 */
@Repository
public interface SignRepository {
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
    List<CompanySign> getCompanySign(@Param("companyCode") String companyCode);

    /**
     * �����˹���ʶ
     *
     * @param companySign companySign
     */
    void addCompanySign(@Param("cs") CompanySign companySign);

    /**
     * ɾ���˹���ʶ
     *
     * @param companyCode ��˰�˵��ӵ�����
     * @param signId      ��ʶID
     */
    void deleteCompanySign(@Param("companyCode") String companyCode, @Param("signId") String signId);

    /**
     * ��ѯ ��¼���ڵ�����
     *
     * @param companyCode ��˰�˵��ӵ�����
     * @param signName    ��ʶ����
     * @return ���ڼ�¼������
     */
    int findBySignNameCount(@Param("companyCode") String companyCode, @Param("signName") String signName);

    /**
     * ��ѯһ����ҵ���˹���ʶ
     *
     * @param nsrdzdahSet ��ҵ����
     * @return ��ҵ�˹���ʶ
     */
    List<CompanySign> getCompanySigns(@Param("nsrdzdahSet") Collection<String> nsrdzdahSet);

}
