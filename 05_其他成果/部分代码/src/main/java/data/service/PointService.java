package data.service;

import data.domain.DataPoint;
import org.grape.CrudService;

import java.util.List;

public interface PointService extends CrudService<DataPoint> {
    /**
     * 根据实体ID查找数据点
     *
     * @param entityId 实体ID
     * @return 数据点列表
     */
    List<DataPoint> findByEntityId(String entityId);

    /**
     * 根据实体ID和参数ID删除点数据
     * @param  entityId
     * @param  parameterId
     */
    int delete(String entityId,String parameterId);

    /**
     * 根据实体ID和参数ID查询点数据ID
     * @param  entityId 实体ID
     * @param  parameterId 参数ID
     * @return 点数据ID列表
     */
    List<String> listPointId(String entityId,String parameterId);
}
