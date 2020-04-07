package com.excenergy.tagdataserv;

import com.alibaba.fastjson.annotation.JSONField;
import com.excenergy.emq.Msg;

import java.util.Date;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2014-07-21
 */
public class EtsdbMsg extends Msg {
    public static final String TYPE_POWER_OFF = "power-off";
    public static final String TYPE_OFFLINE = "offline";
    public static final String TYPE_QUALITY_ERROR = "quality-error";
    public static final String TYPE_VALUE_ERROR = "value-error";
    public static final String TOPIC = "EtsdbMsg";

    /**
     * 采集器编码
     */
    private String entity;

    /**
     * 消息类型
     */
    private String type;

    private String value;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date time;

    public EtsdbMsg() {
        super(EtsdbMsg.class.getName());
    }

    public EtsdbMsg(String entity, String type, String value, Date time) {
        super(TOPIC);
        this.entity = entity;
        this.type = type;
        this.value = value;
        this.time = time;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtsdbMsg etsdbMsg = (EtsdbMsg) o;

        if (entity != null ? !entity.equals(etsdbMsg.entity) : etsdbMsg.entity != null) return false;
        if (time != null ? !time.equals(etsdbMsg.time) : etsdbMsg.time != null) return false;
        if (type != null ? !type.equals(etsdbMsg.type) : etsdbMsg.type != null) return false;
        if (value != null ? !value.equals(etsdbMsg.value) : etsdbMsg.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EtsdbMsg{" +
                "entity='" + entity + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", time=" + time +
                '}';
    }
}