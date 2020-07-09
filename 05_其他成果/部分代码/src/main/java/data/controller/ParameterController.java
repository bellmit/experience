package data.controller;

import data.domain.DataParameter;
import data.domain.DataParameterTag;
import data.dto.CsvParameter;
import data.service.ParameterService;
import data.service.ParameterTagService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.csveed.api.CsvClientImpl;
import org.grape.PagedResultList;
import org.grape.SimpleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Api(tags = "参数管理")
@RestController
@RequestMapping("/data/parameter")
public class ParameterController {

    private ParameterService service;
    private ParameterTagService tagService;

    @Autowired
    public ParameterController(ParameterService service, ParameterTagService tagService) {
        this.service = service;
        this.tagService = tagService;
    }

    @ApiOperation("查询参数列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PagedResultList<DataParameter> list(@RequestBody SimpleQuery query) {
        return service.find(query);
    }

    @ApiOperation("保存参数")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody DataParameter parameter) {
        service.insertOrUpdate(parameter);
    }

    @ApiOperation("删除参数")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(@ApiParam(value = "参数ID", required = true)
                       @RequestParam String parameterId) {
        service.deleteById(parameterId);
    }

    @ApiOperation("根据参数ID查询参数")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public DataParameter findById(@ApiParam(value = "参数ID", required = true)
                                  @RequestParam String parameterId) {
        return service.findByIdNullable(parameterId);
    }

    @ApiOperation("参数的标签列表")
    @RequestMapping(value = "/tagList", method = RequestMethod.GET)
    public List<String> tagList(@ApiParam(value = "参数ID", required = true)
                                @RequestParam String parameterId) {
        return service.tagList(parameterId);
    }

    @ApiOperation("增加标签")
    @RequestMapping(value = "/addTag", method = RequestMethod.GET)
    public void addTag(@ApiParam(value = "参数ID", required = true)
                       @RequestParam String parameterId,
                       @ApiParam(value = "标签名称", required = true)
                       @RequestParam String tagName) {
        service.addTag(parameterId, tagName);
    }

    @ApiOperation("删除标签")
    @RequestMapping(value = "/deleteTag", method = RequestMethod.GET)
    public void deleteTag(@ApiParam(value = "参数ID", required = true)
                          @RequestParam String parameterId,
                          @ApiParam(value = "标签名称", required = true)
                          @RequestParam String tagName) {
        service.deleteTag(parameterId, tagName);
    }

    @ApiOperation("所有的参数标签")
    @RequestMapping(value = "/allTags", method = RequestMethod.GET)
    public List<String> allTags() {
        return tagService.all().stream().map(DataParameterTag::getName).collect(Collectors.toList());
    }

    @ApiOperation("根据标签查找参数")
    @RequestMapping(value = "/findByTag", method = RequestMethod.GET)
    public List<DataParameter> findByTag(@ApiParam(value = "标签名称", required = true)
                                         @RequestParam String tagName) {
        return service.findByTag(tagName);
    }

    @ApiOperation("CSV导入参数")
    @ApiImplicitParams(@ApiImplicitParam(name = "file", value = "文件流对象,接收数组格式", required = true, dataType = "__File"))
    @RequestMapping(value = "/csvImport", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void csvImport(@RequestParam("file") MultipartFile file) {
        try {
            String str = new String(file.getBytes(), StandardCharsets.UTF_8);

            List<CsvParameter> csvParameterList = new CsvClientImpl<>(new StringReader(str), CsvParameter.class).readBeans();

            service.csvImport(csvParameterList);
        } catch (IOException e) {
            log.error("csvImport error", e);
        }
    }

    @ApiOperation("CSV导出参数")
    @RequestMapping(value = "/csvExport", method = RequestMethod.GET)
    public void csvExport(HttpServletResponse response) {
        try {
            List<CsvParameter> csvParameterList = service.csvExport();

            response.setHeader("Content-Disposition", "attachment; filename=\"parameter.csv\"");
            response.setContentType("text/csv; charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();


            StringWriter writer = new StringWriter();
            new CsvClientImpl<>(writer, CsvParameter.class).writeBeans(csvParameterList);
            outputStream.write(writer.toString().getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (IOException e) {
            log.error("csvExport error", e);
        }
    }
}
