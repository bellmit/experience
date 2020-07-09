package data.repository;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class InfluxConfig {
    private InfluxDB db;
    private Environment environment;

    @Autowired
    public InfluxConfig(InfluxDB db, Environment environment) {
        this.db = db;
        this.environment = environment;
    }

    @PostConstruct
    private void init() {
        String dbName = environment.getProperty("data.influx.database", "data");
        createDatabaseIfNotExist(dbName);
        db.setDatabase(dbName);

        int actions = Integer.parseInt(environment.getProperty("data.influx.batch.actions", "2000"));
        int flushDuration = Integer.parseInt(environment.getProperty("data.influx.batch.flushDuration.seconds", "1"));
        db.enableBatch(actions, flushDuration, TimeUnit.SECONDS);
    }

    private void createDatabaseIfNotExist(String databaseName) {
        QueryResult query = db.query(new Query("SHOW DATABASES"));
        Set<String> dbNameSet = query.getResults().get(0).getSeries().get(0).getValues().stream().map(l -> String.valueOf(l.get(0))).collect(Collectors.toSet());
        if (!dbNameSet.contains(databaseName)) {
            db.query(new Query(String.format("create database %s", databaseName)));
        }
    }
}
