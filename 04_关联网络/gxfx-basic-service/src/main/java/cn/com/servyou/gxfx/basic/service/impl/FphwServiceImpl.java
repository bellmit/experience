package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.sjck.FphwRepository;
import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.FphwJyje;
import cn.com.servyou.gxfx.basic.model.HwJe;
import cn.com.servyou.gxfx.basic.model.QyTop;
import cn.com.servyou.gxfx.basic.service.FphwService;
import com.github.pagehelper.PageHelper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author lpp
 * 2018-11-19
 */
@Service
public class FphwServiceImpl implements FphwService {

    private final FphwRepository repository;

    @Autowired
    public FphwServiceImpl(@NonNull FphwRepository repository) {
        this.repository = repository;
    }

    @Override
    public QyTop qyTop(@NonNull String nsrdzdah, int top, @NonNull FpDataParams params) {
        return new QyTop(qyJxTop(nsrdzdah, top, params), qyXxTop(nsrdzdah, top, params));
    }

    @Override
    public List<HwJe> jyTop(@NonNull String xfnsrdzdah, @NonNull String gfnsrdzdah, int top, @NonNull FpDataParams params) {
        PageHelper.startPage(1, top);
        switch (params.getFplx()) {
            case zp:
                return repository.jyTopZp(xfnsrdzdah, gfnsrdzdah, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.jyTopPp(xfnsrdzdah, gfnsrdzdah, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.jyTopAll(xfnsrdzdah, gfnsrdzdah, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    @Override
    public List<HwJe> gjhw(String nsrdzdah, FpDataParams params) {
        switch (params.getFplx()) {
            case zp:
                return repository.gjhwZp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.gjhwPp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.gjhwAll(nsrdzdah, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    @Override
    public List<HwJe> xshw(String nsrdzdah, FpDataParams params) {
        switch (params.getFplx()) {
            case zp:
                return repository.xshwZp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.xshwPp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.xshwAll(nsrdzdah, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    @Override
    public List<FphwJyje> syHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params) {
        switch (params.getFplx()) {
            case zp:
                return repository.syHwFpZp(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.syHwFpPp(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.syHwFpAll(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    @Override
    public List<FphwJyje> xyHwFp(String nsrdzdah, Collection<String> hwSet, FpDataParams params) {
        switch (params.getFplx()) {
            case zp:
                return repository.xyHwFpZp(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.xyHwFpPp(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.xyHwFpAll(nsrdzdah, hwSet, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    private List<HwJe> qyJxTop(@NonNull String nsrdzdah, int top, @NonNull FpDataParams params) {
        PageHelper.startPage(1, top);
        switch (params.getFplx()) {
            case zp:
                return repository.qyJxTopZp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.qyJxTopPp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.qyJxTopAll(nsrdzdah, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }

    private List<HwJe> qyXxTop(@NonNull String nsrdzdah, int top, @NonNull FpDataParams params) {
        PageHelper.startPage(1, top);
        switch (params.getFplx()) {
            case zp:
                return repository.qyXxTopZp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case pp:
                return repository.qyXxTopPp(nsrdzdah, params.getBeginStr(), params.getEndStr());
            case all:
                return repository.qyXxTopAll(nsrdzdah, params.getBeginStr(), params.getEndStr());
            default:
                throw new RuntimeException("unsupported fplx: " + params.getFplx().name());
        }
    }
}
