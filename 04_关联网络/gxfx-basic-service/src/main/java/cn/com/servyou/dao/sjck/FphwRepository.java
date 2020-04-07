package cn.com.servyou.dao.sjck;

import cn.com.servyou.gxfx.basic.model.FphwJyje;
import cn.com.servyou.gxfx.basic.model.HwJe;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-19
 */
@Repository
public interface FphwRepository {

    /**
     * 专票进项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyJxTopZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 普票进项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyJxTopPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 专普票进项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyJxTopAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 专票销项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyXxTopZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 普票销项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyXxTopPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 专普票销项Top
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物交易金额
     */
    List<HwJe> qyXxTopAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 专票交易Top
     *
     * @param xfnsrdzdah 销方纳税人电子档案号
     * @param gfnsrdzdah 购方纳税人电子档案号
     * @param begin      开始时间
     * @param end        结束时间
     * @return 货物交易金额
     */
    List<HwJe> jyTopZp(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 普票交易Top
     *
     * @param xfnsrdzdah 销方纳税人电子档案号
     * @param gfnsrdzdah 购方纳税人电子档案号
     * @param begin      开始时间
     * @param end        结束时间
     * @return 货物交易金额
     */
    List<HwJe> jyTopPp(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 专普票交易Top
     *
     * @param xfnsrdzdah 销方纳税人电子档案号
     * @param gfnsrdzdah 购方纳税人电子档案号
     * @param begin      开始时间
     * @param end        结束时间
     * @return 货物交易金额
     */
    List<HwJe> jyTopAll(@Param("xf") String xfnsrdzdah, @Param("gf") String gfnsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 购进货物-专票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> gjhwZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 购进货物-普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> gjhwPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 购进货物-专普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> gjhwAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 销售货物-专票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> xshwZp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 销售货物-普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> xshwPp(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 销售货物-专普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param begin    开始时间
     * @param end      结束时间
     * @return 货物金额列表
     */
    List<HwJe> xshwAll(@Param("nsrdzdah") String nsrdzdah, @Param("begin") String begin, @Param("end") String end);

    /**
     * 上游货物发票-专票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> syHwFpZp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * 上游货物发票-普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> syHwFpPp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * 上游货物发票-专普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> syHwFpAll(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * 下游货物发票-专票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> xyHwFpZp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * 下游货物发票-普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> xyHwFpPp(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);

    /**
     * 下游货物发票-专普票
     *
     * @param nsrdzdah 纳税人电子档案号
     * @param hwSet    货物集合
     * @param begin    开始时间
     * @param end      结束时间
     * @return 发票货物集合
     */
    List<FphwJyje> xyHwFpAll(@Param("nsrdzdah") String nsrdzdah, @Param("hwSet") Collection<String> hwSet, @Param("begin") String begin, @Param("end") String end);
}
