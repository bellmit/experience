package etsdb.services;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class CassandraSessionFactory {
    private static final Logger logger = LoggerFactory.getLogger(CassandraSessionFactory.class);

    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS etsdb "
            + "WITH replication = {'class':'SimpleStrategy', 'replication_factor':2};";

    public static Session createSession(String... nodes) {
        Cluster cluster = Cluster.builder().addContactPoints(nodes) //
                .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE) //
                .withReconnectionPolicy(new ConstantReconnectionPolicy(10000L)) //
                .build();

        cluster.getConfiguration().getProtocolOptions()
                .setCompression(ProtocolOptions.Compression.SNAPPY);

        Metadata metadata = cluster.getMetadata();

        logger.info(format("Connected to cluster: %s", metadata.getClusterName()));

        metadata.getAllHosts().forEach(host -> {
            String msgFmt = "Datacenter: %s; Host: %s; Rack: %s";
            logger.info(format(msgFmt, host.getDatacenter(), host.getAddress(), host.getRack()));
        });

        Session session = cluster.connect();
        session.execute(CREATE_KEYSPACE);
        return session;
    }
}
