package data.controller;

import data.domain.DataQuality;
import data.dto.CsvQuality;
import data.service.QualityService;
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

@Slf4j
@Api(tags = "质量码管理")
@RestController
@RequestMapping("/data/quality")
public class QualityController {

    private QualityService service;

    @Autowired
    public QualityController(QualityService service) {
        this.service = service;
    }

    @ApiOperation("查询质量码列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PagedResultList<DataQuality> list(@RequestBody SimpleQuery query) {
        return service.find(query);
    }

    @ApiOperation("grape-collector同步质量码方法")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public List<DataQuality> list() {
        return service.queryList();
    }



    @ApiOperation("保存质量码")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody DataQuality quality) {
        service.insertOrUpdate(quality);
    }

    @ApiOperation("删除质量码")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(@ApiParam(value = "质量码ID", required = true) @RequestParam String qualityId) {
        service.deleteById(qualityId);
    }

    @ApiOperation("根据ID查询质量码")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public DataQuality findById(@ApiParam(value = "质量码ID", required = true) @RequestParam String qualityId) {
        return service.findByIdNullable(qualityId);
    }

    @ApiOperation("CSV导入质量码")
    @ApiImplicitParams(@ApiImplicitParam(name = "file", value = "文件流对象,接收数组格式", required = true, dataType = "__File"))
    @RequestMapping(value = "/csvImport", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void csvImport(@RequestParam("file") MultipartFile file) {
        try {
            String str = new String(file.getBytes(), StandardCharsets.UTF_8);

            List<CsvQuality> qualityList = new CsvClientImpl<>(new StringReader(str), CsvQuality.class).readBeans();

            service.csvImport(qualityList);
        } catch (IOException e) {
            log.error("csvImport error", e);
        }
    }

    @ApiOperation("CSV导出质量码")
    @RequestMapping(value = "/csvExport", method = RequestMethod.GET)
    public void csvExport(HttpServletResponse response) {
        try {
            List<CsvQuality> qualityList = service.csvExport();

            response.setHeader("Content-Disposition", "attachment; filename=\"quality.csv\"");
            response.setContentType("text/csv; charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();

            StringWriter writer = new StringWriter();
            new CsvClientImpl<>(writer, CsvQuality.class).writeBeans(qualityList);
            outputStream.write(writer.toString().getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (IOException e) {
            log.error("csvExport error", e);
        }
    }
}
