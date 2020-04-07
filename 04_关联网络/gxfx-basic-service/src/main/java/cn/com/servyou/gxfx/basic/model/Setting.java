package cn.com.servyou.gxfx.basic.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lpp
 * 2018-11-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
    private String userId;
    private String itemCode;
    private String itemName;
    private String itemValue;

    public static Map<String, String> toMap(List<Setting> list) {
        Map<String, String> result = Maps.newHashMap();

        for (Setting s : list) {
            result.put(s.itemCode, s.itemValue);
        }
        return result;
    }

    public static Collection<Setting> toList(final String userId, Map<String, String> settings) {
        return Collections2.transform(settings.entrySet(), new Function<Map.Entry<String, String>, Setting>() {
            @Override
            public Setting apply(Map.Entry<String, String> entry) {
                String code = entry.getKey();
                String value = entry.getValue();
                return new Setting(userId, code, code, value);
            }
        });
    }
}
