package com.excenergy.tagdataserv;

import java.io.IOException;
import java.util.Properties;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-21
 */
public class TagDataException extends RuntimeException {
    private static Properties errorCode = new Properties();

    private String code;

    public TagDataException(String code, String message) {
        super(message);
        this.code = code;
    }

    public TagDataException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public synchronized static String getMsg(String code) {
        if (errorCode.isEmpty()) {
            try {
                errorCode.load(TagDataException.class.getClassLoader().getResourceAsStream("errorcode.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return errorCode.getProperty(code);
    }

    @Override
    public String toString() {
        return String.format("Code:%s: %s", code, super.getMessage());
    }
}
