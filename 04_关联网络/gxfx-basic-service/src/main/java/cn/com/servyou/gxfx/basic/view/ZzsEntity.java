package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.model.SbZzs;
import cn.com.servyou.gxfx.basic.util.TransformUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * 企业增值税信息统计
 *
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
public class ZzsEntity {
    /**
     * 取值区间
     */
    private String qzqj;
    /**
     * 全部销售收?入
     */
    private Double qbxssr;
    /**
     * 全部销项税额
     */
    private Double qbxxse;
    /**
     * 全部进项税额
     */
    private Double qbjxse;
    /**
     * 应纳税额减征额
     */
    private Double ynsejze;
    /**
     * 全部应纳税额
     */
    private Double qbynse;
    /**
     * 入库税款合计
     */
    private Double rkskhj;
    /**
     * 增值税税负
     */
    private Double zzssf;
    /**
     * 未开发票占?比
     */
    private Double wkfpzb;

    public ZzsEntity(@NonNull SbZzs sbZzs, double zzsRksr, @NonNull String begin, @NonNull String end) {
        this.qzqj = String.format("%s-%s", begin, end);

        this.qbxssr = TransformUtil.scaleWy(sbZzs.getQbxssr(), 2);
        this.qbxxse = TransformUtil.scaleWy(sbZzs.getQbxxse(), 2);
        this.qbjxse = TransformUtil.scaleWy(sbZzs.getQbjxse(), 2);
        this.ynsejze = TransformUtil.scaleWy(sbZzs.getYnsejze(), 2);
        this.qbynse = TransformUtil.scaleWy(sbZzs.getQbynse(), 2);

        this.zzssf = TransformUtil.scale(sbZzs.zzssf(), 4);
        this.wkfpzb = TransformUtil.scale(sbZzs.wdfpzb(), 4);

        this.rkskhj = TransformUtil.scaleWy(zzsRksr, 2);
    }
}
