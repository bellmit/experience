package data.service.impl;


import data.domain.DataParameterTag;
import data.service.ParameterTagService;
import io.ebean.DB;
import io.ebean.Ebean;
import io.ebean.annotation.Transactional;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.stereotype.Component;

import java.util.List;

@Service
@Component
public class ParameterTagServiceImpl extends BaseCrudService<DataParameterTag> implements ParameterTagService {
    public ParameterTagServiceImpl() {
        super(DataParameterTag.class);
    }

    @Override
    public int delete(List<String> ids) {
        for (String id : ids){
            this.deleteById(id);
        }
//        String sql = "delete from data_parameter_data_parameter_tag where data_parameter_tag_id in (?)";
//        for (String id : ids) {
//            DB.sqlUpdate(sql).setParameter(1,id).execute();
//        }
//        Ebean.deleteAll(DataParameterTag.class,ids);
        return 0;
    }
}
