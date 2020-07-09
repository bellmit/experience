package data.controller;

import base.common.response.Response;
import base.common.response.SuccessResponse;
import data.domain.DataCalculateExpression;
import data.service.CalculateExpressionService;
import data.vo.CalculateVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "公式管理")
@RestController
@RequestMapping("/data/expression/selfDefined")
@CrossOrigin
public class CalculateExpressionController {

    private CalculateExpressionService service;

    @Autowired
    public CalculateExpressionController(CalculateExpressionService service){
        this.service = service;
    }

    @ApiOperation("保存公式")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response save(@RequestBody DataCalculateExpression expression) {
        expression.setName(expression.getPointId()+"-"+expression.getId());
        service.insertOrUpdateByIdIsNull(expression);
        return new SuccessResponse("/data/expression/selfDefined/save");
    }

    @ApiOperation("删除公式")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response delete(@RequestBody CalculateVo vo) {
        service.deleteById(vo.getId());
        return new SuccessResponse("/data/expression/selfDefined/delete");
    }

    @ApiOperation("根据实体ID、参数ID、点数据ID查询公式")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public Response queryList(@RequestBody CalculateVo vo){
        List<DataCalculateExpression> list = service.findByExpressions(vo.getEntityId(),vo.getParameterId(),vo.getPointId());
        return new SuccessResponse("/data/expression/selfDefined/queryList",list,list.size());

    }

    @ApiOperation("根据点数据ID查询实体计算公式")
    @RequestMapping(value = "/queryByList", method = RequestMethod.POST)
    public Response queryByList(@RequestBody CalculateVo vo) {
        List<DataCalculateExpression> list = service.findByExpression(vo.getPointId());
        return new SuccessResponse("/data/expression/selfDefined/queryByList",list,list.size());
    }

}
