package data.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.grape.BaseDomain;

import javax.persistence.Entity;

@ApiModel(value = "质量码", parent = BaseDomain.class)
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DataQuality extends BaseDomain {
    public static final int GOOD = 192;
    @ApiModelProperty("质量码")
    private int code;

    public DataQuality(int code, String name) {
        super(String.valueOf(code), name);
        super.name = name;
    }

    public DataQuality(int code, String name, String remark) {
        super(String.valueOf(code), name, remark);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
//        super.id = String.valueOf(code);
    }
}
