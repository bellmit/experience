package data.controller;

import data.service.EntityDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import period.dto.PeriodType;
import period.util.DateTimeUtil;

import java.util.Map;

@Slf4j
@Api(tags = "实体数据读写")
@RestController
@RequestMapping("/data/entityData")
public class EntityDataController {

    private EntityDataService service;

    @Autowired
    public EntityDataController(EntityDataService service) {
        this.service = service;
    }

    @ApiOperation("归档")
    @RequestMapping(value = "/archive/{periodType}/{time}", method = RequestMethod.GET)
    public Void archive(@ApiParam(value = "期间类型", required = true)
                        @PathVariable("periodType") PeriodType periodType,
                        @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                        @PathVariable("time") String time) {
        service.archive(periodType, DateTimeUtil.fromString(time));
        return null;
    }

    @ApiOperation("读某一时间点的值")
    @RequestMapping(value = "/readCalc/{entityId}/{periodType}/{time}", method = RequestMethod.GET)
    public Map<String, Double> readCalc(@ApiParam(value = "实体ID", required = true)
                                        @PathVariable("entityId") String entityId,
                                        @ApiParam(value = "期间类型", required = true)
                                        @PathVariable("periodType") PeriodType periodType,
                                        @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                                        @PathVariable("time") String time) {
        return service.readCalc(entityId, periodType, DateTimeUtil.fromString(time));
    }

    @ApiOperation("删除一段时间的数据")
    @RequestMapping(value = "/deleteData/{measurement}/{entityId}", method = RequestMethod.GET)
    public void deleteData(@ApiParam(value = "数据类别", required = true)
                           @PathVariable("measurement") String measurement,
                           @ApiParam(value = "实体ID", required = true)
                           @PathVariable("entityId") String entityId) {
        service.deleteData(measurement, entityId);
    }
}
