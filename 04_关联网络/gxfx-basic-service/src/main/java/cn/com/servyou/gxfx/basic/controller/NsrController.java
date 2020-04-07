package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.model.Nsr;
import cn.com.servyou.gxfx.basic.service.NsrService;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author lpp
 * 2018-11-12
 */
@Slf4j
@Api("��˰��")
@RestController
@RequestMapping("/gxfx/nsr")
public class NsrController {
    private NsrService nsrService;

    @Autowired
    public NsrController(NsrService nsrService) {
        this.nsrService = nsrService;
    }

    @RequestMapping(value = "/search.spring", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "������˰��")
    public ResponseResult<List<Nsr>> search(Nsr.SearchCondition search) {
        ResponseResult<List<Nsr>> result;
        try {
            result = RestResultGenerator.genResult(nsrService.search(search));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = RestResultGenerator.genResult("����ʧ��");
        }
        return result;
    }

    @ApiOperation(value = "��ȡ��ҵ�Ǽ���Ϣ")
    @ApiImplicitParam(name = "companyCode", value = "��˰�˵��ӵ���", required = true, dataType = "String")
    @RequestMapping(value = "/qydj.spring", method = RequestMethod.GET)
    public ResponseResult<Nsr> qydj(@ApiParam(value = "��˰�˵��ӵ���", required = true)
                                    @RequestParam String companyCode) {
        try {
            return RestResultGenerator.genResult(nsrService.getNsr(companyCode), "�����ɹ�");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }
}
