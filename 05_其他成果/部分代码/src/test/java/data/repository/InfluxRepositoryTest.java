package data.repository;

import com.google.common.collect.Maps;
import data.dto.TimeValue;
import org.assertj.core.util.Lists;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import period.dto.PeriodType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * InfluxRepository Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
public class InfluxRepositoryTest {
    private final InfluxDB db = InfluxDBFactory.connect("http://47.98.102.137:18086", "root", "root");
    private InfluxRepository repository;

    @Before
    public void before() throws Exception {
        db.setDatabase("data");
        repository = new InfluxRepository(db);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: writeDouble(String entityId, String parameterId, PeriodType periodType, LocalDateTime time, Double doubleValue)
     */
    @Test
    public void testWriteDouble() throws Exception {
        repository.writeDouble(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId", LocalDateTime.now(), 3.1415926);
        repository.writeDouble(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId1", LocalDateTime.now(), 3.1415926);
        repository.writeDouble(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId2", LocalDateTime.now(), 3.1415926);
        repository.writeDouble(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId3", LocalDateTime.now(), 3.1415926);
        repository.writeDouble(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId4", LocalDateTime.now(), 3.1415926);
    }

    /**
     * Method: writeDouble(String entityId, String parameterId, PeriodType periodType, LocalDateTime time, Double doubleValue)
     */
    @Test
    public void testWriteBatch() throws Exception {
        HashMap<String, Double> params = Maps.newHashMap();
        params.put("param1", 1.0);
        params.put("param2", 2.0);
        params.put("param3", 3.0);
        LocalDateTime time = LocalDate.now().atStartOfDay();
        repository.writeBatch(InfluxRepository.ARCHIVE_DATA, "entityId", PeriodType.DAY, time, params);

        Map<String, Double> values = repository.read(InfluxRepository.ARCHIVE_DATA, "entityId", PeriodType.DAY, time, Lists.newArrayList("param1", "param2", "param3"));

        System.out.println(values);
    }

    /**
     * Method: writeBoolean(String entityId, String parameterId, PeriodType periodType, LocalDateTime time, Boolean booleanValue)
     */
    @Test
    public void testWriteBoolean() throws Exception {
    }

    /**
     * Method: writeString(String entityId, String parameterId, PeriodType periodType, LocalDateTime time, String stringValue)
     */
    @Test
    public void testWriteString() throws Exception {
        repository.writeString(InfluxRepository.EXPRESSION, "testEntityId", PeriodType.INSTANT, "testParameterId", LocalDateTime.of(2000, 1, 1, 0, 0, 0), null);
    }

    @Test
    public void testDelete() throws Exception {
        Optional<TimeValue> optional = repository.read(InfluxRepository.EXPRESSION, "testEntityId", PeriodType.INSTANT, "testParameterId", LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        if (optional.isPresent()) {
            repository.delete(InfluxRepository.EXPRESSION, "testEntityId", PeriodType.INSTANT, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
            Optional<TimeValue> o = repository.read(InfluxRepository.EXPRESSION, "testEntityId", PeriodType.INSTANT, "testParameterId", LocalDateTime.of(2000, 1, 1, 0, 0, 0));
            if (o.isPresent()) {
                System.out.println("delete fail");
            } else {
                System.out.println("delete success");
            }
        } else {
            System.out.println("data not exist.");
        }
    }

    /**
     * Method: sample(String entityId, PeriodType periodType, String parameterId, LocalDateTime time)
     */
    @Test
    public void testSample() throws Exception {
        Optional<TimeValue> sample = repository.sample(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId", LocalDateTime.now());

        System.out.println(sample.get());

    }

    /**
     * Method: read(String entityId, PeriodType periodType, String parameterId, LocalDateTime time)
     */
    @Test
    public void testReadForEntityIdPeriodTypeParameterIdTime() throws Exception {
        Optional<TimeValue> sample = repository.read(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId", LocalDateTime.of(2019, 7, 17, 16, 21, 56));

        System.out.println(sample.get());
    }

    /**
     * Method: read(String entityId, PeriodType periodType, String parameterId, LocalDateTime begin, LocalDateTime end)
     */
    @Test
    public void testReadForEntityIdPeriodTypeParameterIdBeginEnd() throws Exception {
        List<TimeValue> list = repository.read(InfluxRepository.VALID_DATA, "entityId", PeriodType.INSTANT, "parameterId", LocalDateTime.of(2019, 7, 17, 0, 0, 0), LocalDateTime.of(2019, 7, 18, 0, 0, 0));
        System.out.println(list);
    }

} 
