package cn.com.servyou.gxfx.basic.service;

import cn.com.servyou.gxfx.basic.view.QySbResponseEntity;

/**
 * @author lpp
 * 2018-12-07
 */
public interface SbService {
    QySbResponseEntity getQySb(String nsrdzdah, String begin, String end);
}
