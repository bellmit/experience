package data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.csveed.annotations.CsvCell;
import org.csveed.annotations.CsvFile;
import org.csveed.bean.ColumnNameMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CsvFile(mappingStrategy = ColumnNameMapper.class, separator = ',')
public class CsvParameter {
    @CsvCell(columnIndex = 1, columnName = "ID", required = true)
    private String id;
    @CsvCell(columnIndex = 2, columnName = "名称", required = true)
    private String name;
    @CsvCell(columnIndex = 3, columnName = "类型(INSTANT,PERIOD,CONTINUOUS)", required = true)
    private String type;
    @CsvCell(columnIndex = 4, columnName = "单位", required = true)
    private String unit;
    @CsvCell(columnIndex = 5, columnName = "数据精度", required = true)
    private int dataPrecision;
    @CsvCell(columnIndex = 6, columnName = "数据类型(BOOLEAN,DOUBLE,STRING)", required = true)
    private String dataType;
    @CsvCell(columnIndex = 7, columnName = "期间集合(INSTANT,CONTINUOUS,HOUR,DAY,WEEK,MONTH,SEASON,YEAR)")
    private String periodSet;
    @CsvCell(columnIndex = 8, columnName = "标签名集合")
    private String tagSet;
    @CsvCell(columnIndex = 9, columnName = "备注")
    private String remark;
}
