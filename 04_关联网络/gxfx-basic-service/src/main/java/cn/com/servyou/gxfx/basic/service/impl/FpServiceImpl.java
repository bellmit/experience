package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FpFilterParams;
import cn.com.servyou.gxfx.basic.model.SxyType;
import cn.com.servyou.gxfx.basic.repository.FpRepository;
import cn.com.servyou.gxfx.basic.service.FpService;
import cn.com.servyou.gxfx.model.Fp;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-11-14
 */
@Service
public class FpServiceImpl implements FpService {
    private final FpRepository repository;

    @Autowired
    public FpServiceImpl(FpRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<String> searchSxy(@NonNull Collection<String> vIds, @NonNull FpFilterParams params) {
        switch (params.getFilterType()) {
            case top:
                return repository.searchTop(vIds, params.getSxyType(), params.cols(), Long.valueOf(Math.round(params.getFilterValue())).intValue());
            case jyje:
                return repository.searchJyzb(vIds, params.getSxyType(), params.cols(), params.getFilterValue());
            default:
                throw new RuntimeException("unsupported filter type: " + params.getFilterType());
        }
    }

    @Override
    public Map<String, Double> readJxze(@NonNull Collection<String> vIds, @NonNull FpDataParams params) {
        return repository.readZe(vIds, params.cols(), SxyType.sy);
    }

    @Override
    public Map<String, Double> readXxze(@NonNull Collection<String> vIds, @NonNull FpDataParams params) {
        return repository.readZe(vIds, params.cols(), SxyType.xy);
    }

    @Override
    public <T extends Fp> Set<T> readPl(@NonNull Collection<String> fromSet, @NonNull Collection<String> toSet, @NonNull FpDataParams params, @NonNull Class<T> tClass) {
        Map<String, Double> xxzeMap = readXxze(fromSet, params);
        Map<String, Double> jxzeMap = readJxze(toSet, params);

        Set<T> plSet = repository.pl(fromSet, toSet, params.cols(), tClass);

        for (T pl : plSet) {
            Double jyje = pl.getJyje();
            pl.setXxzb(jyje / xxzeMap.get(pl.getFrom()));
            pl.setJxzb(jyje / jxzeMap.get(pl.getTo()));
        }
        return plSet;
    }
}
