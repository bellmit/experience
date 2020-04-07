package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.service.RzService;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-12-03
 */
@Slf4j
@RestController
@RequestMapping("/gxfx")
public class RzController {
    private final RzService service;

    @Autowired
    public RzController(RzService service) {
        this.service = service;
    }

    @RequestMapping(value = "/allWorkType.spring", method = RequestMethod.GET)
    @ApiOperation(value = "查询所有任职关系列表")
    public ResponseResult<List<Map<String, String>>> getAllWorkType() {
        try {
            return RestResultGenerator.genResult(service.getAllWorkType(), "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult("操作失败");
        }
    }
}