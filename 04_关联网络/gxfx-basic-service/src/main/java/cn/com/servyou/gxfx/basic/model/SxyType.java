package cn.com.servyou.gxfx.basic.model;

/**
 * @author lpp
 * 2018-11-15
 */
public enum SxyType {
    /**
     * ����
     */
    sy("inbound"),
    /**
     * ����
     */
    xy("outbound"),
    /**
     * ������
     */
    sxy("any");

    private final String bound;

    SxyType(String bound) {
        this.bound = bound;
    }

    public String bound() {
        return bound;
    }
}
