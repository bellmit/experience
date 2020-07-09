package data.dto;


import data.domain.DataQuality;
import period.util.DateTimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("时间值")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "time")
@NoArgsConstructor
@AllArgsConstructor
public class TimeValue implements Serializable {
    @ApiModelProperty("时间")
    protected LocalDateTime time;
    @ApiModelProperty("值")
    protected Object value;
    @ApiModelProperty("质量码")
    protected int quality = DataQuality.GOOD;

    public TimeValue(LocalDateTime time, Object value) {
        this.time = time;
        if (value instanceof String && String.valueOf(value).contains("^_^")) {
            stringValueWithQuality(String.valueOf(value));
        } else {
            this.value = value;
        }
    }

    @NonNull
    public Boolean booleanValue() {
        return Boolean.valueOf(stringValue());
    }

    @NonNull
    public Double doubleValue() {
        return Double.valueOf(stringValue());
    }

    @NonNull
    public String stringValue() {
        return String.valueOf(value);
    }

    public boolean isGood() {
        return DataQuality.GOOD == this.quality;
    }

    public String stringValueWithQuality() {
        return String.format("%s^_^%d", stringValue(), quality);
    }

    public void stringValueWithQuality(String value) {
        String[] split = value.split("\\^_\\^");
        this.value = split[0];
        this.quality = Short.valueOf(split[1]);
    }

    public long timestamp() {
        return DateTimeUtil.toTimestamp(this.time);
    }
}
