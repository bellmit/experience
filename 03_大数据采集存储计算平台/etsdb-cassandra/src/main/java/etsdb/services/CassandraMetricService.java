/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package etsdb.services;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import etsdb.domains.DataPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class CassandraMetricService implements AccumulatedMetricService {
    private static final Logger logger = LoggerFactory.getLogger(CassandraMetricService.class);

    private static final String CREATE_VALID_TABLE = "CREATE TABLE IF NOT EXISTS etsdb.data_valid "
            + "(metric text, time timestamp, val text, PRIMARY KEY (metric, time) );";
    private static final String CREATE_INVALID_TABLE =
            "CREATE TABLE IF NOT EXISTS etsdb.data_invalid "
                    + "(metric text, time timestamp, val text, PRIMARY KEY (metric, time) );";

    private static final String INSERT_VALID =
            "INSERT INTO etsdb.data_valid (metric, time, val) VALUES (?,?,?)";
    private static final String INSERT_INVALID =
            "INSERT INTO etsdb.data_invalid (metric, time, val) VALUES (?,?,?)";

    private static final String SAMPLE_FORWARD_VALID =
            "SELECT * FROM etsdb.data_valid WHERE metric = ? AND time > ? order by time desc limit 1";
    private static final String SAMPLE_FORWARD_INVALID =
            "SELECT * FROM etsdb.data_invalid WHERE metric = ? AND time > ? order by time desc limit 1";

    private static final String SAMPLE_VALID =
            "SELECT * FROM etsdb.data_valid WHERE metric = ? AND time <= ? order by time desc limit 1";
    private static final String SAMPLE_INVALID =
            "SELECT * FROM etsdb.data_invalid WHERE metric = ? AND time <= ? order by time desc limit 1";

    private static final String QUERY_VALID =
            "SELECT * FROM etsdb.data_valid WHERE metric = ? AND time >= ? AND time <= ?";
    private static final String QUERY_INVALID =
            "SELECT * FROM etsdb.data_invalid WHERE metric = ? AND time >= ? AND time <= ?";

    private static final String DELETE_VALID =
            "DELETE FROM etsdb.data_valid WHERE metric = ? AND time >= ? AND time <= ?";
    private static final String DELETE_INVALID =
            "DELETE FROM etsdb.data_invalid WHERE metric = ? AND time >= ? AND time <= ?";

    private final PreparedStatement insertValid;
    private final PreparedStatement insertInValid;

    private final PreparedStatement sampleValid;
    private final PreparedStatement sampleInValid;

    private final PreparedStatement sampleForwardValid;
    private final PreparedStatement sampleForwardInValid;

    private final PreparedStatement queryValid;
    private final PreparedStatement queryInValid;

    private final PreparedStatement deleteValid;
    private final PreparedStatement deleteInValid;

    private final Session session;

    private final BlockingQueue<Map.Entry<DataPoint, CompletableFuture<Void>>> asyncWriteQueue = Queues.newLinkedBlockingQueue();

    public CassandraMetricService(Session session) {
        this.session = session;

        session.execute(CREATE_VALID_TABLE);
        session.execute(CREATE_INVALID_TABLE);

        insertValid = session.prepare(INSERT_VALID);
        insertInValid = session.prepare(INSERT_INVALID);

        sampleValid = session.prepare(SAMPLE_VALID);
        sampleInValid = session.prepare(SAMPLE_INVALID);

        sampleForwardValid = session.prepare(SAMPLE_FORWARD_VALID);
        sampleForwardInValid = session.prepare(SAMPLE_FORWARD_INVALID);

        queryValid = session.prepare(QUERY_VALID);
        queryInValid = session.prepare(QUERY_INVALID);

        deleteValid = session.prepare(DELETE_VALID);
        deleteInValid = session.prepare(DELETE_INVALID);

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("etsdb-async-write-%d").build();
        Executors.newSingleThreadScheduledExecutor(threadFactory).scheduleWithFixedDelay(this::writeAsyncInternal, 5, 1, TimeUnit.SECONDS);
    }

    public CassandraMetricService(String... nodes) {
        this(CassandraSessionFactory.createSession(nodes));
    }

    @Override
    public void write(DataPoint dp) {
        if (dp != null && dp.checkValid()) {
            session.execute(insertStmt(dp));
        }
    }

    /**
     * Modify BatchStatement to BoundStatement.
     * <p>
     * Warn: do not use batch.
     * <p>
     * Because batches are not for improving performance.
     * <p>
     * They are used for ensuring atomicity and isolation.
     * <p>
     * Batching can be effective for single partition write operations.
     * <p>
     * But batches are often mistakenly used in an attempt to optimize performance.
     * <p>
     * Depending on the batch operation, the performance may actually worsen.
     * <p>
     * So, DO NOT USE BATCH unless you known what you do.
     *
     * @param dps dps
     * @author li
     */
    @Override
    public void write(List<DataPoint> dps) {
        if (dps != null && !dps.isEmpty()) {
            dps.parallelStream().filter(Objects::nonNull).forEach(this::write);
        }
    }

    @Override
    public CompletableFuture<Void> writeAsync(DataPoint dp) {
        if (dp != null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            asyncWriteQueue.offer(new AbstractMap.SimpleImmutableEntry<>(dp, future));
            return future;
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private void writeAsyncInternal() {
        try {
            while (!asyncWriteQueue.isEmpty()) {
                List<Map.Entry<DataPoint, CompletableFuture<Void>>> list = Lists.newArrayList();
                asyncWriteQueue.drainTo(list);
                List<DataPoint> dpl = list.stream().map(Map.Entry::getKey).collect(Collectors.toList());
                write(dpl);
                list.stream().map(Map.Entry::getValue).forEach(future -> future.complete(null));
            }
        } catch (Throwable e) {
            logger.error("write async internal error", e);
        }
    }

    @Nullable
    @Override
    public DataPoint sample(@NotNull String metric, long timestamp, boolean valid, boolean forward) {
        checkArgument(Objects.nonNull(metric), "metric must non null.");

        BoundStatement bs = forward ? sampleForwardStmt(valid) : sampleStmt(valid);

        ResultSet r = session.execute(bs.bind(metric, new Date(timestamp)));

        List<DataPoint> dps = fromResultSet(r, valid);

        return dps.isEmpty() ? null : dps.get(0);
    }

    @NotNull
    @Override
    public List<DataPoint> read(@NotNull String metric, long start, long end, boolean onlyValid) {
        checkArgument(Objects.nonNull(metric), "metric must non null.");
        checkArgument(start <= end,
                String.format("Error: Start time > end time. start: %d, end: %d.", start, end));

        Map<Long, DataPoint> results = Maps.newHashMap();

        if (!onlyValid) {
            BoundStatement bs = queryStmt(false).bind(metric, new Date(start), new Date(end));
            ResultSet r = session.execute(bs);
            fromResultSet(r, false).forEach(dp -> results.put(dp.getTimestamp(), dp));
        }

        BoundStatement statement = queryStmt(true).bind(metric, new Date(start), new Date(end));
        ResultSet resultSet = session.execute(statement);
        fromResultSet(resultSet, true).forEach(dp -> results.put(dp.getTimestamp(), dp));

        return results.values().stream().sorted(Comparator.comparingLong(DataPoint::getTimestamp)).collect(Collectors.toList());
    }

    @Override
    public void delete(String metric, long start, long end) {
        session.execute(deleteStmt(false).bind(metric, new Date(start), new Date(end)));
        session.execute(deleteStmt(true).bind(metric, new Date(start), new Date(end)));
    }

    private List<DataPoint> fromResultSet(ResultSet r, boolean valid) {
        final List<DataPoint> results = Lists.newArrayList();

        r.forEach(row -> {
            String metric = row.getString("metric");
            String val = row.getString("val");
            Date timestamp = row.getTimestamp("time");
            DataPoint dp;
            if (valid) {
                dp = new DataPoint(metric, timestamp.getTime(), val);
            } else {
                String[] items = val.split("\\|");
                dp = new DataPoint(metric, timestamp.getTime(), items[0], Integer.parseInt(items[1]));
            }
            results.add(revise(dp));
        });
        return results;
    }

    protected DataPoint revise(DataPoint dp) {
        return dp;
    }

    private BoundStatement insertStmt(DataPoint dp) {
        if (dp.valid()) {
            return new BoundStatement(insertValid).bind(dp.getMetric(), dp.getDate(), dp.getVal());
        } else {
            return new BoundStatement(insertInValid).bind(dp.getMetric(), dp.getDate(), dp.val() + "|"
                    + dp.getQuality());
        }
    }

    private BoundStatement sampleStmt(boolean valid) {
        return valid ? new BoundStatement(sampleValid) : new BoundStatement(sampleInValid);
    }

    private BoundStatement sampleForwardStmt(boolean valid) {
        return valid ? new BoundStatement(sampleForwardValid) : new BoundStatement(sampleForwardInValid);
    }

    private BoundStatement queryStmt(boolean valid) {
        return valid ? new BoundStatement(queryValid) : new BoundStatement(queryInValid);
    }

    private BoundStatement deleteStmt(boolean valid) {
        return valid ? new BoundStatement(deleteValid) : new BoundStatement(deleteInValid);
    }
}
