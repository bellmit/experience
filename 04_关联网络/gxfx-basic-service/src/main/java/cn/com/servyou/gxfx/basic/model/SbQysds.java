package cn.com.servyou.gxfx.basic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lpp
 * 2018-12-07
 */
@Getter
@Setter
public class SbQysds {
    private String nsrdzdah;
    private String nsrmc;
    private String ssnd;
    private Double yysr;
    private Double xsspsr;
    private Double yycb;
    private Double qjfy;
    private Double lrze;
    private Double ynse;

    public Double sdsgxl() {
        if (this.ynse == null || this.lrze == null || this.lrze == 0.0) {
            return 0.0;
        } else {
            return this.ynse / this.lrze;
        }
    }

    public Double sdssf() {
        if (this.ynse == null || this.yysr == null || this.yysr == 0.0) {
            return 0.0;
        } else {
            return this.ynse / this.yysr;
        }
    }
}
