package data.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.grape.BaseDomain;

import javax.persistence.Entity;
import java.util.Date;

@ApiModel(value = "点公式", parent = BaseDomain.class)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class DataCalculateExpression extends BaseDomain {

    @ApiModelProperty("点数据ID")
    private String pointId;

    @ApiModelProperty("valid_from")
    private Date validFrom;

    @ApiModelProperty("expression")
    private String expression;

    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty("name")
    private String name;
}
