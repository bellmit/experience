package data.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(value = "数据点的值", parent = TimeValue.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointValue extends TimeValue {
    @ApiModelProperty("数据点的ID")
    private String pointId;
}
