package data.dto;

import base.common.interfaces.ExeclCol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class XlsParameter implements Serializable {
    @ExeclCol("ID")
    private String id;
    @ExeclCol("名称")
    private String name;
    @ExeclCol("类型(瞬时值，期间值，持续量)")
    private String type;
    @ExeclCol("单位")
    private String unit;
    @ExeclCol("数据精度")
    private String dataPrecision;
    @ExeclCol("数据类型(开关，数字，字符串)")
    private String dataType;
    @ExeclCol("期间集合(瞬时值，持续量，小时，日，周，月，季，年)")
    private String periodSet;
    @ExeclCol("标签名集合")
    private String tagSet;
    @ExeclCol("备注")
    private String remark;
}
