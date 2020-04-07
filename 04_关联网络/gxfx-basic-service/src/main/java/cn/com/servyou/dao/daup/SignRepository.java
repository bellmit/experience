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
     * 所有预设的标识
     *
     * @return 所有预设的标识列表
     */
    List<Sign> getAllPreSetSign();

    /**
     * 查询人工标识
     *
     * @param companyCode 纳税人电子档案号
     * @return 企业人工标识
     */
    List<CompanySign> getCompanySign(@Param("companyCode") String companyCode);

    /**
     * 增加人工标识
     *
     * @param companySign companySign
     */
    void addCompanySign(@Param("cs") CompanySign companySign);

    /**
     * 删除人工标识
     *
     * @param companyCode 纳税人电子档案号
     * @param signId      标识ID
     */
    void deleteCompanySign(@Param("companyCode") String companyCode, @Param("signId") String signId);

    /**
     * 查询 记录存在的条数
     *
     * @param companyCode 纳税人电子档案号
     * @param signName    标识名称
     * @return 存在记录的条数
     */
    int findBySignNameCount(@Param("companyCode") String companyCode, @Param("signName") String signName);

    /**
     * 查询一批企业的人工标识
     *
     * @param nsrdzdahSet 企业集合
     * @return 企业人工标识
     */
    List<CompanySign> getCompanySigns(@Param("nsrdzdahSet") Collection<String> nsrdzdahSet);

}
