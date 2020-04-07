package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.model.SbQysds;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import static cn.com.servyou.gxfx.basic.util.TransformUtil.scale;
import static cn.com.servyou.gxfx.basic.util.TransformUtil.scaleWy;

/**
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
public class SdsEntity {
    /**
     * 营业收入
     */
    private Double[] yysr;
    /**
     * 销售商品收入
     */
    private Double[] xsspsr;
    /**
     * 营业成本
     */
    private Double[] yycb;
    /**
     * 期间费用
     */
    private Double[] qjfy;
    /**
     * 利润总额
     */
    private Double[] lrze;
    /**
     * 应纳税额
     */
    private Double[] ynse;
    /**
     * 入库税款
     */
    private Double[] rksk;
    /**
     * 所得税贡献率
     */
    private Double[] sdsgxl;
    /**
     * 所得税税负
     */
    private Double[] sdssf;

    public SdsEntity(@NonNull SbQysds lastYear, @NonNull SbQysds beforeLastYear, double qysdsRkse1, double qysdsRkse2) {
        this.yysr = toArrayWy(beforeLastYear.getYysr(), lastYear.getYysr());
        this.xsspsr = toArrayWy(beforeLastYear.getXsspsr(), lastYear.getXsspsr());
        this.yycb = toArrayWy(beforeLastYear.getYycb(), lastYear.getYycb());
        this.qjfy = toArrayWy(beforeLastYear.getQjfy(), lastYear.getQjfy());
        this.lrze = toArrayWy(beforeLastYear.getLrze(), lastYear.getLrze());
        this.ynse = toArrayWy(beforeLastYear.getYnse(), lastYear.getYnse());

        this.rksk = toArrayWy(qysdsRkse2, qysdsRkse1);
        this.sdsgxl = toArray(beforeLastYear.sdsgxl(), lastYear.sdsgxl());
        this.sdssf = toArray(beforeLastYear.sdssf(), lastYear.sdssf());
    }

    private Double[] toArrayWy(Double yysr, Double yysr2) {
        return new Double[]{scaleWy(yysr, 2), scaleWy(yysr2, 2)};
    }

    private Double[] toArray(Double yysr, Double yysr2) {
        return new Double[]{scale(yysr, 2), scale(yysr2, 2)};
    }
}
