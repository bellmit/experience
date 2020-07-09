package data.controller;


import data.domain.DataEntity;
import data.service.EntityService;
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
@Api(tags = "实体管理")
@RestController
@RequestMapping("/data/entity")
public class EntityController {

    private EntityService service;

    @Autowired
    public EntityController(EntityService service) {
        this.service = service;
    }

    @ApiOperation("查询实体列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PagedResultList<DataEntity> list(@RequestBody SimpleQuery query) {
        return service.find(query);
    }

    @ApiOperation("保存实体")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody DataEntity entity) {
        service.insertOrUpdate(entity);
    }

    @ApiOperation("删除实体")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(@ApiParam(value = "实体ID", required = true) @RequestParam String entityId) {
        service.deleteById(entityId);
    }

    @ApiOperation("根据实体ID查询实体")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public DataEntity findById(@ApiParam(value = "实体ID", required = true)
                               @RequestParam String entityId) {
        return service.findByIdNullable(entityId);
    }

    @ApiOperation("根据客户ID查找实体")
    @RequestMapping(value = "/findByCustomerId", method = RequestMethod.GET)
    public List<DataEntity> findByCustomerId(@ApiParam(value = "客户ID", required = true)
                                             @RequestParam String customerId) {
        return service.findByCustomerId(customerId);
    }

    @ApiOperation("查询所有的客户ID")
    @RequestMapping(value = "/findAllCustomerIds", method = RequestMethod.GET)
    public List<String> findAllCustomerIds() {
        return service.findAllCustomerIds();
    }
}
