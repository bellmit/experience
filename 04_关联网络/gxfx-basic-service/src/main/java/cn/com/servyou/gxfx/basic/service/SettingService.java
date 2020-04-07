package cn.com.servyou.gxfx.basic.service;

import java.util.Map;

/**
 * @author lpp
 * 2018-11-29
 */
public interface SettingService {

    /**
     * �����û�Ĭ������
     *
     * @param userId �û�ID
     * @return Ĭ�����ã�����Ϊempty
     */
    Map<String, String> loadSettings(String userId);

    /**
     * �����û�Ĭ������
     *
     * @param userId   �û�ID
     * @param settings ����
     */
    void saveSettings(String userId, Map<String, String> settings);
}
