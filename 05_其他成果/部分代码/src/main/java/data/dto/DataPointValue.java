package data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import period.dto.PeriodType;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataPointValue {
    /**
     * tag: entityId
     */
    private String entityId;
    /**
     * tag: periodType
     */
    private PeriodType periodType;

    /**
     * time
     */
    private LocalDateTime time;

    /**
     * field name
     */
    private String parameterId;

    /**
     * field value
     */
    private Object value;


    private short quality;

    public long timestamp() {
        return Timestamp.valueOf(time).getTime();
    }
}
