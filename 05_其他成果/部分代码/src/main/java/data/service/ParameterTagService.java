package data.service;

import data.domain.DataParameterTag;
import org.grape.CrudService;

import java.util.List;

public interface ParameterTagService extends CrudService<DataParameterTag> {
    /**
     * 删除标签
     *
     * @param ids 标签ID
     */
    int delete(List<String> ids);
}
