package data.calculate;

import com.google.common.base.Preconditions;
import data.domain.DataPoint;
import data.dto.TimeValue;
import data.service.ExpressionService;
import data.service.RawPointDataService;
import period.util.DateTimeUtil;
import lombok.NonNull;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import period.dto.PointPeriod;
import period.service.PeriodService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CalculateService {
    private ExpressionService expressionService;
    private RawPointDataService rawService;
    @Reference(check = false)
    private PeriodService periodService;

    @Autowired
    public CalculateService(ExpressionService expressionService, RawPointDataService rawService) {
        this.expressionService = expressionService;
        this.rawService = rawService;
    }

    // todo add aop log to db
    public TimeValue calcInstant(@NonNull DataPoint dataPoint, @NonNull LocalDateTime time) {
        Preconditions.checkArgument(dataPoint.isCalc(), "point: %s is not calc point.", dataPoint.getId());
        Preconditions.checkArgument(dataPoint.isInstant(), String.format("only support instant point, point: %s", dataPoint.getId()));

        // 1. create node
        DataNode node = new DataNode(dataPoint, time);
        CalcContext ctx = new CalcContext(rawService);

        // 2. expanding
        expressionService.expandingInstant(node, ctx);

        // 3. read raw data
        ctx.cacheRawData(time);

        // 4. calc
        return node.calcInstant(ctx);
    }

    public TimeValue calcPeriod(DataPoint dataPoint, LocalDateTime time) {
        Preconditions.checkArgument(dataPoint.isPeriod(), String.format("only support period point, point: %s", dataPoint.getId()));
        Preconditions.checkArgument(dataPoint.isCalc(), "point: %s is not calc point.", dataPoint.getId());

        LocalDateTime calcTime = DateTimeUtil.standard(dataPoint.getPeriodType(), time);

        // 1. create node and add to ctx
        DataNode node = new DataNode(dataPoint, calcTime);
        CalcContext ctx = new CalcContext(rawService);

        // 2. expanding
        expressionService.expandingPeriod(node, ctx);

        // 3. read raw data
        PointPeriod period = periodService.getEntityPeriod(dataPoint.getEntity().getId(), dataPoint.getPeriodType(), calcTime);
        ctx.cacheRawData(period.getBegin(), period.getEnd());

        // 4. calc
        return node.calcPeriod(ctx);
    }

    public List<TimeValue> calcPeriodList(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        Preconditions.checkArgument(dataPoint.isPeriod(), String.format("only support period point, point: %s", dataPoint.getId()));
        Preconditions.checkArgument(dataPoint.isCalc(), "point: %s is not calc point.", dataPoint.getId());

        // 1. create node and add to ctx
        DataNodeList node = new DataNodeList(dataPoint, null, begin, end);
        CalcContext ctx = new CalcContext(rawService);

        // 2. expanding
        expressionService.expandingPeriod(node, ctx);

        // 3. read raw data
        ctx.cacheRawData(begin, end);

        // 4. calc
        return node.calcPeriodList(ctx);
    }
}
