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
     * 查询某用户的设置
     *
     * @param userId 用户ID
     * @return 该用户所有设置
     */
    List<Setting> getSettingsByUserId(@Param("userId") String userId);

    /**
     * 删除某用户的设置
     *
     * @param userId 用户ID
     */
    void deleteSettingsByUserId(@Param("userId") String userId);

    /**
     * 增加用户设置
     *
     * @param list 用户设置列表
     */
    void addUserDefalutSetting(@Param("list") Collection<Setting> list);
}

