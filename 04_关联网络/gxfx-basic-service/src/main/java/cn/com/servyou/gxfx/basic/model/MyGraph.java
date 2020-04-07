package cn.com.servyou.gxfx.basic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author lpp
 * 2018-11-29
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyGraph {

    /**
     * 图Id
     */
    @ApiModelProperty("图ID")
    private String graphId;
    /**
     * 内容说明
     */
    @ApiModelProperty(required = true, value = "图名称")
    private String graphName;
    /**
     * 创建人ID
     */
    private String createUserId;
    /**
     * 创建人
     */
    private String createUserName;
    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 企业编号
     */
    private String centralCompanyCode;
    /**
     * 企业名称
     */
    private String centralCompanyName;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;
    /**
     * 图数据
     */
    private String graphData;

    public enum OrderBy {
        /**
         * 时间
         */
        sj("MODIFY_TIME DESC"),
        /**
         * 名称
         */
        mc("GRAPH_NAME ASC");

        @Getter
        private final String value;

        OrderBy(String value) {
            this.value = value;
        }
    }
}
