package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.service.SettingService;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lpp
 * 2018-12-03
 */
@Slf4j
@RestController
@RequestMapping("/gxfx")
public class SettingController {

    private final SettingService settingService;

    @Autowired
    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @ApiOperation(value = "读取配置信息")
    @RequestMapping(value = "/loadDefaultSetting.spring", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> loadDefaultSetting(HttpServletRequest request) {
        String userId = request.getSession().getAttribute("current_user_id").toString();

        Map<String, String> map = settingService.loadSettings(userId);
        if (map.isEmpty()) {
            return new ResponseEntity<Map<String, String>>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "保存用户默认设置")
    @RequestMapping(value = "/saveDefaultSetting.spring", method = RequestMethod.POST)
    public ResponseResult<Boolean> saveDefaultSetting(HttpServletRequest request,
                                                      @ApiParam(value = "设置内容", required = true)
                                                      @RequestParam(name = "setting") String setting) {
        try {
            String userId = request.getSession().getAttribute("current_user_id").toString();
            settingService.saveSettings(userId, JSON.parseObject(setting, new TypeReference<Map<String, String>>() {
            }));
            return RestResultGenerator.genResult(true, "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult("操作失败");
        }
    }
}
