package cn.com.servyou.gxfx.basic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lpp
 * 2018-11-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySign {
    private String companyCode;
    private String signId;
    private String signName;
    private String comment;
    private String createUserId;
    private String createUserName;
    private String createTime;
}
