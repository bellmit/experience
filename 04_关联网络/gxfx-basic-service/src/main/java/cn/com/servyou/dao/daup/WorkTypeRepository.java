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
     * ��ѯ���е���ְ����
     *
     * @return ������ְ����
     */
    List<Map<String, String>> getAllWorkType();
}
