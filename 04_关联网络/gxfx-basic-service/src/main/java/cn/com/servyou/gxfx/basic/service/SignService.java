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
    List<CompanySign> getCompanySign(String companyCode);

    /**
     * 增加人工标识
     *
     * @param companySign companySign
     */
    void addCompanySign(CompanySign companySign);

    /**
     * 删除人工标识
     *
     * @param companyCode 纳税人电子档案号
     * @param signId      标识ID
     */
    void deleteCompanySign(String companyCode, String signId);

    /**
     * 检查人工标识名称是否存在
     *
     * @param companyCode 纳税人电子档案号
     * @param signName    标识名称
     * @return 存在 true，不存在 false
     */
    Boolean checkSignName(String companyCode, String signName);

    /**
     * 查询一批企业的人工标识
     *
     * @param nsrdzdahSet 企业集合
     * @return 企业人工标识，Key: 纳税人电子档案号，Value：人工标识名称集合
     */
    Map<String, Collection<String>> getSignMap(Collection<String> nsrdzdahSet);
}
