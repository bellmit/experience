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
     * ͼId
     */
    @ApiModelProperty("ͼID")
    private String graphId;
    /**
     * ����˵��
     */
    @ApiModelProperty(required = true, value = "ͼ����")
    private String graphName;
    /**
     * ������ID
     */
    private String createUserId;
    /**
     * ������
     */
    private String createUserName;
    /**
     * ��������
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * ��ҵ���
     */
    private String centralCompanyCode;
    /**
     * ��ҵ����
     */
    private String centralCompanyName;
    /**
     * �޸�ʱ��
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;
    /**
     * ͼ����
     */
    private String graphData;

    public enum OrderBy {
        /**
         * ʱ��
         */
        sj("MODIFY_TIME DESC"),
        /**
         * ����
         */
        mc("GRAPH_NAME ASC");

        @Getter
        private final String value;

        OrderBy(String value) {
            this.value = value;
        }
    }
}
