package cn.com.servyou.gxfx.basic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author lpp
 * 2018-08-27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QyTop {
    private List<HwJe> jxTop;
    private List<HwJe> xxTop;
}
