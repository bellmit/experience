package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.daup.SignRepository;
import cn.com.servyou.gxfx.basic.model.CompanySign;
import cn.com.servyou.gxfx.basic.model.Sign;
import cn.com.servyou.gxfx.basic.service.SignService;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lpp
 * 2018-11-27
 */
@Service
public class SignServiceImpl implements SignService {

    private final SignRepository repository;

    @Autowired
    public SignServiceImpl(SignRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Sign> getAllPreSetSign() {
        return repository.getAllPreSetSign();
    }

    @Override
    public List<CompanySign> getCompanySign(@NonNull String companyCode) {
        return repository.getCompanySign(companyCode);
    }

    @Override
    public void addCompanySign(@NonNull CompanySign companySign) {
        repository.addCompanySign(companySign);
    }

    @Override
    public void deleteCompanySign(@NonNull String companyCode, @NonNull String signId) {
        repository.deleteCompanySign(companyCode, signId);
    }

    @Override
    public Boolean checkSignName(@NonNull String companyCode, @NonNull String signName) {
        return repository.findBySignNameCount(companyCode, signName) > 0;
    }

    @Override
    public Map<String, Collection<String>> getSignMap(@NonNull Collection<String> nsrdzdahSet) {
        if (nsrdzdahSet.isEmpty()) {
            return Maps.newHashMap();
        } else {
            List<CompanySign> companySigns = repository.getCompanySigns(Lists.newArrayList(nsrdzdahSet));

            SetMultimap<String, String> multimap = Multimaps.newSetMultimap(new HashMap<String, Collection<String>>(companySigns.size()), new Supplier<Set<String>>() {
                @Override
                public Set<String> get() {
                    return new HashSet<String>();
                }
            });

            for (CompanySign cs : companySigns) {
                multimap.put(cs.getCompanyCode(), cs.getSignName());
            }

            return multimap.asMap();
        }
    }
}
