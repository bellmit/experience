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
     * 企业购销Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param top      Top数
     * @param params   数据参数
     * @return 企业购销Top
     */
    QyTop qyTop(String nsrdzdah, int top, FpDataParams params);

    /**
     * 交易Top
     *
     * @param xfnsrdzdah 销方纳税人电子档案号
     * @param gfnsrdzdah 购方纳税人电子档案号
     * @param top        Top数
     * @param params     数据参数
     * @return 交易Top
     */
    List<HwJe> jyTop(String xfnsrdzdah, String gfnsrdzdah, int top, FpDataParams params);

    /**
     * 购进货物
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param params   销售货物
     * @return 货物金额列表
     */
    List<HwJe> gjhw(String nsrdzdah, FpDataParams params);

    /**
     * 销售货物
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param params   销售货物
     * @return 货物金额列表
     */
    List<HwJe> xshw(String nsrdzdah, FpDataParams params);

    /**
     * 上游货物发票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param params   数据参数
     * @return 发票货物交易金额列表
     */
    List<FphwJyje> syHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params);

    /**
     * 下游货物发票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param params   数据参数
     * @return 发票货物交易金额列表
     */
    List<FphwJyje> xyHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params);
}
