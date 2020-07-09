package data.calculate;

import com.google.common.collect.Lists;
import data.domain.DataPoint;
import data.dto.TimeValue;
import period.util.DateTimeUtil;
import lombok.Getter;
import period.dto.PeriodType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多个转一个的共有 3 种：
 * <p>
 * 1. 元素是 计算值，如 月值 = sum(天值)；
 * 2. 元素是 原始值，且是期间值，如 月值 = sum(手工录入/系统对接 的天值)；
 * 3. 元素是 原始值，且是瞬时值，如 天值 = minus(电表读数), 实际走偏移量，也是相加;
 */
public class DataNodeList extends DataNode {
    @Getter
    private final List<DataNode> nodes = Lists.newArrayList();
    private final LocalDateTime begin;
    private final LocalDateTime end;

    public DataNodeList(DataPoint dp, LocalDateTime time, LocalDateTime begin, LocalDateTime end) {
        super(dp, time);
        this.begin = begin;
        this.end = end;

        if (dp.isCalc()) {
            // support 1
            for (LocalDateTime t = DateTimeUtil.standard(dp.getPeriodType(), begin); !t.isAfter(end); t = next(dp.getPeriodType(), t)) {
                this.nodes.add(new DataNode(dp, t));
            }
        }
    }

    private LocalDateTime next(PeriodType periodType, LocalDateTime t) {
        switch (periodType) {
            case HOUR:
                return t.plusHours(1);
            case DAY:
                return t.plusDays(1);
            case WEEK:
                return t.plusWeeks(1);
            case MONTH:
                return t.plusMonths(1);
            case SEASON:
                return t.plusMonths(3);
            case YEAR:
                return t.plusYears(1);
            default:
                throw new RuntimeException("not supported.");
        }
    }

    List<TimeValue> calcPeriodList(CalcContext ctx) {
        if (point.isCalc()) {
            return nodes.stream()
                    .map(n -> n.calcPeriod(ctx))
                    .collect(Collectors.toList());
        } else {
            return ctx.getDb().read(point.getId(), begin, end);
        }
    }
}
