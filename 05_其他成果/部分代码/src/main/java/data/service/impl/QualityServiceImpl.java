package data.service.impl;

import data.domain.DataQuality;
import data.dto.CsvQuality;
import data.service.QualityService;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
public class QualityServiceImpl extends BaseCrudService<DataQuality> implements QualityService {
    public QualityServiceImpl() {
        super(DataQuality.class);
    }

    @Override
    public void insertOrUpdate(DataQuality quality){
        super.insertOrUpdate(quality);
    }

    @Override
    public List<DataQuality> queryList() {
        return this.finder.query().findList();
    }

    @Override
    public void csvImport(List<CsvQuality> qualityList) {
        qualityList.stream()
                .map(this::fromCsv)
                .forEach(this::insertOrUpdate);
    }

    @Override
    public List<CsvQuality> csvExport() {
        return all().stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
    }

    private DataQuality fromCsv(CsvQuality csv) {
        return new DataQuality(csv.getCode(), csv.getName(), csv.getRemark());
    }

    private CsvQuality toCsv(DataQuality quality) {
        return new CsvQuality(quality.getCode(), quality.getName(), quality.getRemark());
    }
}
