package data.service;

import data.domain.DataEntity;
import org.grape.CrudService;

import java.util.List;

public interface EntityService extends CrudService<DataEntity> {
    /**
     * 根据客户ID查找实体
     *
     * @param customerId 客户ID
     * @return 实体
     */
    List<DataEntity> findByCustomerId(String customerId);

    /**
     * 保存实体
     *
     * @param domain
     */
    @Override
    void insertOrUpdate(DataEntity domain);

    /**
     * 根据客户ID和名称查找实体
     *
     * @param customerId 客户ID
     * @param name       名称
     * @return 实体
     */
    DataEntity findById(String customerId, String name);

    /**
     * 查询所有的customerId
     *
     * @return customerId
     */
    List<String> findAllCustomerIds();

    int delete(String customerId,String name);
}
