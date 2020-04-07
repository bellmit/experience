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
     * 根据纳税人电子档案号，查询该纳税人的基本信息
     *
     * @param nsrdzdah 纳税人电子档案号
     * @return 纳税人基本信息
     */
    Nsr getNsr(String nsrdzdah);

    /**
     * 根据一批纳税人电子档案号，返回这一批纳税人的基本信息
     *
     * @param nsrdzdahs 纳税人电子档案号集合
     * @return 纳税人基本信息Map
     */
    Map<String, Nsr> getNsrBatch(Collection<String> nsrdzdahs);

    /**
     * 搜索纳税人
     *
     * @param search     搜索条件
     * @return 分页Bean
     */
    PagerBean<Nsr> search(Nsr.SearchCondition search);
}
