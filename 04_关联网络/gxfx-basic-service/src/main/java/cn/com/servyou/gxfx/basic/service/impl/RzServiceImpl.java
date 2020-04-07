package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.daup.WorkTypeRepository;
import cn.com.servyou.gxfx.basic.repository.RzRepository;
import cn.com.servyou.gxfx.basic.service.RzService;
import cn.com.servyou.gxfx.model.Rz;
import cn.com.servyou.gxfx.model.RzType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-15
 */
@Service
public class RzServiceImpl implements RzService {
    private final RzRepository repository;
    private final WorkTypeRepository workTypeRepository;

    @Autowired
    public RzServiceImpl(@NonNull RzRepository repository, @NonNull WorkTypeRepository workTypeRepository) {
        this.repository = repository;
        this.workTypeRepository = workTypeRepository;
    }

    @Override
    public Set<String> searchRyglqy(@NonNull String id, @NonNull Collection<RzType> rzTypes) {
        return repository.searchRyglqy(id, rzTypes);
    }

    @Override
    public <T extends Rz> Set<T> readRz(@NonNull Collection<String> qyIds, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass) {
        return repository.readRz(qyIds, rzTypes, tClass);
    }

    @Override
    public <T extends Rz> Set<T> readRz(@NonNull Collection<String> newIds, @NonNull Collection<String> allIds, @NonNull Collection<RzType> rzTypes, @NonNull Class<T> tClass) {
        return repository.readRz(newIds, allIds, rzTypes, tClass);
    }

    @Override
    public List<Map<String, String>> getAllWorkType() {
        return workTypeRepository.getAllWorkType();
    }
}
