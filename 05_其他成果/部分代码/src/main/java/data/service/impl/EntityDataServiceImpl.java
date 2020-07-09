package data.service.impl;

import com.google.common.collect.Maps;
import data.domain.DataEntity;
import data.domain.DataPoint;
import data.dto.TimeValue;
import data.repository.InfluxRepository;
import data.service.EntityDataService;
import data.service.EntityService;
import data.service.PointDataService;
import data.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import period.dto.PeriodType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static data.repository.InfluxRepository.ARCHIVE_DATA;
import static data.repository.InfluxRepository.VALID_DATA;

@Slf4j
@Service
@Component
public class EntityDataServiceImpl implements EntityDataService {
    private InfluxRepository repository;
    private PointDataService pointDataService;
    private EntityService entityService;
    private PointService pointService;

    @Autowired
    public EntityDataServiceImpl(InfluxRepository repository, PointDataService pointDataService, EntityService entityService, PointService pointService) {
        this.repository = repository;
        this.pointDataService = pointDataService;
        this.entityService = entityService;
        this.pointService = pointService;
    }

    @Override
    public void write(String entityId, PeriodType periodType, LocalDateTime time, Map<String, ?> paramValues) {
        if (!paramValues.isEmpty()) {
            repository.writeBatch(ARCHIVE_DATA, entityId, periodType, time, paramValues);
        }
    }

    @Override
    public Map<String, Double> readArchive(String entityId, PeriodType periodType, LocalDateTime time, Collection<String> params) {
        if (!params.isEmpty()) {
            return repository.read(ARCHIVE_DATA, entityId, periodType, time, params);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Double> readCalc(String entityId, PeriodType periodType, LocalDateTime time, Collection<String> params) {
        if (!params.isEmpty()) {
            Map<String, Double> result = Maps.newHashMap();
            for (String param : params) {
                TimeValue timeValue = pointDataService.read(DataPoint.id(entityId, param, periodType), time)
                        .orElseThrow(() -> new RuntimeException(String.format("readCalc error, entityId: %s, periodType: %s, time: %s, param: %s", entityId, periodType, time, param)));
                result.put(param, timeValue.doubleValue());
            }
            return result;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Double> readArchive(String entityId, PeriodType periodType, LocalDateTime time) {
        DataEntity entity = entityService.findByIdNonNull(entityId);

        List<String> params = entity.getPoints().stream()
                .filter(p -> periodType.equals(p.getPeriodType()))
                .map(p -> p.getParameter().getId())
                .collect(Collectors.toList());

        if (!params.isEmpty()) {
            return readArchive(entityId, periodType, time, params);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Double> readCalc(String entityId, PeriodType periodType, LocalDateTime time) {
        List<String> params = pointService.findByEntityId(entityId).stream()
                .filter(p -> periodType.equals(p.getPeriodType()))
                .map(p -> p.getParameter().getId())
                .collect(Collectors.toList());

        if (!params.isEmpty()) {
            return readCalc(entityId, periodType, time, params);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public void archive(PeriodType periodType, LocalDateTime time) {
        List<String> customerIds = entityService.findAllCustomerIds();

        for (String customerId : customerIds) {
            List<DataEntity> entities = entityService.findByCustomerId(customerId);

            for (DataEntity entity : entities) {
                try {
                    Map<String, Double> values = readCalc(entity.getId(), periodType, time);
                    write(entity.getId(), periodType, time, values);
                } catch (RuntimeException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public void deleteData(String measurement, String entityId) {
        repository.delete(measurement, entityId);
    }
}
