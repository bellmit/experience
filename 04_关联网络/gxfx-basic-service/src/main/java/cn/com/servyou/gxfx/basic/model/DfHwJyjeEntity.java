package cn.com.servyou.gxfx.basic.model;

import cn.com.jdls.foundation.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lpp
 * 2018-10-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DfHwJyjeEntity {
    public static final String GF_BJ = "GF";
    public static final String XF_BJ = "XF";

    private String nsrdzdah;
    private String nsrmc;
    private String dfnsrdzdah;
    private String dfnsrmc;
    private String gfxfBj;
    private String spmc;
    private double je;

    public String hwKey() {
        return Base64.encode(spmc);
    }

    public String hwId() {
        return String.format("%s_%s", gfxfBj, hwKey());
    }

    public String dfNsrId() {
        return String.format("%s_%s", gfxfBj, dfnsrdzdah);
    }

    public String hwEdgeSource() {
        if (GF_BJ.equalsIgnoreCase(gfxfBj)) {
            return hwId();
        } else {
            return nsrdzdah;
        }
    }

    public String hwEdgeTarget() {
        if (GF_BJ.equalsIgnoreCase(gfxfBj)) {
            return nsrdzdah;
        } else {
            return hwId();
        }
    }

    public String hwEdgeId() {
        return String.format("%s-%s", hwEdgeSource(), hwEdgeTarget());
    }

    public String dfEdgeSource() {
        if (GF_BJ.equalsIgnoreCase(gfxfBj)) {
            return dfNsrId();
        } else {
            return hwId();
        }
    }

    public String dfEdgeTarget() {
        if (GF_BJ.equalsIgnoreCase(gfxfBj)) {
            return hwId();
        } else {
            return dfNsrId();
        }
    }

    public String dfEdgeId() {
        return String.format("%s-%s", dfEdgeSource(), dfEdgeTarget());
    }
}
