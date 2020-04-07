package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.util.TransformUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ����˰��Ϣ
 *
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
public class XfsEntity {
    /**
     * ��ֹʱ��
     */
    private String qzqj;
    /**
     * Ӧ��˰��
     */
    private Double ynse;
    /**
     * ���˰��
     */
    private Double rkse;

    public XfsEntity(double xfsYnse, double xfsRkse, String begin, String end) {
        this.qzqj = String.format("%s-%s", begin, end);
        this.ynse = TransformUtil.scaleWy(xfsYnse, 2);
        this.rkse = TransformUtil.scaleWy(xfsRkse, 2);
    }
}
