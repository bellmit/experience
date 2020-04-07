package cn.com.servyou.gxfx.basic.util;

import com.google.common.base.Charsets;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.lang.StringUtils;

import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过注解配置，导出Excel
 *
 * @author lpp
 * 2018-06-13
 */
public class ExportExcelUtil {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ExcelColumn {
        /**
         * 列的顺序，0,1,2,3...
         *
         * @return 列的顺序
         */
        int index();

        /**
         * 列名，第一行列的名称
         *
         * @return 列名
         */
        String name();

        /**
         * get 方法名, 默认使用 getFieldName
         * <p>
         * 如果get方法名为 isFieldName 或 fieldName 格式，则需要配置
         *
         * @return 列名
         */
        String getMethod() default "";
    }

    private static class ColumnModel {
        private int index;
        private String name;
        private String getMethod;

        public ColumnModel(Field field) {
            ExcelColumn a = field.getAnnotation(ExcelColumn.class);
            this.index = a.index();
            this.name = a.name();
            this.getMethod = StringUtils.isNotBlank(a.getMethod()) ? a.getMethod() : String.format("get%s%s", field.getName().substring(0, 1).toUpperCase(), field.getName().substring(1));
        }
    }

    /**
     * 转Excel
     *
     * @param beanList  对象列表，对象的属性需要加注解
     * @param out       输出流
     * @param sheetName sheet 名称
     * @param cls       类
     * @param <T>       范型
     */
    public static <T> void toExcel(List<T> beanList, OutputStream out, String sheetName, Class<T> cls) {
        WritableWorkbook wwb;
        try {
            wwb = Workbook.createWorkbook(out);

            WritableSheet sheet = wwb.createSheet(sheetName, 0);

            List<ColumnModel> headerNameList = fetchColumnInfo(cls);

            fillHeader(sheet, headerNameList, beanList, cls);

            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> void fillHeader(WritableSheet sheet, List<ColumnModel> modelList, List<T> beanList, Class<T> cls) throws WriteException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int row = 0;

        // header
        WritableCellFormat hFmt = getHeaderFormat();
        for (int i = 0; i < modelList.size(); i++) {
            String cc = modelList.get(i).name;
            sheet.addCell(new Label(i, row, cc, hFmt));
        }
        row++;

        // body
        WritableCellFormat bdFmt = getBodyFormat();
        int[] counts = new int[modelList.size()];
        int[] maxs = new int[modelList.size()];
        for (T b : beanList) {
            for (int i = 0; i < modelList.size(); i++) {
                ColumnModel m = modelList.get(i);
                Object o = cls.getMethod(m.getMethod).invoke(b);
                String cc = String.valueOf(o);
                int length = cc.getBytes(Charsets.UTF_8).length;
                counts[i] += length;
                if (length > maxs[i]) {
                    maxs[i] = length;
                }
                sheet.addCell(new Label(i, row, cc, bdFmt));
            }
            row++;
        }

        // set column width
        for (int i = 0; i < counts.length; i++) {
            int count = counts[i];
            int avg = count / beanList.size();
            int header = modelList.get(i).name.getBytes(Charsets.UTF_8).length;
            int max = maxs[i];
            int width = header > avg ? header : max < avg * 1.3 ? max : (int) (avg * 1.3);
            sheet.setColumnView(i, (int) (width * 1.3));
        }
    }

    private static WritableCellFormat getHeaderFormat() throws WriteException {
        WritableCellFormat fmt = new WritableCellFormat(new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.BOLD));
        fmt.setBackground(Colour.GRAY_25);
        fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
        fmt.setAlignment(Alignment.CENTRE);
        fmt.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        return fmt;
    }

    private static WritableCellFormat getBodyFormat() throws WriteException {
        WritableCellFormat fmt = new WritableCellFormat(new WritableFont(WritableFont.createFont("宋体"), 12));
        fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
        fmt.setAlignment(Alignment.CENTRE);
        fmt.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        return fmt;
    }

    private static <T> List<ColumnModel> fetchColumnInfo(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        List<ColumnModel> list = new ArrayList<ColumnModel>(fields.length);
        for (Field field : fields) {
            ColumnModel m = new ColumnModel(field);
            list.add(m.index, m);
        }
        return list;
    }
}
