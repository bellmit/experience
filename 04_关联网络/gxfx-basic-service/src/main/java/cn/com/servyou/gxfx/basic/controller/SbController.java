package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.service.SbService;
import cn.com.servyou.gxfx.basic.view.QySbResponseEntity;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lpp
 * 2018-12-07
 */
@Slf4j
@RestController
@RequestMapping("/gxfx")
public class SbController {

    private SbService service;

    @Autowired
    public SbController(SbService service) {
        this.service = service;
    }

    @ApiOperation(value = "��ȡ��ҵ�걨��Ϣ")
    @RequestMapping(value = "/qysb.spring", method = RequestMethod.GET)
    public ResponseResult<QySbResponseEntity> qysb(@ApiParam(value = "��˰�˵��ӵ�����", required = true)
                                                   @RequestParam String companyCode,
                                                   @ApiParam(value = "��ʼʱ��", required = true)
                                                   @RequestParam String begin,
                                                   @ApiParam(value = "����ʱ��", required = true)
                                                   @RequestParam String end) {
        try {
            return RestResultGenerator.genResult(service.getQySb(companyCode, begin, end), "�����ɹ�");
        } catch (RuntimeException e) {
            log.error("read sb error.", e);
            return RestResultGenerator.genResult(e.getMessage());
        }
    }
}
