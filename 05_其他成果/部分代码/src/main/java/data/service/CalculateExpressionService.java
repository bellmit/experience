package data.service;


import data.domain.DataCalculateExpression;
import org.grape.CrudService;

import java.util.List;

public interface CalculateExpressionService extends CrudService<DataCalculateExpression> {
    /**
     * 根据点数据关联ID查找点数据公式表
     * @param pointId 公式ID
     */
    List<DataCalculateExpression> findByExpression(String pointId);

    /**
     * 根据实体ID、参数ID、点数据ID查询公式
     * @param  entityId 实体ID
     * @param  parameterId 参数ID
     * @param  pointId 点数据ID
     * @return 公式列表
     */
    List<DataCalculateExpression> findByExpressions(String entityId, String parameterId, String pointId);

    /**
     * 根据id删除点公式
     * @param  id
     */
    void deleteById(String id);

}
