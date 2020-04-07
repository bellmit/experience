package cn.com.servyou.gxfx.basic.model;

import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Fplx;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.YearMonth;

import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FpDataParams {
    protected Fplx fplx;
    protected YearMonth begin;
    protected YearMonth end;

    public FpDataParams(Fplx fplx, String begin, String end) {
        this.fplx = fplx;
        this.begin = Fp.monthOf(begin);
        this.end = Fp.monthOf(end);
    }

    public String getBeginStr() {
        return Fp.monthStr(begin);
    }

    public String getEndStr() {
        return Fp.monthStr(end);
    }

    public Set<String> cols() {
        Set<String> result = Sets.newHashSet();
        for (YearMonth m = begin; m.isBefore(end) || m.isEqual(end); m = m.plusMonths(1)) {
            switch (fplx) {
                case zp:
                case pp:
                    result.add(Fp.col(fplx, m));
                    break;
                case all:
                    result.add(Fp.col(Fplx.zp, m));
                    result.add(Fp.col(Fplx.pp, m));
                    break;
                default:
                    throw new RuntimeException("Fplx error.");
            }
        }
        return result;
    }
}
