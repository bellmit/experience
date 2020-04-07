package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.sjck.NsrRepository;
import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.service.NsrService;
import cn.com.servyou.tdap.web.PagerBean;
import com.github.pagehelper.PageHelper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author lpp
 * 2018-10-22
 */
@Service
public class NsrServiceImpl implements NsrService {

    private NsrRepository repository;

    @Autowired
    public NsrServiceImpl(@NonNull NsrRepository repository) {
        this.repository = repository;
    }

    @Override
    public Nsr getNsr(@NonNull String nsrdzdah) {
        return repository.getNsr(nsrdzdah);
    }

    @Override
    public Map<String, Nsr> getNsrBatch(@NonNull Collection<String> nsrdzdahs) {
        if (nsrdzdahs.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return repository.getNsrBatch(nsrdzdahs);
        }
    }

    @Override
    public PagerBean<Nsr> search(@NonNull Nsr.SearchCondition search) {
        PageHelper.startPage(search.getPageIndex(), search.getPageSize());
        return new PagerBean<Nsr>(repository.search(search));
    }
}
