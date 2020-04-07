package cn.com.servyou.gxfx.basic.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 企业申报返回信息
 *
 * @author wucq
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QySbResponseEntity {
    private ZzsEntity zzs;

    private XfsEntity xfs;

    private SdsEntity qysds;
}
