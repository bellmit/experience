package data.service.impl;

import data.domain.DataPoint;
import data.dto.PointValue;
import data.dto.TimeValue;
import data.repository.InfluxRepository;
import data.service.RawPointDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static data.repository.InfluxRepository.*;

@Component
public class RawPointDataServiceImpl implements RawPointDataService {
    private InfluxRepository repository;

    @Autowired
    public RawPointDataServiceImpl(InfluxRepository repository) {
        this.repository = repository;
    }

    @Override
    public void writeValid(DataPoint dataPoint, PointValue pointValue) {
        switch (dataPoint.getParameter().getDataType()) {
            case DOUBLE:
                repository.writeDouble(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.doubleValue());
                break;
            case BOOLEAN:
                repository.writeBoolean(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.booleanValue());
                break;
            case STRING:
            default:
                repository.writeString(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.stringValue());
        }
    }

    @Override
    public void writeInvalid(DataPoint dataPoint, PointValue pointValue) {
        repository.writeString(INVALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.stringValueWithQuality());
    }

    @Override
    public void writeRevise(DataPoint dataPoint, PointValue pointValue) {
        repository.writeDouble(REVISE_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.doubleValue());
    }

    @Override
    public void writeOffset(DataPoint dataPoint, PointValue pointValue) {
        repository.writeDouble(OFFSET_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), pointValue.getTime(), pointValue.doubleValue());
    }

    @Override
    public Optional<TimeValue> sampleValid(DataPoint dataPoint, LocalDateTime time) {
        return repository.sample(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), time);
    }

    @Override
    public Optional<TimeValue> sampleInvalid(DataPoint dataPoint, LocalDateTime time) {
        return repository.sample(INVALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), time);
    }

    @Override
    public Optional<TimeValue> readValid(DataPoint dataPoint, LocalDateTime time) {
        return repository.read(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), time);
    }

    @Override
    public List<TimeValue> readValid(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        return repository.read(VALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);
    }

    @Override
    public List<TimeValue> readInvalid(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        return repository.read(INVALID_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);
    }

    @Override
    public List<TimeValue> readArchive(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        return repository.read(ARCHIVE_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);
    }

    @Override
    public List<TimeValue> readOffset(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        return repository.read(OFFSET_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);
    }

    @Override
    public List<TimeValue> readRevise(DataPoint dataPoint, LocalDateTime begin, LocalDateTime end) {
        return repository.read(REVISE_DATA, dataPoint.entityId(), dataPoint.getPeriodType(), dataPoint.parameterId(), begin, end);
    }
}
