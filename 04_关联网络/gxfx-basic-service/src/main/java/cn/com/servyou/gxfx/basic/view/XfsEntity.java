package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.util.TransformUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 消费税信息
 *
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
public class XfsEntity {
    /**
     * 起止时间
     */
    private String qzqj;
    /**
     * 应纳税额
     */
    private Double ynse;
    /**
     * 入库税额
     */
    private Double rkse;

    public XfsEntity(double xfsYnse, double xfsRkse, String begin, String end) {
        this.qzqj = String.format("%s-%s", begin, end);
        this.ynse = TransformUtil.scaleWy(xfsYnse, 2);
        this.rkse = TransformUtil.scaleWy(xfsRkse, 2);
    }
}
