package cn.com.servyou.gxfx.basic.model;

/**
 * @author lpp
 * 2018-11-15
 */
public enum SxyType {
    /**
     * 上游
     */
    sy("inbound"),
    /**
     * 下游
     */
    xy("outbound"),
    /**
     * 上下游
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
