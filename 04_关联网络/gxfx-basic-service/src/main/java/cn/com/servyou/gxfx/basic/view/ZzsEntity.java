package cn.com.servyou.gxfx.basic.view;

import cn.com.servyou.gxfx.basic.model.SbZzs;
import cn.com.servyou.gxfx.basic.util.TransformUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * ��ҵ��ֵ˰��Ϣͳ��
 *
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
public class ZzsEntity {
    /**
     * ȡֵ����
     */
    private String qzqj;
    /**
     * ȫ��������?��
     */
    private Double qbxssr;
    /**
     * ȫ������˰��
     */
    private Double qbxxse;
    /**
     * ȫ������˰��
     */
    private Double qbjxse;
    /**
     * Ӧ��˰�������
     */
    private Double ynsejze;
    /**
     * ȫ��Ӧ��˰��
     */
    private Double qbynse;
    /**
     * ���˰��ϼ�
     */
    private Double rkskhj;
    /**
     * ��ֵ˰˰��
     */
    private Double zzssf;
    /**
     * δ����Ʊռ?��
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
