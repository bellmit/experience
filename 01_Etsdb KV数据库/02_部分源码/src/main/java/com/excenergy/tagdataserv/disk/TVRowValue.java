package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-23
 */
public class TVRowValue {
    private static final Logger logger = LoggerFactory.getLogger(TVRowValue.class);
    private byte[] data;
    private Map<Integer, byte[]> valueMap;

    public TVRowValue(byte[] data) {
        if (data.length < 3) {
            String code = "E-000007";
            String msg = String.format(TagDataException.getMsg(code));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        this.data = data.clone();
    }

    public TVRowValue(Map<Integer, byte[]> vMap) {
        valueMap = vMap;
        this.data = TVMapper.encode(vMap);
    }

    public byte[] encode() {
        return data.clone();
    }

    public Map<Integer, byte[]> getValueMap() {
        if (valueMap == null) {
            valueMap = TVMapper.decode(data);
        }
        return valueMap;
    }

    public void merge(TVRowValue rowValue) {
        if (data[0] == rowValue.data[0]) { //  值长度相等或不相等，当值长度相等或不相等时都可以Merge
            data = TVMapper.merge(data, rowValue.data);
        } else {  //  一个值长度相等，另一个值长度不等，这种情况是极小的概率，逻辑上是会出现的
            Map<Integer, byte[]> map = getValueMap();
            map.putAll(rowValue.getValueMap());
            data = TVMapper.encode(map);
        }
    }
}
