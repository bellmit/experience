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
@Api("人工标识")
@RestController
@RequestMapping("/gxfx")
public class SignController {

    @Resource
    private SignService service;


    @ApiOperation(value = "查询系统默认标识")
    @RequestMapping(value = "/allResetSign.spring", method = RequestMethod.GET)
    public ResponseResult<List<Sign>> allResetSign() {
        try {
            return RestResultGenerator.genResult(service.getAllPreSetSign(), "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "查询企业标识")
    @RequestMapping(value = "/allCompanySign.spring", method = RequestMethod.GET)
    public ResponseResult<List<CompanySign>> allCompanySign(@ApiParam(value = "纳税人电子档案号", required = true)
                                                            @RequestParam String companyCode) {
        try {
            return RestResultGenerator.genResult(service.getCompanySign(companyCode), "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/deleteCompanySign.spring", method = RequestMethod.POST)
    @ApiOperation(value = "删除企业标识")
    public ResponseResult<Boolean> deleteCompanySign(@ApiParam(value = "纳税人电子档案号", required = true)
                                                     @RequestParam String companyCode,
                                                     @ApiParam(value = "标识ID", required = true)
                                                     @RequestParam String signId) {
        try {
            service.deleteCompanySign(companyCode, signId);
            return RestResultGenerator.genResult(true, "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "增加企业标识")
    @RequestMapping(value = "/addCompanySign.spring", method = RequestMethod.POST)
    public ResponseResult<Boolean> addCompanySign(HttpServletRequest request,
                                                  @ApiParam(value = "标识ID")
                                                  @RequestParam(required = false) String signId,
                                                  @ApiParam(value = "纳税人电子档案号", required = true)
                                                  @RequestParam String companyCode,
                                                  @ApiParam(value = "标识名称", required = true)
                                                  @RequestParam String signName,
                                                  @ApiParam(value = "内容说明")
                                                  @RequestParam(required = false) String comment) {
        try {
            if (service.checkSignName(companyCode, signName)) {
                return RestResultGenerator.genResult(false, "标识名称已存在");
            }
            String userId = request.getSession().getAttribute("current_user_id").toString();
            String userName = request.getSession().getAttribute("current_user_name").toString();
            service.addCompanySign(new CompanySign(companyCode, StringUtils.isNotEmpty(signId) ? signId : UUID.randomUUID().toString(), signName, comment, userId, userName, null));
            return RestResultGenerator.genResult(true, "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }
}
