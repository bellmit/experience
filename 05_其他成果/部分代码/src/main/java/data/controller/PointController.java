package data.controller;

import data.domain.DataPoint;
import data.service.PointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.grape.PagedResultList;
import org.grape.SimpleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "数据点管理")
@RestController
@RequestMapping("/data/point")
public class PointController {

    private PointService service;

    @Autowired
    public PointController(PointService service) {
        this.service = service;
    }

    @ApiOperation("查询数据点列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PagedResultList<DataPoint> list(@RequestBody SimpleQuery query) {
        return service.find(query);
    }

    @ApiOperation("保存数据点")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody DataPoint point) {
        service.insertOrUpdate(point);
    }

    @ApiOperation("删除数据点")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(@ApiParam(value = "数据点ID", required = true) @RequestParam String pointId) {
        service.deleteById(pointId);
    }

    @ApiOperation("根据数据点ID查询数据点")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public DataPoint findById(@ApiParam(value = "数据点ID", required = true)
                              @RequestParam String pointId) {
        return service.findByIdNullable(pointId);
    }

    @ApiOperation("根据实体ID查找数据点")
    @RequestMapping(value = "/findByEntityId", method = RequestMethod.GET)
    public List<DataPoint> findByEntityId(@ApiParam(value = "实体ID", required = true)
                                          @RequestParam String entityId) {
        return service.findByEntityId(entityId);
    }
}
