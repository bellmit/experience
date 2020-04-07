package cn.com.servyou.gxfx.basic.util;

import cn.com.servyou.gxfx.model.RzType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.joda.time.Period;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author lpp
 * 2018-08-07
 */
public class TransformUtil {
    private static final int SFJ_15 = 15;
    private static final int SFJ_18 = 18;
    private static final String GYJT_11 = "11";
    private static final String GYJT_12 = "12";
    private static final String GYJT_151 = "151";
    private static final String GFLY_13 = "13";
    private static final String GFLY_14 = "14";
    private static final String GFLY_16 = "16";
    private static final String QTYXZR = "159";
    private static final String SYQY = "17";
    private static final String QTNZ = "19";
    private static final String WZQY_2 = "2";
    private static final String WZQY_3 = "3";

    private static Map<String, RzType> intEnumMap = ImmutableMap.<String, RzType>builder()
            .put("1", RzType.fr)
            .put("2", RzType.cw)
            .put("3", RzType.bsr)
            .put("4", RzType.gpr)
            .put("5", RzType.tzr)
            .build();

    public static Set<RzType> rzTypes(int controlType) {
        String s = Integer.toBinaryString(controlType);
        char[] chars = new StringBuilder(s).reverse().toString().toCharArray();
        Set<RzType> result = Sets.newHashSet();
        for (int i = 0; i < chars.length; i++) {
            if ('1' == chars[i]) {
                result.add(intEnumMap.get("" + (i + 1)));
            }
        }
        return result;
    }

    public static int rzTypes(Set<RzType> rzTypes) {
        String s = String.format("%s%s%s%s%s", check(rzTypes, RzType.fr), check(rzTypes, RzType.cw), check(rzTypes, RzType.bsr), check(rzTypes, RzType.gpr), check(rzTypes, RzType.tzr));
        return Integer.parseInt(new StringBuilder(s).reverse().toString(), 2);
    }

    private static String check(Set<RzType> rzTypes, RzType fr) {
        return rzTypes.contains(fr) ? "1" : "0";
    }

    public static Integer fetchAgeFromSfz(String idNumber) {
        String dateStr;
        if (idNumber.length() == SFJ_15) {
            dateStr = "19" + idNumber.substring(6, 12);
        } else if (idNumber.length() == SFJ_18) {
            dateStr = idNumber.substring(6, 14);
        } else {//默认是合法身份证号，但不排除有意外发生
            throw new RuntimeException("身份证号码不合法：" + idNumber);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date birthday = simpleDateFormat.parse(dateStr);
            return new Period(birthday.getTime(), System.currentTimeMillis()).getYears();
        } catch (ParseException e) {
            throw new RuntimeException("身份证号码不合法：" + idNumber);
        }
    }

    public static boolean isSmhy(String hydm) {
        return hydm.startsWith("51") || hydm.startsWith("52");
    }

    public static String djzclxdm(String djzclxdm) {
        if (djzclxdm.startsWith(GYJT_11) || djzclxdm.startsWith(GYJT_12) || djzclxdm.startsWith(GYJT_151)) {
            return "110";
        } else if (djzclxdm.startsWith(GFLY_13) || djzclxdm.startsWith(GFLY_14) || djzclxdm.startsWith(GFLY_16)) {
            return "130";
        } else if (djzclxdm.startsWith(QTYXZR)) {
            return "159";
        } else if (djzclxdm.startsWith(SYQY)) {
            return "170";
        } else if (djzclxdm.startsWith(QTNZ)) {
            return "190";
        } else if (djzclxdm.startsWith(WZQY_2) || djzclxdm.startsWith(WZQY_3)) {
            return "200";
        } else {
            return djzclxdm;
        }
    }

    public static String djzclxmc(String djzclxdm, String djzclxmc) {
        if (djzclxdm.startsWith(GYJT_11) || djzclxdm.startsWith(GYJT_12) || djzclxdm.startsWith(GYJT_151)) {
            return "国有集体";
        } else if (djzclxdm.startsWith(GFLY_13) || djzclxdm.startsWith(GFLY_14) || djzclxdm.startsWith(GFLY_16)) {
            return "股份联营";
        } else if (djzclxdm.startsWith(QTYXZR)) {
            return "其他有限责任";
        } else if (djzclxdm.startsWith(SYQY)) {
            return "私营企业";
        } else if (djzclxdm.startsWith(QTNZ)) {
            return "其他内资";
        } else if (djzclxdm.startsWith(WZQY_2) || djzclxdm.startsWith(WZQY_3)) {
            return "外资企业";
        } else {
            return djzclxmc;
        }
    }

    public static double scaleWy(Double je, int newScale) {
        if (je == null) {
            return 0.0;
        } else {
            return new BigDecimal(je / 10000).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public static double scale(Double je, int newScale) {
        if (je == null) {
            return 0.0;
        } else {
            return new BigDecimal(je).setScale(newScale, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
