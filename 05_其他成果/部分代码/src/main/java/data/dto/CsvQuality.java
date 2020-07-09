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
public class CsvQuality {
    @CsvCell(columnIndex = 1, columnName = "质量码", required = true)
    private int code;
    @CsvCell(columnIndex = 2, columnName = "中文描述", required = true)
    private String name;
    @CsvCell(columnIndex = 3, columnName = "备注")
    private String remark;
}
