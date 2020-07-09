package data.domain;

import io.ebean.annotation.DbEnumValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.grape.BaseDomain;
import period.dto.PeriodType;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ApiModel(value = "参数", parent = BaseDomain.class)
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DataParameter extends BaseDomain {
    @ApiModelProperty("参数公式")
    @Column(nullable = true)
    private String expression;
    @ApiModelProperty("参数类型")
    @Column(nullable = false)
    private Type type;
    @ApiModelProperty("单位")
    @Column(nullable = false)
    private String unit;
    @ApiModelProperty("数据精度")
    @Column(nullable = false)
    private int dataPrecision;
    @ApiModelProperty("数据类型")
    @Column(nullable = false)
    private DataType dataType;
    @ApiModelProperty(hidden = true)
    private String periodSet;
    @Transient
    @ApiModelProperty("有效期间")
    private Set<PeriodType> periods;
    @ApiModelProperty("标签，参数标签对象内可以只传name，id会自动生成")
    @ManyToMany
    private Set<DataParameterTag> tags;

    public DataParameter(String id, String name, String remark, Type type, String unit, Integer dataPrecision, DataType dataType, Set<PeriodType> periods, Set<DataParameterTag> tags) {
        super(id, name, remark);
        this.type = type;
        this.unit = unit;
        this.dataPrecision = dataPrecision;
        this.dataType = dataType;
        setPeriods(periods);
        if(null!=tags&&tags.size()>0)
        {
            this.tags = tags;
        }

    }

    public void setPeriods(Set<PeriodType> periods) {
        this.periods = periods;
        this.periodSet = periods.stream().map(PeriodType::name).collect(Collectors.joining(","));
    }

    public Set<PeriodType> getPeriods() {
        return Arrays.stream(periodSet.split(",")).map(PeriodType::valueOf).collect(Collectors.toSet());
    }

    public enum Type {
        /**
         * 瞬时值，如 A向电流
         */
        INSTANT,

        /**
         * 期间值，如 天用电量
         */
        PERIOD,

        /**
         * 持续量，如单价
         */
        CONTINUOUS;

        @DbEnumValue
        public String dbValue() {
            return this.name();
        }

    }

    public enum DataType {
        /**
         * 2状态类：如 开关
         */
        BOOLEAN,

        /**
         * 所有数值
         */
        DOUBLE,

        /**
         * 其他所有
         */
        STRING;

        @DbEnumValue
        public String dbValue() {
            return this.name();
        }
    }
}
