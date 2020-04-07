package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.model.FpDataParams;
import cn.com.servyou.gxfx.basic.model.HwJe;
import cn.com.servyou.gxfx.basic.model.QyTop;
import cn.com.servyou.gxfx.basic.service.FphwService;
import cn.com.servyou.gxfx.model.Fp;
import cn.com.servyou.gxfx.model.Fplx;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author lpp
 * 2018-08-08
 */
@Slf4j
@Api("����Top")
@RestController
@RequestMapping("/gxfx")
public class HwTopController {
    private final FphwService service;

    @Autowired
    public HwTopController(@NonNull FphwService service) {
        this.service = service;
    }

    @ApiOperation("��ҵTop")
    @RequestMapping(value = "/qyTop.spring", produces = "application/json; charset=utf-8", method = RequestMethod.GET)
    public QyTop qyTop(@ApiParam(value = "��˰�˵��ӵ�����", required = true)
                       @RequestParam String companyCode,
                       @ApiParam(value = "Topֵ", required = true)
                       @RequestParam int top,
                       @ApiParam(value = "��ʼ�·�", required = true, example = "201801")
                       @RequestParam String begin,
                       @ApiParam(value = "�����·�", required = true, example = "201812")
                       @RequestParam String end,
                       @ApiParam(value = "��Ʊ����", allowableValues = "zp, pp, all", defaultValue = "zp")
                       @RequestParam(required = false, defaultValue = "zp") Fplx fplx) {
        try {
            QyTop qyTop = service.qyTop(companyCode, top, new FpDataParams(fplx, Fp.monthOf(begin), Fp.monthOf(end)));
            HwJe.transformJe2Wy(qyTop.getJxTop());
            HwJe.transformJe2Wy(qyTop.getXxTop());
            return qyTop;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return new QyTop(Collections.<HwJe>emptyList(), Collections.<HwJe>emptyList());
        }
    }

    @ApiOperation("����Top")
    @RequestMapping(value = "/jyTop.spring", produces = "application/json; charset=utf-8", method = RequestMethod.GET)
    public List<HwJe> jyTop(@ApiParam(value = "������˰�˵��ӵ�����", required = true)
                            @RequestParam String xf,
                            @ApiParam(value = "������˰�˵��ӵ�����", required = true)
                            @RequestParam String gf,
                            @ApiParam(value = "Topֵ", required = true)
                            @RequestParam int top,
                            @ApiParam(value = "��ʼ�·�", required = true, example = "201801")
                            @RequestParam String begin,
                            @ApiParam(value = "�����·�", required = true, example = "201812")
                            @RequestParam String end,
                            @ApiParam(value = "��Ʊ����", allowableValues = "zp, pp, all", defaultValue = "zp")
                            @RequestParam(required = false, defaultValue = "zp") Fplx fplx) {
        try {
            List<HwJe> hwJes = service.jyTop(xf, gf, top, new FpDataParams(fplx, Fp.monthOf(begin), Fp.monthOf(end)));
            HwJe.transformJe2Wy(hwJes);
            return hwJes;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
