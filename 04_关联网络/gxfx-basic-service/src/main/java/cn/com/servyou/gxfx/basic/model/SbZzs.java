package cn.com.servyou.gxfx.basic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lpp
 * 2018-12-07
 */
@Getter
@Setter
public class SbZzs {
    private String nsrdzdah;
    private String nsrmc;
    private String ssyf;
    private Double qbxssr;
    private Double qbxxse;
    private Double qbjxse;
    private Double ynsejze;
    private Double qbynse;
    private Double wkfpje;

    public Double zzssf() {
        if (this.qbynse == null || this.qbxssr == null || this.qbxssr == 0.0) {
            return 0.0;
        } else {
            return this.qbynse / this.qbxssr;
        }
    }

    public Double wdfpzb() {
        if (this.wkfpje == null || this.qbxssr == null || this.qbxssr == 0.0) {
            return 0.0;
        } else {
            return this.wkfpje / this.qbxssr;
        }
    }
}
