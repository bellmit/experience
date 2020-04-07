package cn.com.servyou.gxfx.basic.service.impl;

import cn.com.servyou.dao.frck.SbRepository;
import cn.com.servyou.gxfx.basic.model.SbQysds;
import cn.com.servyou.gxfx.basic.model.SbZzs;
import cn.com.servyou.gxfx.basic.service.SbService;
import cn.com.servyou.gxfx.basic.view.QySbResponseEntity;
import cn.com.servyou.gxfx.basic.view.SdsEntity;
import cn.com.servyou.gxfx.basic.view.XfsEntity;
import cn.com.servyou.gxfx.basic.view.ZzsEntity;
import lombok.NonNull;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lpp
 * 2018-12-07
 */
@Service
public class SbServiceImpl implements SbService {

    @Resource
    private SbRepository repository;

    @Override
    public QySbResponseEntity getQySb(@NonNull String nsrdzdah, @NonNull String begin, @NonNull String end) {
        ZzsEntity zzs = getZzsEntity(nsrdzdah, begin, end);
//        XfsEntity xfs = getXfsEntity(nsrdzdah, begin, end);
        XfsEntity xfs = null;
        SdsEntity qysds = getSdsEntity(nsrdzdah);

        return new QySbResponseEntity(zzs, xfs, qysds);
    }

    private ZzsEntity getZzsEntity(String nsrdzdah, String begin, String end) {
        SbZzs sbZzs = repository.getZzs(nsrdzdah, begin, end);
        Double zzsRkse = repository.getRkse(nsrdzdah, "10101", begin, end);
        return new ZzsEntity(orDefault(sbZzs, new SbZzs()), getOrDefault(zzsRkse), begin, end);
    }

    private XfsEntity getXfsEntity(String nsrdzdah, String begin, String end) {
        Double xfsYnse = repository.getXfsYnse(nsrdzdah, begin, end);
        Double xfsRkse = repository.getRkse(nsrdzdah, "10102", begin, end);
        return new XfsEntity(getOrDefault(xfsYnse), getOrDefault(xfsRkse), begin, end);
    }

    private SdsEntity getSdsEntity(String nsrdzdah) {
        SbQysds lastYear = repository.getQysds(nsrdzdah, String.valueOf(DateTime.now().getYear() - 1));
        SbQysds beforeLastYear = repository.getQysds(nsrdzdah, String.valueOf(DateTime.now().getYear() - 2));

        Double qysdsRkse1 = getQysdsRkse(nsrdzdah, String.valueOf(DateTime.now().getYear() - 1));
        Double qysdsRkse2 = getQysdsRkse(nsrdzdah, String.valueOf(DateTime.now().getYear() - 2));

        return new SdsEntity(orDefault(lastYear, new SbQysds()), orDefault(beforeLastYear, new SbQysds()), getOrDefault(qysdsRkse1), getOrDefault(qysdsRkse2));
    }

    private Double getQysdsRkse(String nsrdzdah, String year) {
        return repository.getRkse(nsrdzdah, "10104", String.format("%s01", year), String.format("%s12", year));
    }

    private <T> T orDefault(T lastYear, T defaultVal) {
        return lastYear != null ? lastYear : defaultVal;
    }

    private double getOrDefault(Double xfsYnse) {
        return xfsYnse != null ? xfsYnse : 0.0;
    }
}
