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
     * 纳税人电子档案号
     */
    private String nsrdzdah;
    /**
     * 名称
     */
    private String name;
    /**
     * 识别号
     */
    private String sbh;
    /**
     * 状态代码
     */
    private String nsrztdm;
    /**
     * 状态名称
     */
    private String nsrztmc;
    /**
     * 行业代码
     */
    private String hydm;
    /**
     * 行业名称
     */
    private String hymc;
    /**
     * 注册类型代码
     */
    private String djzclxdm;
    /**
     * 注册类型名称
     */
    private String djzclxmc;
    /**
     * 增值税纳税人类型名称
     */
    private String zzsnsrlxmc;
    /**
     * 税务机关名称
     */
    private String swjgmc;
    /**
     * 地市税务机关
     */
    private String dsswjgmc;
    /**
     * 登记日期
     */
    private String djrq;

    /**
     * 注册资本
     */
    private Double zczb;

    /**
     * 注册地址
     */
    private String zcdz;

    /**
     * 生产经营地址
     */
    private String scjydz;

    /**
     * 经营范围
     */
    private String jyfw;
    /**
     * 信誉等级
     */
    private String xydj;
    /**
     * 立案状态
     */
    private String lazt;
    /**
     * 法定代表人名称
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
        @ApiModelProperty(value = "搜索条件，支持 名称、识别号、社会信用代码")
        private String name;
        @ApiModelProperty(value = "每页数量")
        private Integer pageSize;
        @ApiModelProperty(value = "当前页数")
        private Integer pageIndex;
        @ApiModelProperty(value = "行业ID")
        private String hyId;
        @ApiModelProperty(value = "纳税人状态代码")
        private String nsrztdm;
        @ApiModelProperty(value = "注册资本起(万元)")
        private Double zczbBegin;
        @ApiModelProperty(value = "注册资本止(万元)")
        private Double zczbEnd;
        @ApiModelProperty(value = "税务机关代码")
        private String swjgdm;
        @ApiModelProperty(value = "登记注册类型ID")
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
