package cn.com.servyou.gxfx.basic.service;

import java.util.Map;

/**
 * @author lpp
 * 2018-11-29
 */
public interface SettingService {

    /**
     * 加载用户默认配置
     *
     * @param userId 用户ID
     * @return 默认配置，可能为empty
     */
    Map<String, String> loadSettings(String userId);

    /**
     * 保存用户默认配置
     *
     * @param userId   用户ID
     * @param settings 设置
     */
    void saveSettings(String userId, Map<String, String> settings);
}
