package cn.com.servyou.gxfx.basic.model;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-08-13
 */
@Getter
@Setter
public class HwJe {
    private String spmc;
    private double je;

    public HwJe() {
    }

    public static Map<String, Double> toMap(Collection<HwJe> collection) {
        Map<String, Double> result = Maps.newHashMap();

        for (HwJe h : collection) {
            result.put(h.spmc, h.je);
        }

        return result;
    }

    private void transformJe2Wy() {
        this.je = Math.round(this.je / 10000);
    }


    public static void transformJe2Wy(List<HwJe> hwJes) {
        for (HwJe hwJe : hwJes) {
            hwJe.transformJe2Wy();
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %.0f", spmc, je);
    }
}
