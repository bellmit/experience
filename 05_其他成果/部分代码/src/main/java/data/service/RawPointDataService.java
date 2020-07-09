package data.service;

import data.domain.DataPoint;
import data.dto.PointValue;
import data.dto.TimeValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RawPointDataService {
    void writeValid(DataPoint dataPoint, PointValue pointValue);

    void writeInvalid(DataPoint dataPoint, PointValue pointValue);

    void writeRevise(DataPoint dataPoint, PointValue pointValue);

    void writeOffset(DataPoint dataPoint, PointValue pointValue);

    Optional<TimeValue> sampleValid(DataPoint dataPoint, LocalDateTime time);

    Optional<TimeValue> sampleInvalid(DataPoint dataPoint, LocalDateTime time);

    Optional<TimeValue> readValid(DataPoint dataPoint, LocalDateTime time);

    List<TimeValue> readValid(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end);

    List<TimeValue> readInvalid(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end);

    List<TimeValue> readArchive(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end);

    List<TimeValue> readOffset(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end);

    List<TimeValue> readRevise(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end);
}
