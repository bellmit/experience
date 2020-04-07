package cn.com.servyou.dao.daup;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-29
 */
@Repository
public interface WorkTypeRepository {
    /**
     * 查询所有的任职类型
     *
     * @return 所有任职类型
     */
    List<Map<String, String>> getAllWorkType();
}
