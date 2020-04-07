package cn.com.servyou.gxfx.basic.controller;

import cn.com.servyou.gxfx.basic.model.MyGraph;
import cn.com.servyou.gxfx.basic.service.MyGraphService;
import cn.com.servyou.tdap.web.PagerBean;
import cn.com.servyou.tdap.web.ResponseResult;
import cn.com.servyou.tdap.web.RestResultGenerator;
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

/**
 * @author lpp
 * 2018-12-03
 */
@Slf4j
@RestController
@RequestMapping("/gxfx")
public class MyGraphController {

    @Resource
    private MyGraphService graphService;

    @ApiOperation(value = "查询关系图列表")
    @RequestMapping(value = "/listMyGraph.spring", method = RequestMethod.GET)
    public ResponseResult<List<MyGraph>> listMyGraph(HttpServletRequest request,
                                                     @ApiParam(value = "排序字段", allowableValues = "sj, mc", defaultValue = "sj")
                                                     @RequestParam(value = "orderField", required = false, defaultValue = "sj") String orderField,
                                                     @ApiParam(value = "每页显示条数", defaultValue = "10")
                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                     @ApiParam(value = "当前页码", defaultValue = "1")
                                                     @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex) {
        try {
            String userId = request.getSession().getAttribute("current_user_id").toString();
            PagerBean<MyGraph> result = graphService.getByUserId(userId, MyGraph.OrderBy.valueOf(orderField), pageSize, pageIndex);
            return RestResultGenerator.genResult(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "读取关系图")
    @RequestMapping(value = "/loadMyGraph.spring", method = RequestMethod.GET)
    public ResponseResult<MyGraph> loadMyGraph(@ApiParam(value = "关系图ID", required = true)
                                               @RequestParam String graphId) {
        try {
            return RestResultGenerator.genResult(graphService.get(graphId), "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

    @ApiOperation(value = "删除关系图")
    @RequestMapping(value = "/deleteMyGraph.spring", method = RequestMethod.POST)
    public ResponseResult<Boolean> deleteMyGraph(@ApiParam(value = "关系图ID", required = true)
                                                 @RequestParam String graphId) {
        try {
            graphService.delete(graphId);
            return RestResultGenerator.genResult(true, "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }


    @ApiOperation(value = "保存关系图")
    @RequestMapping(value = "/saveMyGraph.spring", method = RequestMethod.POST)
    public ResponseResult<Boolean> saveMyGraph(HttpServletRequest request, MyGraph graph) {
        try {
            String userId = request.getSession().getAttribute("current_user_id").toString();
            String userName = request.getSession().getAttribute("current_user_name").toString();
            graph.setCreateUserId(userId);
            graph.setCreateUserName(userName);
            graphService.save(graph);
            return RestResultGenerator.genResult(true, "操作成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResultGenerator.genResult(e.getMessage());
        }
    }

}
