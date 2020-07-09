package data.service;

import data.calculate.CalcContext;
import data.calculate.DataNode;
import data.dto.DataCalculateExpression;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpressionService {
    /**
     * 写计算公式
     *
     * @param expression 公式
     */
    void write(@NonNull DataCalculateExpression expression);

    /**
     * 写计算公式
     *
     * @param expressions 公式列表
     */
    void write(@NonNull List<DataCalculateExpression> expressions);

    /**
     * 读一段时间点的计算公式
     *
     * @param pointId 点的ID
     * @param begin   开始时间
     * @param end     结束时间
     * @return 这一段时间的计算公式
     */
    List<DataCalculateExpression> read(@NonNull String pointId, @NonNull LocalDateTime begin, @NonNull LocalDateTime end);

    /**
     * 取某一点在某时间适用的计算公式
     *
     * @param pointId 点的ID
     * @param time    结果值时间
     * @return 该点适用的计算公式
     */
    Optional<String> sample(@NonNull String pointId, @NonNull LocalDateTime time);

    /**
     * 采样，结果非空，当为空时会抛异常
     *
     * @param pointId 点的ID
     * @param time    结果值时间
     * @return 该点适用的计算公式
     */
    @NonNull
    String sampleNonNull(String pointId, LocalDateTime time);

    /**
     * 递归展开节点
     *
     * @param node 节点
     * @param ctx  上下文
     */
    void expandingInstant(DataNode node, CalcContext ctx);

    void expandingPeriod(DataNode node, CalcContext ctx);
}
