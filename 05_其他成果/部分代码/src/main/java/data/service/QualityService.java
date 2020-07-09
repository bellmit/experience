package data.service;

import data.domain.DataQuality;
import data.dto.CsvQuality;
import org.grape.CrudService;

import java.util.List;

public interface QualityService extends CrudService<DataQuality> {
    /**
     * 查询质量码
     */
    List<DataQuality> queryList();

    /**
     * CSV导入质量码
     *
     * @param qualityList 质量码列表
     */
    void csvImport(List<CsvQuality> qualityList);

    /**
     * CSV导出质量码
     *
     * @return 质量码列表
     */
    List<CsvQuality> csvExport();

    /**
     * 保存质量码
     * @param quality
     */
    void insertOrUpdate(DataQuality quality);
}
