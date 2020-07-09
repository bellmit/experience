package data.service.impl;

import data.domain.DataCalculateExpression;
import data.service.CalculateExpressionService;
import data.service.PointService;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class CalculateExpressionServiceImpl extends BaseCrudService<DataCalculateExpression>
        implements CalculateExpressionService {

    private PointService dataPointService;

    @Autowired
    public CalculateExpressionServiceImpl(PointService dataPointService){
        super(DataCalculateExpression.class);
        this.dataPointService = dataPointService;
    }

    @Override
    public List<DataCalculateExpression> findByExpression(String pointId) {
        return this.finder.query().where().eq("point_id", pointId).findList();
    }

    @Override
    public List<DataCalculateExpression> findByExpressions(String entityId, String parameterId, String pointId) {
        List<DataCalculateExpression> list = new ArrayList<>();
        if(null == pointId || "".equals(pointId)){
            List<String> pointsIds = dataPointService.listPointId(entityId,parameterId);
            if(pointsIds==null || pointsIds.size() == 0){
                return list;
            }
            else
            {
                pointId =pointsIds.get(0);
            }
        }

        return this.finder.query().where().eq("point_id", pointId).findList();
    }

    @Override
    public void insertOrUpdateByIdIsNull(DataCalculateExpression domain) {
        super.insertOrUpdateByIdIsNull(domain);
    }

    @Override
    public void deleteById(String id){
        super.deleteById(id);
    }
}
