package cn.com.servyou.dao.daup;

import cn.com.servyou.gxfx.basic.model.Setting;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-29
 */
@Repository
public interface SettingRepository {
    /**
     * ��ѯĳ�û�������
     *
     * @param userId �û�ID
     * @return ���û���������
     */
    List<Setting> getSettingsByUserId(@Param("userId") String userId);

    /**
     * ɾ��ĳ�û�������
     *
     * @param userId �û�ID
     */
    void deleteSettingsByUserId(@Param("userId") String userId);

    /**
     * �����û�����
     *
     * @param list �û������б�
     */
    void addUserDefalutSetting(@Param("list") Collection<Setting> list);
}

