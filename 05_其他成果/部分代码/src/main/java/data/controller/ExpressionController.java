package data.controller;

import data.dto.DataCalculateExpression;
import data.service.ExpressionService;
import period.util.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Slf4j
@Api(tags = "公式管理")
@RestController
@RequestMapping("/data/expression")
public class ExpressionController {

    private ExpressionService service;

    @Autowired
    public ExpressionController(ExpressionService service) {
        this.service = service;
    }

    @ApiOperation("写入，采集或手工录入")
    @RequestMapping(value = "/write", method = RequestMethod.POST)
    public void write(@RequestBody DataCalculateExpression pointValue) {
        service.write(pointValue);
    }


    @ApiOperation("采样，只采有效的")
    @RequestMapping(value = "/sample/{pointId}/{time}", method = RequestMethod.GET)
    public Optional<String> sample(@ApiParam(value = "数据点ID", required = true)
                                   @PathVariable("pointId") String pointId,
                                   @ApiParam(value = "时间", required = true, example = "yyyyMMddHHmmss")
                                   @PathVariable("time") String time) {
        return service.sample(pointId, DateTimeUtil.fromString(time));
    }
}
