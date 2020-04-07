package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.model.CompanySign;
import cn.com.servyou.gxfx.basic.model.Sign;
import cn.com.servyou.gxfx.basic.service.SignService;
import cn.com.servyou.tdap.commons.StringUtils;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @author lpp
 * 2018-12-03
 */
@Slf4j
@Api("�˹���ʶ")
@RestController
@RequestMapping("/gxfx")
public class SignController {

    @Resource
    private SignService service;


    @ApiOperation(value = "��ѯϵͳĬ�ϱ�ʶ")
    @RequestMapping(value = "/allResetSign.spring", method = RequestMethod.GET)
    public ResponseResult<List<Sign>> allResetSign() {
        try {
            return RestResultGenerator.genResult(service.getAllPreSetSign(), "�����ɹ�");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "��ѯ��ҵ��ʶ")
    @RequestMapping(value = "/allCompanySign.spring", method = RequestMethod.GET)
    public ResponseResult<List<CompanySign>> allCompanySign(@ApiParam(value = "��˰�˵��ӵ�����", required = true)
                                                            @RequestParam String companyCode) {
        try {
            return RestResultGenerator.genResult(service.getCompanySign(companyCode), "�����ɹ�");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/deleteCompanySign.spring", method = RequestMethod.POST)
    @ApiOperation(value = "ɾ����ҵ��ʶ")
    public ResponseResult<Boolean> deleteCompanySign(@ApiParam(value = "��˰�˵��ӵ�����", required = true)
                                                     @RequestParam String companyCode,
                                                     @ApiParam(value = "��ʶID", required = true)
                                                     @RequestParam String signId) {
        try {
            service.deleteCompanySign(companyCode, signId);
            return RestResultGenerator.genResult(true, "�����ɹ�");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "������ҵ��ʶ")
    @RequestMapping(value = "/addCompanySign.spring", method = RequestMethod.POST)
    public ResponseResult<Boolean> addCompanySign(HttpServletRequest request,
                                                  @ApiParam(value = "��ʶID")
                                                  @RequestParam(required = false) String signId,
                                                  @ApiParam(value = "��˰�˵��ӵ�����", required = true)
                                                  @RequestParam String companyCode,
                                                  @ApiParam(value = "��ʶ����", required = true)
                                                  @RequestParam String signName,
                                                  @ApiParam(value = "����˵��")
                                                  @RequestParam(required = false) String comment) {
        try {
            if (service.checkSignName(companyCode, signName)) {
                return RestResultGenerator.genResult(false, "��ʶ�����Ѵ���");
            }
            String userId = request.getSession().getAttribute("current_user_id").toString();
            String userName = request.getSession().getAttribute("current_user_name").toString();
            service.addCompanySign(new CompanySign(companyCode, StringUtils.isNotEmpty(signId) ? signId : UUID.randomUUID().toString(), signName, comment, userId, userName, null));
            return RestResultGenerator.genResult(true, "�����ɹ�");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }
}
