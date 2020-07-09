package data.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import data.calculate.CalcContext;
import data.calculate.DataNode;
import data.calculate.DataNodeList;
import data.domain.DataPoint;
import data.dto.DataCalculateExpression;
import data.dto.TimeValue;
import data.repository.InfluxRepository;
import data.service.ExpressionService;
import data.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import period.dto.PeriodType;
import period.dto.PointPeriod;
import period.service.PeriodService;
import period.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static data.repository.InfluxRepository.EXPRESSION;

@Slf4j
@Component
@Service
public class ExpressionServiceImpl implements ExpressionService {
    private static final Pattern ELEMENT_PATTERN = Pattern.compile("\\{\\s*[^}]+\\s*}");
    private static final Pattern LIST_PATTERN = Pattern.compile("\\[\\s*[^]]+\\s*]");

    @Reference(check = false)
    private PeriodService periodService;

    private PointService pointService;

    private InfluxRepository repository;

    @Autowired
    public ExpressionServiceImpl(PointService pointService, InfluxRepository repository) {
        this.pointService = pointService;
        this.repository = repository;
    }

    @Override
    public void write(DataCalculateExpression expression) {
        DataPoint dataPoint = pointService.findByIdNonNull(expression.getPointId());

        repository.writeString(EXPRESSION, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), expression.getValidFrom(), expression.getExpression());
    }

    @Override
    public void write(List<DataCalculateExpression> expressions) {
        for (DataCalculateExpression expression : expressions) {
            write(expression);
        }
    }

    @Override
    public List<DataCalculateExpression> read(String pointId, LocalDateTime begin, LocalDateTime end) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        List<TimeValue> values = repository.read(EXPRESSION, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);

        return values.stream()
                .map(v -> new DataCalculateExpression(pointId, v.getTime(), String.valueOf(v.getValue())))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> sample(String pointId, LocalDateTime time) {
        DataPoint dataPoint = pointService.findByIdNonNull(pointId);

        Optional<TimeValue> sample = repository.sample(EXPRESSION, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), time);

        if (sample.isPresent()) {
            return sample.map(v -> String.valueOf(v.getValue()));
        } else {
            String expression = dataPoint.getParameter().getExpression();
            if (Strings.isNullOrEmpty(expression)) {
                return Optional.empty();
            } else {
                return Optional.of(transformParameterExpression(dataPoint.entityId(), expression, dataPoint.getPeriodType()));
            }
        }
    }

    private static String transformParameterExpression(String entityId, String expression, PeriodType periodType) {
        Matcher matcher = LIST_PATTERN.matcher(expression);
        while (matcher.find()) {
            String next = matcher.group(0);

            expression = expression.replace(next, String.format("[%s__%s__%s]", entityId, next.substring(1, next.length() - 1).trim(), PeriodType.INSTANT));
            System.out.println(expression);
        }

        Matcher eMatcher = ELEMENT_PATTERN.matcher(expression);
        while (eMatcher.find()) {
            String next = eMatcher.group(0);

            expression = expression.replace(next, String.format("{%s__%s__%s}", entityId, next.substring(1, next.length() - 1).trim(), periodType));
            System.out.println(expression);
        }
        return expression;
    }

    @Override
    public String sampleNonNull(String pointId, LocalDateTime time) {
        return sample(pointId, time).orElseThrow(() -> new RuntimeException(String.format("can not found expression, pointId: %s, time: %s", pointId, DateTimeUtil.toString(time))));
    }

    @Override
    public void expandingInstant(DataNode node, CalcContext ctx) {
        // 1. read expression
        String expression = sampleNonNull(node.point.getId(), node.time);
        Preconditions.checkArgument(!LIST_PATTERN.matcher(expression).find(), "expanding instant point: %s, time: %s, expression: %s found list pattern.", node.point.getId(), node.time, expression);

        // 2. set expression
        node.setExpression(expression);

        // 3. find depend nodes
        Matcher matcher = ELEMENT_PATTERN.matcher(expression);
        while (matcher.find()) {
            // 3.1 fetch point id
            String next = matcher.group(0);
            String m = next.substring(1, next.length() - 1).trim();

            // 3.2 read point
            DataPoint dp = pointService.findByIdNonNull(m);
            if (dp.isRaw()) {
                ctx.addRawPoint(dp);
            }

            // 3.3 create node, add to dep and context
            DataNode n = new DataNode(dp, node.time);
            node.addDepNode(n);
        }

        // 4. recur expanding depend node
        node.depNodes().stream()
                .filter(n -> n.point.isCalc())
                .forEach(n -> expandingInstant(n, ctx));
    }

    @Override
    public void expandingPeriod(DataNode node, CalcContext ctx) {
        if (node instanceof DataNodeList) {
            if (node.point.isCalc()) {
                for (DataNode n : ((DataNodeList) node).getNodes()) {
                    expandingPeriod(n, ctx);
                }
            }
        } else {
            // 1. read expression
            String expression = sampleNonNull(node.point.getId(), node.time);

            // 2. set expression
            node.setExpression(expression);

            // 3. find depend element nodes
            Matcher matcher = ELEMENT_PATTERN.matcher(expression);
            while (matcher.find()) {
                // 3.1 fetch point id
                String next = matcher.group(0);
                String m = next.substring(1, next.length() - 1).trim();

                // 3.2 read point
                DataPoint dp = pointService.findByIdNonNull(m);
                Preconditions.checkArgument(dp.isContinuous()
                                || (dp.isPeriod() && Objects.equals(node.point.getPeriodType(), dp.getPeriodType())),
                        String.format("expression error: period point must calc by continuous, same period or aggregate by other period or instant point: %s, time: %s, expression: %s, error depend point: %s", node.point.getId(), node.time, expression, dp.getId()));
                if (dp.isRaw()) {
                    ctx.addRawPoint(dp);
                }

                // 3.3 create node, add to dep and context
                DataNode n = new DataNode(dp, node.time);
                node.addDepNode(n);
            }

            // 4. find depend list nodes
            PointPeriod period = periodService.getEntityPeriod(node.point.getEntity().getId(), node.point.getPeriodType(), node.time);
            Matcher listMatcher = LIST_PATTERN.matcher(expression);
            while (listMatcher.find()) {
                // 3.1 fetch point id
                String next = listMatcher.group(0);
                String m = next.substring(1, next.length() - 1).trim();

                // 3.2 read point
                DataPoint dp = pointService.findByIdNonNull(m);
                Preconditions.checkArgument(!dp.isContinuous(), String.format("expression error: can not aggregate continuous value, point: %s, time: %s, expression: %s, error depend point: %s", node.point.getId(), node.time, expression, dp.getId()));
                if (dp.isRaw()) {
                    ctx.addRawPoint(dp);
                }

                // 3.3 create node, add to dep and context
                DataNodeList n = new DataNodeList(dp, node.time, period.getBegin(), period.getEnd());
                node.addDepNode(n);
            }

            // 5. recur expanding depend node
            node.depNodes().stream()
                    .filter(n -> n.point.isCalc())
                    .forEach(n -> expandingPeriod(n, ctx));
        }
    }
}
