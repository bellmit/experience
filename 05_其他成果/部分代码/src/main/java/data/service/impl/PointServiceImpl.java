package data.service.impl;

import data.domain.DataPoint;
import data.service.PointService;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.stereotype.Component;

import java.util.List;

@Service
@Component
public class PointServiceImpl extends BaseCrudService<DataPoint> implements PointService {
    public PointServiceImpl() {
        super(DataPoint.class);
    }

    @Override
    public List<DataPoint> findByEntityId(String entityId) {
        return finder.query().where().eq("entity.id", entityId).findList();
    }

    @Override
    public int delete(String entityId,String parameterId){
        return finder.query().where().eq("entity_id",entityId)
                .eq("parameter_id",parameterId).delete();
    }

    @Override
    public List<String> listPointId(String entityId, String parameterId) {
        return finder.query().select("id").where().eq("entity_id",entityId)
                .eq("parameter_id",parameterId).findIds();
    }
}
