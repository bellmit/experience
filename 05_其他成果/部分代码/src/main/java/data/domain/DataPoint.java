package data.domain;

import data.service.PointService;
import io.ebean.annotation.DbEnumValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.grape.BaseDomain;
import org.grape.ReferenceHelper;
import period.dto.PeriodType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Optional;

@ApiModel(value = "数据点", parent = BaseDomain.class)
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DataPoint extends BaseDomain {
    @ApiModelProperty(value = "期间类型", required = true)
    @Column(nullable = false)
    private PeriodType periodType;

    @ApiModelProperty("增加修改时忽略，查询时可以关联出实体名称，客户ID")
    @ManyToOne
    private DataEntity entity;

    @ApiModelProperty("增加修改时忽略，查询时可以关联出参数名称等")
    @ManyToOne
    private DataParameter parameter;

    @ApiModelProperty(value = "类型", required = true)
    @Column(nullable = false)
    private Type type;
    @ApiModelProperty("数据精度")
    private Integer dataPrecision;

    public void assertDataTypeIsDouble() {
        if (!DataParameter.DataType.DOUBLE.equals(parameter.getDataType())) {
            throw new RuntimeException(String.format("the parameter data type %s is not double, pointId: %s", parameter.getDataType(), this.id));
        }
    }

    public boolean isRaw() {
        return Type.RAW.equals(this.type);
    }

    public boolean isCalc() {
        return Type.CALC.equals(this.type);
    }

    /**
     * is period and it's instant exist && isRaw
     *
     * @return true if is instant period
     */
    public boolean isInstantPeriod() {
        if (periodType.isPeriod()) {
            PointService service = ReferenceHelper.reference(PointService.class);
            Optional<DataPoint> optional = service.findById(id(entityId(), parameterId(), PeriodType.INSTANT));
            return optional.map(DataPoint::isRaw).orElse(false);
        } else {
            return false;
        }
    }

    public boolean isNotInstantPeriod() {
        return !isInstantPeriod();
    }

    public void assertTypeIsRaw() {
        if (!isRaw()) {
            throw new RuntimeException(String.format("the type %s is not RAW, pointId: %s", this.type.name(), this.id));
        }
    }

    public String entityId() {
        return entity.getId();
    }

    public void  setEntityId(String entityId)
    {
        if(null == this.entity)
        {
            this.entity  = new DataEntity();
        }
        this.entity.setId(entityId);
    }
    public String parameterId() {
        return parameter.getId();
    }

    public void  setParameterId(String parameterId)
    {
        if(null == this.parameter)
        {
            this.parameter  = new DataParameter();
        }
        this.parameter.setId(parameterId);
    }

    private void generateId() {
        if (entity != null && parameter != null && this.periodType != null) {
            String entityId = entityId();
            String parameterId = parameterId();
            PeriodType periodType = this.periodType;
            super.setId(id(entityId, parameterId, periodType));
        }
    }

    public static String id(String entityId, String parameterId, PeriodType periodType) {
        return id(entityId, parameterId, periodType.name());
    }

    public static String fetchEntityIdFromId(String id) {
        return id.split("__")[0];
    }

    public static String fetchParameterIdFromId(String id) {
        return id.split("__")[1];
    }

    public static PeriodType fetchPeriodTypeFromId(String id) {
        return PeriodType.valueOf(id.split("__")[2]);
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
        generateId();
    }

    public void setEntity(DataEntity entity) {
        this.entity = entity;
        generateId();
    }

    public void setParameter(DataParameter parameter) {
        this.parameter = parameter;
        generateId();
    }

    public boolean isInstant() {
        return PeriodType.INSTANT.equals(this.periodType);
    }

    public boolean isContinuous() {
        return PeriodType.CONTINUOUS.equals(this.periodType);
    }

    public boolean isPeriod() {
        return PeriodType.HOUR.equals(this.periodType)
                || PeriodType.DAY.equals(this.periodType)
                || PeriodType.WEEK.equals(this.periodType)
                || PeriodType.MONTH.equals(this.periodType)
                || PeriodType.SEASON.equals(this.periodType)
                || PeriodType.YEAR.equals(this.periodType);
    }

    public enum Type {
        RAW,
        CALC;

        @DbEnumValue
        public String dbValue() {
            return this.name();
        }
    }
}
