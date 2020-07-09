package data.controller;

import data.dto.PointValue;
import data.dto.TimeValue;
import data.service.PointDataService;
import period.util.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Api(tags = "点数据读写")
@RestController
@RequestMapping("/data/pointData")
public class PointDataController {

    private PointDataService service;

    @Autowired
    public PointDataController(PointDataService service) {
        this.service = service;
    }

    @ApiOperation("写入，采集或手工录入")
    @RequestMapping(value = "/write", method = RequestMethod.POST)
    public void write(@RequestBody PointValue pointValue) {
        service.write(pointValue);
    }

    @ApiOperation("修正")
    @RequestMapping(value = "/revise", method = RequestMethod.POST)
    public void revise(@RequestBody PointValue pointValue) {
        service.revise(pointValue);
    }

    @ApiOperation("偏移量，换表时使用，也可以用修正接口实现")
    @RequestMapping(value = "/offset", method = RequestMethod.POST)
    public void offset(@RequestBody PointValue pointValue) {
        service.offset(pointValue);
    }

    @ApiOperation("采样，只采有效的")
    @RequestMapping(value = "/sample/{pointId}/{time}", method = RequestMethod.GET)
    public Optional<TimeValue> sample(@ApiParam(value = "数据点ID", required = true)
                                      @PathVariable("pointId") String pointId,
                                      @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                                      @PathVariable("time") String time) {
        return service.sample(pointId, DateTimeUtil.fromString(time));
    }

    @ApiOperation("间隔采样，只采有效的")
    @RequestMapping(value = "/sample/{pointId}/{begin}/{end}/{interval}", method = RequestMethod.GET)
    public List<TimeValue> sampleInterval(@ApiParam(value = "数据点ID", required = true)
                                          @PathVariable("pointId") String pointId,
                                          @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                          @PathVariable("begin") String begin,
                                          @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                          @PathVariable("end") String end,
                                          @ApiParam(value = "间隔(秒数)", required = true)
                                          @PathVariable("interval") int interval) {
        return service.sample(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end), interval);
    }

    @ApiOperation("采样，只采无效的")
    @RequestMapping(value = "/sampleInvalid/{pointId}/{time}", method = RequestMethod.GET)
    public Optional<TimeValue> sampleInvalid(@ApiParam(value = "数据点ID", required = true)
                                             @PathVariable("pointId") String pointId,
                                             @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                                             @PathVariable("time") String time) {
        return service.sampleInvalid(pointId, DateTimeUtil.fromString(time));
    }

    @ApiOperation("读某一时间点的值")
    @RequestMapping(value = "/read/{pointId}/{time}", method = RequestMethod.GET)
    public Optional<TimeValue> read(@ApiParam(value = "数据点ID", required = true)
                                    @PathVariable("pointId") String pointId,
                                    @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                                    @PathVariable("time") String time) {
        return service.read(pointId, DateTimeUtil.fromString(time));
    }

    @ApiOperation("读一段时间的值")
    @RequestMapping(value = "/read/{pointId}/{begin}/{end}", method = RequestMethod.GET)
    public List<TimeValue> read(@ApiParam(value = "数据点ID", required = true)
                                @PathVariable("pointId") String pointId,
                                @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                @PathVariable("begin") String begin,
                                @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                @PathVariable("end") String end) {
        return service.read(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end));
    }

    @ApiOperation("读一段时间的无效值")
    @RequestMapping(value = "/readInvalid/{pointId}/{begin}/{end}", method = RequestMethod.GET)
    public List<TimeValue> readInvalid(@ApiParam(value = "数据点ID", required = true)
                                       @PathVariable("pointId") String pointId,
                                       @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                       @PathVariable("begin") String begin,
                                       @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                       @PathVariable("end") String end) {
        return service.readInvalid(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end));
    }


    @ApiOperation("读一段时间的归档值")
    @RequestMapping(value = "/readArchive/{pointId}/{begin}/{end}", method = RequestMethod.GET)
    public List<TimeValue> readArchive(@ApiParam(value = "数据点ID", required = true)
                                       @PathVariable("pointId") String pointId,
                                       @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                       @PathVariable("begin") String begin,
                                       @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                       @PathVariable("end") String end) {
        return service.readArchive(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end));
    }


    @ApiOperation("读一段时间的偏移量值")
    @RequestMapping(value = "/readOffset/{pointId}/{begin}/{end}", method = RequestMethod.GET)
    public List<TimeValue> readOffset(@ApiParam(value = "数据点ID", required = true)
                                      @PathVariable("pointId") String pointId,
                                      @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                      @PathVariable("begin") String begin,
                                      @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                      @PathVariable("end") String end) {
        return service.readOffset(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end));
    }

    @ApiOperation("读一段时间的修正值")
    @RequestMapping(value = "/readRevise/{pointId}/{begin}/{end}", method = RequestMethod.GET)
    public List<TimeValue> readRevise(@ApiParam(value = "数据点ID", required = true)
                                      @PathVariable("pointId") String pointId,
                                      @ApiParam(value = "开始时间(包括)", required = true, example = "yyyyMMddHHmmss")
                                      @PathVariable("begin") String begin,
                                      @ApiParam(value = "结束时间(不包括)", required = true, example = "yyyyMMddHHmmss")
                                      @PathVariable("end") String end) {
        return service.readRevise(pointId, DateTimeUtil.fromString(begin), DateTimeUtil.fromString(end));
    }
}
