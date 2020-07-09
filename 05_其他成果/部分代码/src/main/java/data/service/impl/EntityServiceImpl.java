package data.service.impl;

import data.domain.DataEntity;
import data.service.EntityService;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.stereotype.Component;

import java.util.List;

@Service
@Component
public class EntityServiceImpl extends BaseCrudService<DataEntity> implements EntityService {
    public EntityServiceImpl() {
        super(DataEntity.class);
    }

    @Override
    public void insertOrUpdate(DataEntity domain) {
        super.insertOrUpdate(domain);
    }

    @Override
    public List<DataEntity> findByCustomerId(String customerId) {
        return finder.query().where().eq("customerId", customerId).findList();
    }

    @Override
    public DataEntity findById(String customerId, String name) {
        return finder.query()
                .where()
                .eq("customer_id", customerId)
                .eq("name", name).findOne();
    }

    @Override
    public List<String> findAllCustomerIds() {
        return finder.query()
                .select("customerId")
                .setDistinct(true)
                .findSingleAttributeList();
    }

    @Override
    public int delete(String customerId,String name) {
        this.finder.query().where()
                .eq("customerId",customerId)
                .eq("name",name)
                .delete();
        return 0;
    }
}
