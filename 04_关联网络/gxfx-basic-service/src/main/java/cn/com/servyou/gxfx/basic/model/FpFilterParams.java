package cn.com.servyou.gxfx.basic.model;

import cn.com.servyou.gxfx.model.Fplx;
import lombok.*;
import org.joda.time.YearMonth;

/**
 * @author lpp
 * 2018-11-14
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FpFilterParams extends FpDataParams {
    private SxyType sxyType;
    private FilterType filterType;
    private double filterValue;

    public FpFilterParams(Fplx fplx, YearMonth begin, YearMonth end, SxyType sxyType, FilterType filterType, double filterValue) {
        super(fplx, begin, end);
        this.sxyType = sxyType;
        this.filterType = filterType;
        this.filterValue = filterValue;
    }

    public enum FilterType {
        /**
         * Top
         */
        top,
        /**
         * 进销项占比
         */
        jyje
    }
}
