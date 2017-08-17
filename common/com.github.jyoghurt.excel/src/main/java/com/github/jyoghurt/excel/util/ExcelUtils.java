package com.github.jyoghurt.excel.util;

import com.github.jyoghurt.excel.annotations.ExportExcel;
import com.github.jyoghurt.excel.annotations.ImportExcel;
import com.github.jyoghurt.excel.enums.ExcelExceptionEnum;
import com.github.jyoghurt.excel.exception.ParseExcelException;
import com.github.jyoghurt.excel.vo.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel 操作类
 * Created by limiao on 2016/5/9.
 */
public class ExcelUtils {

    /**
     * 根据MultipartFile文件、class类配置转换成list集合.
     *
     * @param file        MultipartFile
     * @param clazz       class类
     * @param excelConfig 各种配置参数
     * @return List<T>
     * @throws ParseExcelException
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, ExcelConfig excelConfig) throws ParseExcelException {
        try {
            return readExcel(file.getInputStream(), file.getOriginalFilename(), clazz, excelConfig);
        } catch (IOException e) {
            throw new ParseExcelException(e);
        }
    }

    /**
     * 根据MultipartFile文件、class类配置转换成list集合，默认从第二行开始读.
     *
     * @param file  MultipartFile
     * @param clazz class类
     * @return List<T>
     * @throws ParseExcelException
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) throws ParseExcelException {
        return readExcel(file, clazz, new ExcelConfig().setStartRow(2));
    }

    /**
     * 根据file文件、class类配置转换成list集合.
     *
     * @param file        file
     * @param clazz       class类
     * @param excelConfig 各种配置参数
     * @return List<T>
     * @throws ParseExcelException
     */
    public static <T> List<T> readExcel(File file, Class<T> clazz, ExcelConfig excelConfig) throws ParseExcelException {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            return readExcel(inputStream, file.getName(), clazz, excelConfig);
        } catch (IOException e) {
            throw new ParseExcelException(e);
        }
    }

    /**
     * 根据file文件、class类配置转换成list集合，默认从第二行开始读.
     *
     * @param file  file
     * @param clazz class类
     * @return List<T>
     * @throws ParseExcelException
     */
    public static <T> List<T> readExcel(File file, Class<T> clazz) throws ParseExcelException {
        return readExcel(file, clazz, new ExcelConfig().setStartRow(2));
    }

    /**
     * 根据excel文件、class类配置转换成list集合.
     *
     * @param inputStream excel文件输入流
     * @param clazz       class类
     * @param excelConfig 各种配置参数
     * @return List<T>
     * @throws ParseExcelException
     */
    private static <T> List<T> readExcel(InputStream inputStream, String fileName, Class<T> clazz, ExcelConfig excelConfig) throws ParseExcelException {
        //validate
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream can not be null");
        }
        int startRow = excelConfig.getStartRow();
        if (startRow < 1) {
            startRow = 1;
        }
        //初始化结果集
        List list = new ArrayList<>();
        //poi对象
        Workbook workbook;
        try {
            //初始化excel列与对象的映射关系
            Map<Integer, ImportExcelVo> orderMap = initAnnotationsMap(clazz);
            workbook = initWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            if (rowNum == 0) {
                return list;
            }
            int colNum = sheet.getRow(0).getLastCellNum();
            //循环行
            for (int i = startRow - 1; i <= rowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Object o = clazz.newInstance();

                // 判断整行是否都为空
                boolean allEmpty = true;
                for (int j = 0; j < colNum; j++) {
                    if (orderMap.get(j + 1) == null) {
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        ImportExcelVo importExcelVo = orderMap.get(j + 1);
                        //获取cell对应的值
                        String val = getCellValue(cell, importExcelVo);
                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(val)) {
                            allEmpty = false;
                        }
                    }
                }
                if (allEmpty) {
                    continue;
                }
                //循环列
                for (int j = 0; j < colNum; j++) {
                    if (orderMap.get(j + 1) == null) {
                        continue;
                    }
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                        ImportExcelVo importExcelVo = orderMap.get(j + 1);
                        //获取cell对应的值
                        String val = getCellValue(cell, importExcelVo);
                        //赋到对象中
                        setValue(o, importExcelVo, val);
                    }
                }
                list.add(o);
            }
        } catch (NumberFormatException e) {
            throw new ParseExcelException(ExcelExceptionEnum.ERROR_401);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseExcelException(ExcelExceptionEnum.ERROR_402);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private static Workbook initWorkbook(InputStream inputStream) throws IOException, ParseExcelException, InvalidFormatException {
        return WorkbookFactory.create(inputStream);
//        if (fileName.endsWith("xls")) {
//            return new HSSFWorkbook(inputStream);
//        } else if (fileName.endsWith("xlsx")) {
//            return new XSSFWorkbook(inputStream);
//        } else {
//            throw new ParseExcelException("can not read excel file!");
//        }
    }

    private static Map<Integer, ImportExcelVo> initAnnotationsMap(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<Integer, ImportExcelVo> map = new HashMap<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName != null && field.isAnnotationPresent(ImportExcel.class)) {
                ImportExcel sign = field.getAnnotation(ImportExcel.class);
                ImportExcelVo importExcelVo = new ImportExcelVo();
                importExcelVo.setField(field);
                importExcelVo.setDateFormat(sign.dateFormat());
                importExcelVo.setJavaScriptBody(sign.javaScriptBody());
                importExcelVo.setOrder(sign.order());
                map.put(sign.order(), importExcelVo);
            }
        }
        return map;
    }

    private static void setValue(Object source, ImportExcelVo importExcelVo, String value) throws IllegalAccessException, ParseException, ScriptException {
        Field field = importExcelVo.getField();
        Class clazz = field.getType();
        Object val = null;
        boolean hasJavaScriptBody = importExcelVo.getJavaScriptBody() != null && !"".equals(importExcelVo.getJavaScriptBody());
        if (hasJavaScriptBody) {
            val = exeScript(value, importExcelVo);
        } else {
            if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
                val = Byte.valueOf(value);
            } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
                val = Short.valueOf(value);
            } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                val = Integer.valueOf(value);
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                val = Long.valueOf(value);
            } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
                val = Float.valueOf(value);
            } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
                val = Double.valueOf(value);
            } else if (clazz.equals(BigDecimal.class)) {
                val = new BigDecimal(value);
            } else if (clazz.equals(Date.class)) {
                SimpleDateFormat sdf = new SimpleDateFormat(importExcelVo.getDateFormat());
                val = sdf.parse(value);
            } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
                val = Boolean.valueOf(value);
            } else if (String.class.equals(clazz)) {
                val = value;
            }
        }
        if (val != null) {
            field.setAccessible(true);
            field.set(source, val);
        }
    }

    private static Object exeScript(Object val, ImportExcelVo importExcelVo) throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("JavaScript");
        engine.eval("function run(param){ " + importExcelVo.getJavaScriptBody() + "}");
        if (String.class.equals(val.getClass())) {
            val = "'" + val + "'";
        }
        return engine.eval("run(" + val + ");");
    }

    private static String getCellValue(final Cell cell, ImportExcelVo importExcelVo) {
        String value = "";
        if (cell == null) {
            return value;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(importExcelVo.getDateFormat());
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    DecimalFormat df = new DecimalFormat("#.##");
                    value = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BLANK:
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            default:
                value = "";
                break;
        }
        return value;
    }

    /**
     * @param stream      输出流
     * @param excelSheets sheet页
     * @return OutputStream 输出流
     * @throws IOException            当注入输出流异常时抛出
     * @throws IllegalAccessException 当通过反射回去字段值异常时抛出
     * @throws ParseException         当时间类型转换异常时抛出
     */
    public static OutputStream createExcel(OutputStream stream, ExcelSheet... excelSheets) throws IOException, IllegalAccessException, ParseException {
        buildExcel(stream, false, excelSheets);
        return stream;
    }


    public static OutputStream createExcel(OutputStream stream, boolean ifContainNum, ExcelSheet... excelSheets) throws IOException, IllegalAccessException, ParseException {
        buildExcel(stream, ifContainNum, excelSheets);
        return stream;
    }


    /**
     * 创建excel，默认不带序号列.
     *
     * @param stream     输出流
     * @param clazz      class
     * @param entityList 对象集合
     * @throws IOException            当注入输出流异常时抛出
     * @throws IllegalAccessException 当通过反射回去字段值异常时抛出
     * @throws ParseException         当时间类型转换异常时抛出
     * @since V1.0
     */
    public static OutputStream createExcel(OutputStream stream, Class<?> clazz, List<?> entityList) throws IOException, IllegalAccessException, ParseException {
        return createExcel(stream, new ExcelSheet().setEntityType(clazz).setEntityList(entityList));
    }

    /**
     * 创建excel.
     *
     * @param stream     输出流
     * @param clazz      class
     * @param entityList 对象集合
     * @return ifContainNum 是否包含序号列
     * @throws IOException            当注入输出流异常时抛出
     * @throws IllegalAccessException 当通过反射回去字段值异常时抛出
     * @throws ParseException         当时间类型转换异常时抛出
     * @since V1.0
     */
    public static OutputStream createExcel(OutputStream stream, Class<?> clazz, List<?> entityList, boolean ifContainNum) throws IOException, IllegalAccessException, ParseException {
        return createExcel(stream, ifContainNum, new ExcelSheet().setEntityType(clazz).setEntityList(entityList));
    }


    //--------------------------- 华丽分割线，下面全是私有方法 -------------------------------------------------------------------

    /**
     * 创建新的Workbook,默认情况下生成xls格式的Workbook
     *
     * @return Workbook
     */
    private static Workbook createWorkbook() {
        return new HSSFWorkbook();
    }

    /**
     * 创建Sheet工作簿.
     *
     * @param workbook  Workbook
     * @param sheetName String
     */
    private static Sheet createSheet(Workbook workbook, String sheetName) {
        Sheet sheet;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        sheet.setDefaultColumnWidth(18);
        sheet.setPrintGridlines(true);
        sheet.setDisplayGridlines(true);
        return sheet;
    }

    /**
     * 创建Sheet工作簿中的行Row.
     *
     * @param sheet  Sheet
     * @param rowNum int
     * @return Row
     */
    private static Row createRow(Sheet sheet, int rowNum) {
        return sheet.createRow(rowNum);
    }

    /**
     * 创建一行中的一个cell，可设置样式.
     *
     * @param row       Row
     * @param cellStyle CellStyle
     * @param cellNum   int
     * @return Cell
     */
    private static Cell createCell(Row row, CellStyle cellStyle, int cellNum) {
        Cell cell = row.createCell(cellNum);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 设置标题头样式.
     *
     * @param book Workbook
     * @return CellStyle
     */
    private static CellStyle getTopCellStyle(Workbook book) {
        CellStyle topCellStyle = book.createCellStyle();
        Font font = book.createFont();
        font.setFontName("宋体");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 10);
        topCellStyle.setFont(font);
        topCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        return topCellStyle;
    }

    /**
     * 功能 设置普通内容样式.
     *
     * @param book Workbook
     * @return CellStyle
     */
    private static CellStyle getContextCellStyle(Workbook book) {
        CellStyle contextCellStyle = book.createCellStyle();
        contextCellStyle.setWrapText(false);
        contextCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contextCellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        return contextCellStyle;
    }

    /**
     * 获取excel标题行.
     *
     * @param clazz Class
     * @return String[]
     */
    private static ExcelHeader[] getExcelTitle(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();
        List<ExcelHeader> excelHeaders = new ArrayList<>();
        List<String> distinctAnnotation = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName != null && field.isAnnotationPresent(ExportExcel.class)) {
                ExportExcel sign = field.getAnnotation(ExportExcel.class);
                ExcelHeader header = new ExcelHeader(field.getType(), sign.title(), sign.order());
                excelHeaders.add(header);
                distinctAnnotation.add("get" + fieldName.toLowerCase());
                distinctAnnotation.add("is" + fieldName.toLowerCase());
            }
        }
        for (Method method : methods) {
            String name = method.getName();
            if (method != null && (name.startsWith("get") || name.startsWith("is")) && method.isAnnotationPresent(ExportExcel.class) && !distinctAnnotation.contains(name.toLowerCase())) {
                ExportExcel sign = method.getAnnotation(ExportExcel.class);
                Class type = method.getReturnType();
                ExcelHeader header = new ExcelHeader(type, sign.title(), sign.order());
                excelHeaders.add(header);
            }
        }
        Collections.sort(excelHeaders);
        return excelHeaders.toArray(new ExcelHeader[excelHeaders.size()]);
    }


    /**
     * 获取excel内容.
     *
     * @param clazz Class
     * @return List
     * @throws IllegalAccessException
     */
    private static List<ExcelBody[]> getExcelBody(Class<?> clazz, List<?> entityList) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();
        List<String> distinctAnnotation = new ArrayList<>();
        List<ExcelBody[]> bodyList = new ArrayList<>();
        for (Object o : entityList) {
            ArrayList<ExcelBody> excelBodyList = new ArrayList<>();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName != null && field.isAnnotationPresent(ExportExcel.class)) {
                    ExportExcel sign = field.getAnnotation(ExportExcel.class);
                    field.setAccessible(true);
                    Object val = field.get(o);
                    Class type = field.getType();
                    if (type.equals(Date.class) && !StringUtils.isEmpty(val)) {
                        excelBodyList.add(new ExcelBody(field.getType(), String.valueOf(sdf.format(val)), sign.order()));
                    } else {
                        excelBodyList.add(new ExcelBody(field.getType(), String.valueOf(val), sign.order()));
                    }
                    distinctAnnotation.add("get" + fieldName.toLowerCase());
                    distinctAnnotation.add("is" + fieldName.toLowerCase());
                }
            }
            for (Method method : methods) {
                String name = method.getName();
                if (method != null && (name.startsWith("get") || name.startsWith("is")) && method.isAnnotationPresent(ExportExcel.class) && !distinctAnnotation.contains(name.toLowerCase())) {
                    ExportExcel sign = method.getAnnotation(ExportExcel.class);
                    Object val = null;
                    try {
                        val = method.invoke(o);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    Class type = method.getReturnType();
                    if (type.equals(Date.class)) {
                        excelBodyList.add(new ExcelBody(type, String.valueOf(sdf.format(val)), sign.order()));
                    } else {
                        excelBodyList.add(new ExcelBody(type, String.valueOf(val), sign.order()));
                    }
                }
            }
            Collections.sort(excelBodyList);
            ExcelBody[] bodyArray = excelBodyList.toArray(new ExcelBody[excelBodyList.size()]);
            bodyList.add(bodyArray);
        }
        return bodyList;
    }

    /**
     * 渲染cell.
     *
     * @param row             Row
     * @param excelTitleArray 内容
     * @param style           样式
     */
    private static void renderTitleCell(Row row, ExcelHeader[] excelTitleArray, CellStyle style, boolean ifContainNum) {
        int length = excelTitleArray.length;
        if (ifContainNum) {
            length = length + 1;
        }
        for (int j = 0; j < length; j++) {
            Cell cell = createCell(row, style, j);
            if (ifContainNum && j == 0) {
                cell.setCellValue("序号");
                continue;
            }
            String context;
            if (ifContainNum) {
                context = excelTitleArray[j - 1].getTitle();
            } else {
                context = excelTitleArray[j].getTitle();
            }
            if (context == null || "null".equals(context)) context = "";
            cell.setCellValue(context);
        }
    }

    /* 时间类型格式化 */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 设置cell值.
     *
     * @param cell    Cell
     * @param context 内容
     * @param clazz   cell上内容的类型
     * @throws ParseException
     */
    private static void setCellValue(Cell cell, String context, Class clazz) {
        if (StringUtils.isEmpty(context)) {
            cell.setCellValue("");
            return;
        }
        if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
            cell.setCellValue(Byte.valueOf(context));
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            cell.setCellValue(Short.valueOf(context));
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            cell.setCellValue(Short.valueOf(context));
        } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            cell.setCellValue(Integer.valueOf(context));
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            cell.setCellValue(Long.valueOf(context));
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            cell.setCellValue(Float.valueOf(context));
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            cell.setCellValue(Double.valueOf(context));
        } else if (clazz.equals(BigDecimal.class)) {
            cell.setCellValue(new BigDecimal(context).doubleValue());
        } else if (clazz.equals(Date.class)) {
            cell.setCellValue(context);
        } else {
            cell.setCellValue(context);
        }
    }

    /**
     * 渲染bodyCell.
     *
     * @param row            Row
     * @param excelBodyArray 内容
     * @param style          样式
     * @param ifContainNum   是否包含序号
     * @param num            序号
     * @throws ParseException
     */
    private static void renderBodyCell(Row row, ExcelBody[] excelBodyArray, CellStyle style, boolean ifContainNum, int num) {
        int length = excelBodyArray.length;
        if (ifContainNum) {
            length = length + 1;
        }
        int k = 0;
        for (int j = 0; j < length; j++) {
            Cell cell = createCell(row, style, j);
            if (ifContainNum && j == 0) {
                cell.setCellValue(String.valueOf(num));
                continue;
            }
            String context;
            if (ifContainNum) {
                k = j - 1;
            } else {
                k = j;
            }
            context = excelBodyArray[k].getContent();
            if (context == null || "null".equals(context)) context = "";
            setCellValue(cell, context, excelBodyArray[k].getFieldType());
        }
    }

    /**
     * 组装excel
     *
     * @param stream       输出流
     * @param ifContainNum 是否包含序号列
     * @param excelSheets  sheet页数组
     * @return OutputStream 输出流
     * @throws IOException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    private static OutputStream buildExcel(OutputStream stream, boolean ifContainNum, ExcelSheet... excelSheets) throws IOException, ParseException, IllegalAccessException {
        if (excelSheets != null) {
            Workbook book = createWorkbook();
            CellStyle topStyle = getTopCellStyle(book);
            CellStyle contextStyle = getContextCellStyle(book);
            for (ExcelSheet excelSheet : excelSheets) {
                ExcelHeader[] title = getExcelTitle(excelSheet.getEntityType());
                List<ExcelBody[]> bodyList = getExcelBody(excelSheet.getEntityType(), excelSheet.getEntityList());
                Sheet sheet = createSheet(book, excelSheet.getSheetName());
                Row row = createRow(sheet, 0);
                renderTitleCell(row, title, topStyle, ifContainNum);
                for (int i = 0; i < bodyList.size(); i++) {
                    row = createRow(sheet, i + 1);
                    ExcelBody[] rowContext = bodyList.get(i);
                    renderBodyCell(row, rowContext, contextStyle, ifContainNum, i + 1);
                }
            }
            book.write(stream);
        }
        return stream;
    }


    public static void main(String args[]) {
        try {
//            File file = new File("D://456.xlsx");
//            List list = ExcelUtils.readExcel(file, TestBean1.class, new ExcelConfig().setStartRow(1));
//            System.out.print(list.size());


            ///////////////////////////////////////////

//            File file = new File("D://666.xls");
//            OutputStream out = new FileOutputStream(file);
//
//            List<TestBean1> testBean1List = new ArrayList<>();
//            TestBean1 testBean1 = new TestBean1();
//            testBean1.setBig(new BigDecimal(0));
//            testBean1.setDate(new Date());
//            testBean1.setFlag(false);
//            testBean1.setId(1);
//            testBean1.setName("a");
//            testBean1.setPassword(0);
//            testBean1List.add(testBean1);
//            ExcelUtils.createExcel(out, TestBean1.class, testBean1List);

//            ExcelUtils.createExcel(out, new ExcelSheet().setEntityType(TestBean1.class).setEntityList(testBean1List).setSheetName("hahaha")
//                    , new ExcelSheet().setEntityType(TestBean1.class).setEntityList(testBean1List).setSheetName("hahaha2")
//                    , new ExcelSheet().setEntityType(TestBean1.class).setEntityList(testBean1List).setSheetName(""));


            ///////////////////////////////////////////////////////////


            File file = new File("D://456.xlsxa");

//            WorkbookFactory.create(new FileInputStream(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
