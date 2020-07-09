package data.util;


import data.dto.XlsParameter;

import java.math.BigDecimal;
import java.util.List;

public class Parameter {

    public List<XlsParameter> change(List<XlsParameter> parameterImpList){
        for(XlsParameter imp : parameterImpList){
            //处理类型
            if(imp.getType().equals("期间值")){
                imp.setType("PERIOD");
            }else if(imp.getType().equals("瞬时值")){
                imp.setType("INSTANT");
            }else if(imp.getType().equals("持续量")){
                imp.setType("CONTINUOUS");
            }
            //处理数据类型
            if(imp.getDataType().equals("开关")){
                imp.setDataType("BOOLEAN");
            }else if(imp.getDataType().equals("数字")){
                imp.setDataType("DOUBLE");
            }else if(imp.getDataType().equals("字符串")){
                imp.setDataType("STRING");
            }
            //处理期间集合
            if(null != imp.getPeriodSet() && !"".equals(imp.getPeriodSet())) {
                String[] data = imp.getPeriodSet().split("，");
                String pt = "";
                for (int i = 0; i < data.length; i++) {
                    if (data[i].equals("瞬时值")) {
                        data[i] = "INSTANT";
                    } else if (data[i].equals("持续量")) {
                        data[i] = "CONTINUOUS";
                    } else if (data[i].equals("小时")) {
                        data[i] = "HOUR";
                    } else if (data[i].equals("日")) {
                        data[i] = "DAY";
                    } else if (data[i].equals("周")) {
                        data[i] = "WEEK";
                    } else if (data[i].equals("月")) {
                        data[i] = "MONTH";
                    } else if (data[i].equals("季")) {
                        data[i] = "SEASON";
                    } else if (data[i].equals("年")) {
                        data[i] = "YEAR";
                    }
                    pt += data[i] + ",";
                }
                imp.setPeriodSet(pt);
            }
            //数据精度
            Integer precision = checkIsDoublePointTwo(imp.getDataPrecision().toString());
            imp.setDataPrecision(precision.toString());
        }
        return parameterImpList;
    }

    /**
     * 判断小数位数
     * @param param
     * @return
     */
    public int checkIsDoublePointTwo(String param) {
        if (param == null) {
            return 0;
        }
        BigDecimal bd = new BigDecimal(param);
        String[] ss = bd.toString().split("\\.");
        if (ss.length <= 1){
            return 0;
        }
        return ss[1].length();
    }
}
