package data.service.impl;

import data.domain.DataParameter;
import data.domain.DataParameterTag;
import data.dto.CsvParameter;
import data.dto.XlsParameter;
import data.service.ParameterService;
import data.service.ParameterTagService;
import io.ebean.Ebean;
import org.apache.dubbo.config.annotation.Service;
import org.grape.BaseCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import period.dto.PeriodType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Component
public class ParameterServiceImpl extends BaseCrudService<DataParameter> implements ParameterService {
    private ParameterTagService tagService;

    @Autowired
    public ParameterServiceImpl(ParameterTagService tagService) {
        super(DataParameter.class);
        this.tagService = tagService;
    }

    @Override
    public void insertOrUpdate(DataParameter domain) {
        for(DataParameterTag tag : domain.getTags()){
            if(null == tag || tag.getName().isEmpty()){
                continue;
            }
            tagService.insertOrUpdate(tag);
        }
        super.insertOrUpdate(domain);
    }

    @Override
    public List<String> tagList(String parameterId) {
        return findByIdNonNull(parameterId).getTags().stream().map(DataParameterTag::getName).collect(Collectors.toList());
    }

    @Override
    public void addTag(String parameterId, String tagName) {
        DataParameter parameter = findByIdNonNull(parameterId);

        Set<DataParameterTag> tags = parameter.getTags();
        DataParameterTag tag = new DataParameterTag(tagName);
        tags.add(tag);

        tagService.insertOrUpdate(tag);
        save(parameter);
    }

    @Override
    public void deleteTag(String parameterId, String tagName) {
        DataParameter parameter = findByIdNonNull(parameterId);

        Set<DataParameterTag> tags = parameter.getTags();
        tags.remove(new DataParameterTag(tagName));

        save(parameter);
    }

    @Override
    public List<DataParameter> findByTag(String tagName) {
        return finder.query().where().eq("tags.name", tagName).findList();
    }

    @Override
    public void csvImport(List<CsvParameter> csvParameterList) {
        csvParameterList.stream()
                .map(this::fromCsv)
                .forEach(this::insertOrUpdate);
    }

    @Override
    public List<CsvParameter> csvExport() {
        return all().stream()
                .map(this::toCsv)
                .collect(Collectors.toList());
    }

    private DataParameter fromCsv(CsvParameter csv) {
        DataParameter.Type type = DataParameter.Type.valueOf(csv.getType());
        DataParameter.DataType dataType = DataParameter.DataType.valueOf(csv.getDataType());
        Set<PeriodType> periods = Arrays.stream(csv.getPeriodSet().split(",")).map(PeriodType::valueOf).collect(Collectors.toSet());
        Set<DataParameterTag> tags = Arrays.stream(csv.getTagSet().split(",")).map(DataParameterTag::new).collect(Collectors.toSet());

        return new DataParameter(csv.getId(), csv.getName(), csv.getRemark(), type, csv.getUnit(), csv.getDataPrecision(), dataType, periods, tags);
    }

    private CsvParameter toCsv(DataParameter p) {
        String periodSet = p.getPeriods().stream().map(PeriodType::name).collect(Collectors.joining(","));
        String tagSet = p.getTags().stream().map(DataParameterTag::getName).collect(Collectors.joining(","));
        return new CsvParameter(p.getId(), p.getName(), p.getType().name(), p.getUnit(), p.getDataPrecision(), p.getDataType().name(), periodSet, tagSet, p.getRemark());
    }

    @Override
    public void excelImport(List<XlsParameter> xlsParameterList) {
        xlsParameterList.stream()
                .map(this::fromXls)
                .forEach(this::insertOrUpdate);
    }

    private DataParameter fromXls(XlsParameter xls) {
        DataParameter.Type type = DataParameter.Type.valueOf(xls.getType());
        DataParameter.DataType dataType = DataParameter.DataType.valueOf(xls.getDataType());
        Set<PeriodType> periods = Arrays.stream(xls.getPeriodSet().split(",")).map(PeriodType::valueOf).collect(Collectors.toSet());
//        Set<DataParameterTag> tags = Arrays.stream(xls.getTagSet().split("，")).map(DataParameterTag::new).collect(Collectors.toSet());
        Set<DataParameterTag> tags  = new HashSet<>() ;
        if(null != xls.getTagSet() && !xls.getTagSet().isEmpty()){
            for(String tag : xls.getTagSet().split("，")){
                if(null == tag || tag.isEmpty())
                {
                    continue;
                }
                DataParameterTag dt = new DataParameterTag(tag);
                tags.add(dt);
            }
        }
        return new DataParameter(xls.getId(), xls.getName(), xls.getRemark(), type, xls.getUnit(), Integer.parseInt(xls.getDataPrecision()), dataType, periods, tags);
    }

    @Override
    public List<DataParameter> queryList() {
        List<DataParameter> list = this.finder.query().findList();
        return list;
    }
}
