package com.bitspace.food.util;

/**
 * Created by Administrator on 2018/3/26 0026.
 */
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 转载时请保留以下信息，注明出处！
 *
 * @author leno
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
public class ExportExcelUtil<T>
{
//    public void exportExcel(Collection<T> dataset, OutputStream out)
//    {
//        exportExcel("测试POI导出EXCEL文档", null, dataset, out, "yyyy-MM-dd");
//    }
//
//    public void exportExcel(String[] headers, Collection<T> dataset,
//                            OutputStream out)
//    {
//        exportExcel("测试POI导出EXCEL文档", headers, dataset, out, "yyyy-MM-dd");
//    }
//
//    public void exportExcel(String[] headers, Collection<T> dataset,
//                            OutputStream out, String pattern)
//    {
//        exportExcel2("测试POI导出EXCEL文档", headers, dataset, out, pattern);
//    }


    public void exportMapExcel(String[] headers,Collection<Map<String,Object>> dataSets,OutputStream out,boolean isAddLineNumber){
        exportMapExcel(null,headers,dataSets,out,isAddLineNumber);
    }
    public void exportMapExcel(Map<String,Object> condition,String[] headers,Collection<Map<String,Object>> datas,OutputStream out,boolean isAddLineNumber){
        exportMapExcel("",condition,headers,datas,out,isAddLineNumber,null);
    }

    public void exportMapExcel(String title,Map<String,Object> condition,String[] headers,Collection<Map<String,Object>> datas,OutputStream out,boolean isAddLineNumber,Collection<String> excludes){
        exportMapExcel(title,condition,headers,datas,out,"yyyy-MM-dd",isAddLineNumber,excludes);
    }


    public void exportMapExcel(String title,Map<String,Object> condition, String[] headers, Collection<Map<String,Object>> dataset,OutputStream out,String pattern,boolean isAddLineNumber,Collection<String> excludes){
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(15);
        XSSFCellStyle style  = createHeaderStyle(workbook);

        XSSFCellStyle style2 = createBodyStyle(workbook);

        /**
         * 注释
         */
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        int index = writeCondition(condition,sheet);

        /**
         * 产生标题行
         */
        XSSFRow row = sheet.createRow(index);
        writeHeads(headers,row,style);

        Iterator<Map<String,Object>> it = dataset.iterator();
        int lineNumber = 1;

        while(it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            if(isAddLineNumber) {
                XSSFCell lineNumberCell = row.createCell(0);
                lineNumberCell.setCellStyle(style2);
                lineNumberCell.setCellValue(lineNumber++);
            }
            Map<String, Object> map = it.next();
            int i = 1;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(excludes != null && excludes.contains(entry.getKey())){
                    continue;
                }
                XSSFCell cell = row.createCell(i++);
                cell.setCellStyle(style2);
                Object value = entry.getValue();
                String textValue = getValueStr(value,pattern);
                writeCell(textValue,cell);
            }
        }

        try
        {
            workbook.write(out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private int writeCondition(Map<String,Object> condition,XSSFSheet sheet){
        if(condition==null){
            return 0;
        }

        XSSFRow row = sheet.createRow(0);
        int i = 0;
        for (Map.Entry<String, Object> entry : condition.entrySet()) {

            XSSFCell keyCell = row.createCell(i++);
            String key = entry.getKey()+":";
            keyCell.setCellValue(key);

            Object value = entry.getValue();
            String textValue = getValueStr(value,"");
            XSSFCell valueCell = row.createCell(i++);
            writeCell(textValue,valueCell);
        }
        return 1;
    }

    private void writeCell(String value,XSSFCell cell){
        if (value != null) {
            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
            Matcher matcher = p.matcher(value);
            if (matcher.matches()) {
                // 是数字当作double处理
                cell.setCellValue(Double.parseDouble(value));
            } else {
                XSSFRichTextString richString = new XSSFRichTextString(value);
                cell.setCellValue(richString);
            }
        }
    }


    private void writeHeads(String[] headers,XSSFRow row,XSSFCellStyle style){
        for(short i = 0;i<headers.length;i++){
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
    }

    private String getValueStr(Object value,String pattern){
        String textValue = null;
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            textValue = "TRUE";
            if (!bValue) {
                textValue = "FALSE";
            }
        } else if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        }else {
            // 其它数据类型都当作字符串简单处理
            textValue = value.toString();
        }
        return textValue;
    }


    private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook){
        XSSFCellStyle style  = workbook.createCellStyle();
//        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
//        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeight(12);
        font.setBold(true);

        style.setFont(font);
        return style;
    }

    private XSSFCellStyle  createBodyStyle(XSSFWorkbook workbook){
        XSSFCellStyle style = workbook.createCellStyle();
//        style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
//        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeight(12);
        font.setBold(true);
        return style;
    }

    public void exportExcel(String title,String[] headers,Collection<T> dataset,OutputStream out,String pattern){
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(15);

        XSSFCellStyle style = createHeaderStyle(workbook);

        XSSFCellStyle style2 = createBodyStyle(workbook);
        /**
         * 注释
         */
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        /**
         * 产生标题行
         */
        XSSFRow row = sheet.createRow(0);
        writeHeads(headers,row,style);

        Iterator<T> it = dataset.iterator();
        int index = 0;
        while(it.hasNext()){
            index++;
            row = sheet.createRow(index);
            T t = it.next();
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for(short i = 0;i<fields.length;i++){
                XSSFCell cell = row.createCell(i);
                cell.setCellStyle(style2);
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                try{
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName,new Class[]{});
                    Object value = getMethod.invoke(t,new Object[]{});
                    if(value == null){
                        continue;
                    }
                    String textValue = getValueStr(value,pattern);
                    writeCell(textValue,cell);
                }catch(Exception e){

                }
            }
        }

        try
        {
            workbook.write(out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
