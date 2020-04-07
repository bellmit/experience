package etsdb.services;

import com.datastax.driver.core.Session;
import etsdb.domains.DataPoint;

public class RatioReviseCassandraMetricService extends CassandraMetricService {
    private final RatioReviseService ratioReviseService;

    public RatioReviseCassandraMetricService(RatioReviseService ratioReviseService, Session session) {
        super(session);
        this.ratioReviseService = ratioReviseService;
    }

    public RatioReviseCassandraMetricService(RatioReviseService ratioReviseService, String... nodes) {
        super(nodes);
        this.ratioReviseService = ratioReviseService;
    }

    @Override
    protected DataPoint revise(DataPoint dp) {
        return ratioReviseService.revise(dp);
    }
}
