package data.repository;

import com.google.common.collect.Maps;
import data.dto.TimeValue;
import period.util.DateTimeUtil;
import lombok.NonNull;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import period.dto.PeriodType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.influxdb.dto.BoundParameterQuery.QueryBuilder.newQuery;

@Repository
public class InfluxRepository {
    public static final String VALID_DATA = "valid_data";
    public static final String INVALID_DATA = "invalid_data";
    public static final String OFFSET_DATA = "offset_data";
    public static final String REVISE_DATA = "revise_data";
    public static final String ARCHIVE_DATA = "archive_data";

    public static final String EXPRESSION = "expression";

    private static final String ENTITY_ID = "entityId";
    private static final String PERIOD_TYPE = "periodType";
    private static final String TIME = "time";

    private InfluxDB db;

    @Autowired
    public InfluxRepository(InfluxDB db) {
        this.db = db;
    }

    public void writeDouble(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime time, Double doubleValue) {
        db.write(Point.measurement(measurement)
                .time(DateTimeUtil.toTimestamp(time), TimeUnit.MILLISECONDS)
                .tag(ENTITY_ID, entityId)
                .tag(PERIOD_TYPE, periodType.name())
                .addField(parameterId, doubleValue)
                .build());
    }

    public void writeBatch(String measurement, String entityId, PeriodType periodType, @NonNull LocalDateTime time, @NonNull Map<String, ?> paramValues) {
        Point.Builder builder = Point.measurement(measurement)
                .time(DateTimeUtil.toTimestamp(time), TimeUnit.MILLISECONDS)
                .tag(ENTITY_ID, entityId)
                .tag(PERIOD_TYPE, periodType.name());

        for (Map.Entry<String, ?> e : paramValues.entrySet()) {
            Object val = e.getValue();
            if (val instanceof Double) {
                builder.addField(e.getKey(), (Double) val);
            } else if (val instanceof Long) {
                builder.addField(e.getKey(), (Long) val);
            } else if (val instanceof Boolean) {
                builder.addField(e.getKey(), (Boolean) val);
            } else if (val instanceof Number) {
                builder.addField(e.getKey(), (Number) val);
            } else if (val instanceof String) {
                builder.addField(e.getKey(), (String) val);
            }
        }

        db.write(builder.build());
    }

    public void writeBoolean(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime time, Boolean booleanValue) {
        db.write(Point.measurement(measurement)
                .time(DateTimeUtil.toTimestamp(time), TimeUnit.MILLISECONDS)
                .tag(ENTITY_ID, entityId)
                .tag(PERIOD_TYPE, periodType.name())
                .addField(parameterId, booleanValue)
                .build());
    }

    public void writeString(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime time, String stringValue) {
        db.write(Point.measurement(measurement)
                .time(DateTimeUtil.toTimestamp(time), TimeUnit.MILLISECONDS)
                .tag(ENTITY_ID, entityId)
                .tag(PERIOD_TYPE, periodType.name())
                .addField(parameterId, stringValue)
                .build());
    }

    public Optional<TimeValue> sample(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime time) {
        BoundParameterQuery query = newQuery(String.format("select %s, %s from %s where %s = $%s and %s = $%s and %s <= $%s order by %s desc limit 1", TIME, parameterId, measurement, ENTITY_ID, ENTITY_ID, PERIOD_TYPE, PERIOD_TYPE, TIME, TIME, TIME))
                .bind(ENTITY_ID, entityId)
                .bind(PERIOD_TYPE, periodType.name())
                .bind(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(time)))
                .create();
        QueryResult result = db.query(query);
        return toTimeValue(result);
    }

    public Optional<TimeValue> read(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime time) {
        BoundParameterQuery query = newQuery(String.format("select %s, %s from %s where %s = $%s and %s = $%s and %s = $%s limit 1", TIME, parameterId, measurement, ENTITY_ID, ENTITY_ID, PERIOD_TYPE, PERIOD_TYPE, TIME, TIME))
                .bind(ENTITY_ID, entityId)
                .bind(PERIOD_TYPE, periodType.name())
                .bind(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(time)))
                .create();
        QueryResult result = db.query(query);
        return toTimeValue(result);
    }

    public Map<String, Double> read(String measurement, String entityId, PeriodType periodType, LocalDateTime time, Collection<String> params) {
        BoundParameterQuery query = newQuery(String.format("select %s, %s from %s where %s = $%s and %s = $%s and %s = $%s limit 1", TIME, String.join(", ", params), measurement, ENTITY_ID, ENTITY_ID, PERIOD_TYPE, PERIOD_TYPE, TIME, TIME))
                .bind(ENTITY_ID, entityId)
                .bind(PERIOD_TYPE, periodType.name())
                .bind(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(time)))
                .create();
        QueryResult result = db.query(query);

        return toTimeValues(result);
    }

    public List<TimeValue> read(String measurement, String entityId, PeriodType periodType, String parameterId, LocalDateTime begin, LocalDateTime end) {
        BoundParameterQuery query = newQuery(String.format("select %s, %s from %s where %s = $%s and %s = $%s and %s >= $begin and %s < $end", TIME, parameterId, measurement, ENTITY_ID, ENTITY_ID, PERIOD_TYPE, PERIOD_TYPE, TIME, TIME))
                .bind(ENTITY_ID, entityId)
                .bind(PERIOD_TYPE, periodType.name())
                .bind("begin", TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(begin)))
                .bind("end", TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(end)))
                .create();
        QueryResult result = db.query(query);
        return toTimeValueList(result);
    }

    public void delete(String measurement, String entityId, PeriodType periodType, LocalDateTime time) {
        BoundParameterQuery query = newQuery(String.format("delete from %s where %s = $%s and %s = $%s and %s = $time", measurement, ENTITY_ID, ENTITY_ID, PERIOD_TYPE, PERIOD_TYPE, TIME))
                .bind(ENTITY_ID, entityId)
                .bind(PERIOD_TYPE, periodType.name())
                .bind(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(time)))
                .create();
        db.query(query);
    }

    public void delete(String measurement, String entityId) {
        BoundParameterQuery query = newQuery(String.format("drop series from %s where %s = $%s", measurement, ENTITY_ID, ENTITY_ID))
                .bind(ENTITY_ID, entityId)
                .create();
        db.query(query);
    }

//    public double sum(String entityId, String parameterId, LocalDateTime begin, LocalDateTime end) {
//        WhereQueryImpl query = select().sum(parameterId)
//                .from(InfluxConfig.DATABASE_DATA, OFFSET_DATA)
//                .where(eq(ENTITY_ID, entityId))
//                .and(eq(PERIOD_TYPE, PeriodType.INSTANT.name()))
//                .and(gte(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(begin))))
//                .and(lt(TIME, TimeUtil.toInfluxDBTimeFormat(DateTimeUtil.toTimestamp(end))));
//
//        QueryResult result = db.query(query);
//
//        return toDouble(result);
//    }

    private double toDouble(QueryResult result) {
        List<QueryResult.Series> series = result.getResults().get(0).getSeries();
        if (series != null) {
            return Double.valueOf(String.valueOf(series.get(0).getValues().get(0).get(1)));
        } else {
            return 0.0;
        }
    }

    private static List<TimeValue> toTimeValueList(QueryResult result) {
        List<QueryResult.Series> series = result.getResults().get(0).getSeries();
        if (series != null) {
            return series.get(0).getValues()
                    .stream()
                    .map(l -> new TimeValue(DateTimeUtil.toDateTime(TimeUtil.fromInfluxDBTimeFormat(String.valueOf(l.get(0)))), l.get(1)))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static Optional<TimeValue> toTimeValue(QueryResult result) {
        List<TimeValue> list = toTimeValueList(result);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    private Map<String, Double> toTimeValues(QueryResult result) {
        List<QueryResult.Series> series = result.getResults().get(0).getSeries();
        if (series != null) {
            QueryResult.Series s = series.get(0);

            List<List<Object>> values = s.getValues();
            if (values != null && !values.isEmpty()) {
                List<String> columns = s.getColumns();
                List<Object> row = values.get(0);

                Map<String, Double> r = Maps.newHashMap();
                for (int i = 1; i < columns.size(); i++) {
                    r.put(columns.get(i), Double.valueOf(String.valueOf(row.get(i))));
                }
                return r;
            } else {
                return Collections.emptyMap();
            }
        } else {
            return Collections.emptyMap();
        }
    }
}
