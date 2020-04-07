package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.daup.SettingRepository;
import cn.com.servyou.gxfx.basic.model.Setting;
import cn.com.servyou.gxfx.basic.service.SettingService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-29
 */
@Service
public class SettingServiceImpl implements SettingService {
    private final SettingRepository repository;

    @Autowired
    public SettingServiceImpl(SettingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, String> loadSettings(@NonNull String userId) {
        List<Setting> list = repository.getSettingsByUserId(userId);

        return Setting.toMap(list);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveSettings(@NonNull final String userId, @NonNull Map<String, String> settings) {
        repository.deleteSettingsByUserId(userId);

        repository.addUserDefalutSetting(Setting.toList(userId, settings));
    }
}
