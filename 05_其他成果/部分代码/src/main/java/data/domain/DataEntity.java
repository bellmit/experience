package data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.grape.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@ApiModel(value = "实体", parent = BaseDomain.class)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class DataEntity extends BaseDomain {
    /**
     * customer id, mapping to `b`
     */
    @ApiModelProperty("客户ID(小b)")
    @Column(nullable = false)
    private String customerId;

    @JsonIgnore
    @ApiModelProperty("该实体的所有点")
    @OneToMany(mappedBy = "entity")
    protected Set<DataPoint> points;
}
