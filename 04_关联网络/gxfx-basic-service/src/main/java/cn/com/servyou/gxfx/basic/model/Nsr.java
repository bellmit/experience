package cn.com.servyou.gxfx.basic.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * @author lpp
 * 2018-10-22
 */
@Data
public class Nsr {

    /**
     * ��˰�˵��ӵ�����
     */
    private String nsrdzdah;
    /**
     * ����
     */
    private String name;
    /**
     * ʶ���
     */
    private String sbh;
    /**
     * ״̬����
     */
    private String nsrztdm;
    /**
     * ״̬����
     */
    private String nsrztmc;
    /**
     * ��ҵ����
     */
    private String hydm;
    /**
     * ��ҵ����
     */
    private String hymc;
    /**
     * ע�����ʹ���
     */
    private String djzclxdm;
    /**
     * ע����������
     */
    private String djzclxmc;
    /**
     * ��ֵ˰��˰����������
     */
    private String zzsnsrlxmc;
    /**
     * ˰���������
     */
    private String swjgmc;
    /**
     * ����˰�����
     */
    private String dsswjgmc;
    /**
     * �Ǽ�����
     */
    private String djrq;

    /**
     * ע���ʱ�
     */
    private Double zczb;

    /**
     * ע���ַ
     */
    private String zcdz;

    /**
     * ������Ӫ��ַ
     */
    private String scjydz;

    /**
     * ��Ӫ��Χ
     */
    private String jyfw;
    /**
     * �����ȼ�
     */
    private String xydj;
    /**
     * ����״̬
     */
    private String lazt;
    /**
     * ��������������
     */
    private String fddbrmc;

    public Double getZczb() {
        if (zczb != null) {
            return (double) Math.round(zczb / 10000);
        } else {
            return 0.0;
        }
    }

    @ApiModel
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchCondition {
        @ApiModelProperty(value = "����������֧�� ���ơ�ʶ��š�������ô���")
        private String name;
        @ApiModelProperty(value = "ÿҳ����")
        private Integer pageSize;
        @ApiModelProperty(value = "��ǰҳ��")
        private Integer pageIndex;
        @ApiModelProperty(value = "��ҵID")
        private String hyId;
        @ApiModelProperty(value = "��˰��״̬����")
        private String nsrztdm;
        @ApiModelProperty(value = "ע���ʱ���(��Ԫ)")
        private Double zczbBegin;
        @ApiModelProperty(value = "ע���ʱ�ֹ(��Ԫ)")
        private Double zczbEnd;
        @ApiModelProperty(value = "˰����ش���")
        private String swjgdm;
        @ApiModelProperty(value = "�Ǽ�ע������ID")
        private String djzclxId;

        public String getName() {
            if (StringUtils.isBlank(name)) {
                return null;
            } else {
                return name.trim();
            }
        }

        public String getHyId() {
            if (StringUtils.isBlank(hyId)) {
                return null;
            } else {
                return hyId.replaceAll("0+?$", "%").trim();
            }
        }

        public String getNsrztdm() {
            if (StringUtils.isBlank(nsrztdm)) {
                return null;
            } else {
                return nsrztdm.trim();
            }
        }

        public String getSwjgdm() {
            if (StringUtils.isBlank(swjgdm)) {
                return null;
            } else {
                return swjgdm.trim();
            }
        }

        public String getDjzclxId() {
            if (StringUtils.isBlank(djzclxId)) {
                return null;
            } else {
                return djzclxId.trim();
            }
        }

        public Double getZczbBegin() {
            if (this.zczbBegin != null) {
                return zczbBegin * 10000;
            } else {
                return null;
            }
        }

        public Double getZczbEnd() {
            if (this.zczbEnd != null) {
                return zczbEnd * 10000;
            } else {
                return null;
            }
        }

        public Integer getPageSize() {
            if (pageSize == null) {
                return 10;
            } else {
                return pageSize;
            }
        }

        public Integer getPageIndex() {
            if (pageIndex == null) {
                return 1;
            } else {
                return pageIndex;
            }
        }
    }
}
