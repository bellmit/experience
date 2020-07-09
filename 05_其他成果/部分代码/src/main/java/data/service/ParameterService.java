package data.service;

import data.domain.DataParameter;
import data.dto.CsvParameter;
import data.dto.XlsParameter;
import org.grape.CrudService;

import java.util.List;

public interface ParameterService extends CrudService<DataParameter> {
    /**
     * 参数的标签列表
     *
     * @param parameterId 参数ID
     * @return 标签列表
     */
    List<String> tagList(String parameterId);

    /**
     * 增加标签
     *
     * @param parameterId 参数ID
     * @param tagName     标签名称
     */
    void addTag(String parameterId, String tagName);

    /**
     * 删除标签
     *
     * @param parameterId 参数ID
     * @param tagName     标签名称
     */
    void deleteTag(String parameterId, String tagName);

    /**
     * 根据标签查找参数
     *
     * @param tagName 标签名称
     * @return 标签列表
     */
    List<DataParameter> findByTag(String tagName);

    /**
     * CSV导入参数
     *
     * @param csvParameterList 参数列表
     */
    void csvImport(List<CsvParameter> csvParameterList);

    /**
     * CSV导出参数
     *
     * @return 参数列表
     */
    List<CsvParameter> csvExport();

    /**
     * Excel导入参数
     * @param xlsParameterList 参数列表
     */
    void excelImport(List<XlsParameter> xlsParameterList);

    List<DataParameter> queryList();

    void insertOrUpdate(DataParameter domain);
}
