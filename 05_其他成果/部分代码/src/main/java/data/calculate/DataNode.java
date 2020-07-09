package data.calculate;

import com.google.common.collect.Maps;
import com.googlecode.aviator.AviatorEvaluator;
import data.calculate.function.SumFunction;
import data.domain.DataPoint;
import data.dto.TimeValue;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 瞬时量的计算
 * <p>
 * 1. 所有瞬时量，都是由 瞬时量 和 持续量 计算出来的；
 * 2. 瞬时量计算出来的，总是一个点；
 * 3. 瞬时量 和 持续量 使用的原始值，都是目标时间点的采样值；
 */
public class DataNode {
    public final DataPoint point;
    public final LocalDateTime time;
    @Setter
    protected String expression;

    /**
     * Key: point id
     * value: node
     */
    private Map<String, DataNode> depNodes = Maps.newHashMap();

    static {
        AviatorEvaluator.addFunction(new SumFunction());
    }


    public DataNode(DataPoint dataPoint, LocalDateTime time) {
        this.point = dataPoint;
        this.time = time;
    }

    TimeValue calcInstant(CalcContext ctx) {
        if (point.isRaw()) {
            return ctx.getDb().sample(point.getId(), time);
        } else {
            Map<String, Object> env = depNodes.values().stream()
                    .collect(Collectors.toMap(DataNode::varName, n -> n.calcInstant(ctx).getValue()));

            Object value = AviatorEvaluator.execute(evalExpression(), env);
            return new TimeValue(time, value);
        }
    }

    TimeValue calcPeriod(CalcContext ctx) {
        if (point.isRaw()) {
            if (point.isContinuous()) {
                return ctx.getDb().sample(point.getId(), time);
            } else if (point.isPeriod()) {
                return ctx.getDb().read(point.getId(), time);
            } else {
                throw new RuntimeException(String.format("error: this will not occur for ever. point: %s, time: %s", point.getId(), time));
            }
        } else {
            Map<String, Object> env = depNodes.values().stream()
                    .collect(Collectors.toMap(DataNode::varName, n -> {
                        if (n instanceof DataNodeList) {
                            List<TimeValue> vs = ((DataNodeList) n).calcPeriodList(ctx);
                            return vs.stream()
                                    .map(TimeValue::getValue)
                                    .collect(Collectors.toList());
                        } else {
                            return n.calcInstant(ctx).getValue();
                        }
                    }));

            Object value = AviatorEvaluator.execute(evalExpression(), env);
            return new TimeValue(time, value);
        }
    }

    public String varName() {
        return toVarName(point.getId());
    }

    private static String toVarName(String pointId) {
        return "_" + pointId.replaceAll("\\.", "_");
    }

    private String evalExpression() {
        String evalExpression = expression;
        for (DataNode n : depNodes.values()) {
            evalExpression = evalExpression.replaceAll(String.format("\\{\\s*%s\\s*}", n.point.getId()), n.varName());
            evalExpression = evalExpression.replaceAll(String.format("\\[\\s*%s\\s*]", n.point.getId()), n.varName());
        }
        return evalExpression;
    }

    public void addDepNode(DataNode depNode) {
        this.depNodes.put(depNode.point.getId(), depNode);
    }

    public Collection<DataNode> depNodes() {
        return depNodes.values();
    }
}
