package com.github.jyoghurt.serverApi;

import com.github.jyoghurt.core.exception.BaseException;
import com.github.jyoghurt.core.exception.ExceptionBody;
import com.sun.javadoc.*;
import com.sun.javadoc.Parameter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by jtwu on 2016/3/30.
 */
public class ExcelBuild {
    private Sheet apiSheet, errorSheet;
    protected int apiRowIndex = 0;
    protected String codePrefix;
    private SXSSFWorkbook workbook;
    protected int serviceIndex = 0;
    protected int methodIndex = 1;
    private Log log ;
    private SortedMap<Integer, String> errorMap = new TreeMap<Integer, String>();

    public void buildExcel(Map<String, ClassDoc> classDocMap, Log log) throws Exception {
        this.log = log;
        workbook = new SXSSFWorkbook();
        apiSheet = workbook.createSheet("接口列表");
        {
            int column = 0;
            apiSheet.setColumnWidth(column++, 30 * 256);
            apiSheet.setColumnWidth(column++, 15 * 256);
            apiSheet.setColumnWidth(column++, 80 * 256);
        }
        {
            Row apiTitleRow = apiSheet.createRow(apiRowIndex);
            int column = 0;
//            createHeaderCell(workbook, apiTitleRow, column++, "编号", false);
            createHeaderCell(workbook, apiTitleRow, column++, "名称", false);
            createHeaderCell(workbook, apiTitleRow, column++, "METHOD", false);
            createHeaderCell(workbook, apiTitleRow, column++, "URI", false);
        }
        errorSheet = workbook.createSheet("错误码");
        for (String classDocName : classDocMap.keySet()) {
            ClassDoc classDoc = classDocMap.get(classDocName);
            //只处理controller
            if (!filter(classDoc)) {
                continue;
            }
            for (MethodDoc methodDoc : classDoc.methods()) {
                try {
                    createApiSheet(methodDoc, classDoc);
                } catch (Exception e) {
                    log.error("creat sheet error,class is "+classDocName + " method is " + methodDoc.name(),e);
                }
            }
        }
        tearDown();
    }

    private Boolean filter(ClassDoc classDoc) {
        for (AnnotationDesc annotationDesc : classDoc.annotations()) {
            if (annotationDesc.annotationType().name().equals("RestController") ||
                    annotationDesc.annotationType().name().equals("Controller")) {
                return true;
            }
        }
        return false;
    }


    private void createApiSheet(MethodDoc methodDoc, ClassDoc classDoc) throws Exception {
        InterfaceEntity interfaceEntity = new InterfaceEntity();
        //设置请求路径
        for (AnnotationDesc annotationDesc : classDoc.annotations()) {
            if (annotationDesc.annotationType().name().equals("RequestMapping")) {
                interfaceEntity.setIfUrl(getAnnotationValue(annotationDesc, "value").toString());
            }
        }
        //解析方法
        analyzeMethodDesc(methodDoc, interfaceEntity);
        //接口列表
        {

            CreationHelper creationHelper = workbook.getCreationHelper();
            Row apiTitleRow = apiSheet.createRow(++apiRowIndex);
            int column = 0;
            Cell nameCell = apiTitleRow.createCell(column++);
            {
                nameCell.setCellValue(interfaceEntity.getIfName());
                Hyperlink link = creationHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
                link.setAddress("#" + interfaceEntity.getIfName() + "!A1");
                nameCell.setHyperlink(link);
            }
            apiTitleRow.createCell(column++).setCellValue(interfaceEntity.getIfType());
            apiTitleRow.createCell(column++).setCellValue(interfaceEntity.getIfUrl());
        }
        //接口详情
        {
            Sheet sheet = workbook.createSheet(interfaceEntity.getIfName());
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 30 * 256);
            sheet.setColumnWidth(3, 60 * 256);
            sheet.setColumnWidth(4, 20 * 256);
            sheet.setColumnWidth(6, 30 * 256);


            int rowIndex = 0;
            {
                Row row = sheet.createRow(rowIndex++);
                Cell nameCell = row.createCell(0);
                {
                    nameCell.setCellValue("接口列表");
                    CreationHelper creationHelper = workbook.getCreationHelper();
                    Hyperlink link = creationHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
                    link.setAddress("#接口列表!A1");
                    nameCell.setHyperlink(link);
                }
            }

            {
                Row row = sheet.createRow(rowIndex++);
                createHeaderCell(workbook, row, 2, "接口名称");
                createValueCell(workbook, row, 3, interfaceEntity.getIfName());
            }
            {
                Row row = sheet.createRow(rowIndex++);
                createHeaderCell(workbook, row, 2, "接口描述");
                createValueCell(workbook, row, 3, interfaceEntity.getIfDesc());
            }
            {
                Row row = sheet.createRow(rowIndex++);
                createHeaderCell(workbook, row, 2, "接口地址");
                createValueCell(workbook, row, 3, interfaceEntity.getIfUrl());
            }
            {
                Row row = sheet.createRow(rowIndex++);
                createHeaderCell(workbook, row, 2, "接口类型");
                createValueCell(workbook, row, 3, interfaceEntity.getIfType());
            }
            {
                Row row = sheet.createRow(rowIndex++);
                createHeaderCell(workbook, row, 2, "起始版本");
                createValueCell(workbook, row, 3, interfaceEntity.getSince());
            }
            //请求参数
            rowIndex++;
            {
                Row row = sheet.createRow(rowIndex++);
                int column = 1;
                createHeaderCell(workbook, row, column++, "请求参数");
                createHeaderCell(workbook, row, column++, "KEY");
                createHeaderCell(workbook, row, column++, "说明");
                createHeaderCell(workbook, row, column++, "数据类型");
                createHeaderCell(workbook, row, column++, "必填项");
                createHeaderCell(workbook, row, column++, "校验规则");
            }
            for (ParameterEntity parameterEntity : interfaceEntity.getRequestParams()) {
                rowIndex = createParameter(sheet, rowIndex, parameterEntity);
            }

            //返回参数
            for (ClassEntity classEntity : interfaceEntity.getResponseClass()) {
                rowIndex++;
                Row row = sheet.createRow(rowIndex++);
                createValueCell(workbook, row, 1, classEntity.getClassName());
                if (CollectionUtils.isNotEmpty(classEntity.getParams())) {
                    {
                        rowIndex = createParameterHeader(sheet, rowIndex);
                    }
                    for (ParameterEntity parameterEntity : classEntity.getParams()) {
                        rowIndex = createParameter(sheet, rowIndex, parameterEntity);
                    }
                }
            }
            //错误码
            rowIndex++;
            {
                Row row = sheet.createRow(rowIndex++);
                int column = 1;
                createHeaderCell(workbook, row, column++, "错误码");
                createHeaderCell(workbook, row, column++, "错误描述");
            }
            {
                for (String key : interfaceEntity.getErrors().keySet()) {
                    Row row = sheet.createRow(rowIndex++);
                    int column = 1;
                    createValueCell(workbook, row, column++, key);
                    createValueCell(workbook, row, column++, interfaceEntity.getErrors().get(key));
                }
            }
        }

    }

    private int createParameterHeader(Sheet sheet, int rowIndex) {
        Row row = sheet.createRow(rowIndex++);
        int column = 1;
        createHeaderCell(workbook, row, column++, "返回参数");
        createHeaderCell(workbook, row, column++, "KEY");
        createHeaderCell(workbook, row, column++, "说明");
        createHeaderCell(workbook, row, column++, "数据类型");
        createHeaderCell(workbook, row, column++, "必填项");
        createHeaderCell(workbook, row, column++, "校验规则");
        return rowIndex;
    }

    private int createParameter(Sheet sheet, int rowIndex, ParameterEntity parameterEntity) {
        Row row = sheet.createRow(rowIndex++);
        int column = 1;
        Font font = workbook.createFont();
        if (parameterEntity.getRequired()) {
            font.setColor(HSSFColor.RED.index);
        }
        createValueCell(workbook, row, column++, parameterEntity.getParamName(), font);
        createValueCell(workbook, row, column++, parameterEntity.getParamCode(), font);
        createValueCell(workbook, row, column++, parameterEntity.getParamDesc(), font);
        createValueCell(workbook, row, column++, parameterEntity.getParamType(), font);
        createValueCell(workbook, row, column++, parameterEntity.getRequired() ? "是" : "否", font);
        createValueCell(workbook, row, column++, parameterEntity.getParamValidRules(), font);
        return rowIndex;
    }


    private void analyzeMethodDesc(MethodDoc methodDoc, InterfaceEntity interfaceEntity) throws Exception {
        if (ArrayUtils.isNotEmpty(methodDoc.tags("since"))) {
            interfaceEntity.setSince(methodDoc.tags("since")[0].text());
        }
        //解析方法注解
        for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            switch (annotationDesc.annotationType().name()) {
                case "RequestMapping": {
                    analyzeRequestMapping(annotationDesc, interfaceEntity);
                    break;
                }
            }
        }
        //解析javadoc
        interfaceEntity.setIfName(StringUtils.substringBefore(methodDoc.commentText(), "."));
        interfaceEntity.setIfDesc(StringUtils.substringAfter(methodDoc.commentText(), "."));
        //解析请求参数
        for (Parameter parameter : methodDoc.parameters()) {
            analyzeRequestParameter(parameter, interfaceEntity, methodDoc);
        }
        //解析返回参数
        analyzeResponseParameter(interfaceEntity, methodDoc);
        //解析错误码
        analyzeErrors(interfaceEntity, methodDoc);
    }


    private void analyzeRequestParameter(Parameter parameter, InterfaceEntity interfaceEntity, MethodDoc methodDoc) {
        ParameterEntity parameterEntity = new ParameterEntity();
        parameterEntity.setParamName(parameter.name());
        parameterEntity.setParamType(parameter.typeName());

        parameterEntity.setParamCode(parameter.name());
        for (ParamTag tag : methodDoc.paramTags()) {
            if (!tag.parameterName().equals(parameter.name())) {
                continue;
            }
            //参照javadoc方法名的获取方式，以.为分割符
            if (StringUtils.isNotEmpty(tag.parameterComment()) && tag.parameterComment().contains(".")) {
                parameterEntity.setParamName(StringUtils.substringBefore(tag.parameterComment(), ".").trim());
                parameterEntity.setParamDesc(StringUtils.substringAfter(tag.parameterComment(), ".").trim());
            } else {
                parameterEntity.setParamName(tag.parameterComment());
            }

        }
        for (AnnotationDesc annotationDesc : parameter.annotations()) {
            switch (annotationDesc.annotationType().name()) {
                case "PathVariable": {
                    parameterEntity.setRequired(true);
                    if (annotationDesc.elementValues().length > 0) {
                        parameterEntity.setParamCode(getAnnotationValue(annotationDesc.elementValues()[0]).toString());
                    }
                    break;
                }

                case "RequestParam": {
                    Object value = getAnnotationValue(annotationDesc, "value");
                    if (null != value) {
                        parameterEntity.setParamCode(value.toString());
                    }
                    Object required = getAnnotationValue(annotationDesc, "required");
                    if (null != required) {
                        parameterEntity.setRequired((Boolean) required);
                    }
                    break;
                }
                default: {
                    parameterEntity.addParamValidRule(annotationDesc.annotationType().name());
                    break;
                }
            }
        }
        interfaceEntity.getRequestParams().add(parameterEntity);
    }


    private Object getAnnotationValue(AnnotationDesc annotationDesc, String fieldName) {
        for (AnnotationDesc.ElementValuePair valuePair : annotationDesc.elementValues()) {
            if (valuePair.element().name().equals(fieldName)) {
                return getAnnotationValue(valuePair);
            }
        }
        return null;
    }

    private Object getAnnotationValue(AnnotationDesc.ElementValuePair valuePair) {
        Object value = valuePair.value().value();
        if (!value.getClass().isArray()) {
            return value;
        }
        return ((AnnotationValue[]) value)[0].value();
    }

    private void analyzeRequestMapping(AnnotationDesc annotationDesc, InterfaceEntity interfaceEntity) {
        for (AnnotationDesc.ElementValuePair valuePair : annotationDesc.elementValues()) {
            Object annotationValue = getAnnotationValue(valuePair);
            switch (valuePair.element().name()) {
                case "method": {
                    interfaceEntity.setIfType(((FieldDoc) annotationValue).name());
                    break;
                }
                case "value": {
                    interfaceEntity.setIfUrl(StringUtils.join(interfaceEntity.getIfUrl(), annotationValue.toString()));
                    break;
                }
            }
        }
    }

    private void analyzeResponseParameter(InterfaceEntity interfaceEntity, MethodDoc methodDoc) throws Exception {
        //将源码以来的class都加载到class中
        ExtClasspathLoader.loadClassPath(ServerApiPlugin.classpathElements.get(0).toString());
        ExtClasspathLoader.loadJarPath(ServerApiPlugin.classpathElements);

        //先解析源码的返回值是否带泛型，javadoc api 获取不到返回值得泛型信息，因此有了以下复杂的替代方案
        Class cls = Class.forName(methodDoc.containingClass().toString());
        List<Class> paramClass = new ArrayList<>();
        for (Parameter parameter : methodDoc.parameters()) {
            paramClass.add(Class.forName(parameter.type().toString()));
        }
        Method sourceMethod = cls.getMethod(methodDoc.name(), paramClass.toArray(new Class[paramClass.size()]));
//            returnType.getClass()
        List<Type> genericList = getReturnGeneric(sourceMethod.getGenericReturnType());
        //未生成javadoc的说明，是存在与jar中，需要使用源码生成javadoc
        for (Type type : genericList) {


            ServerApiPlugin serverApiPlugin = new ServerApiPlugin();
            String typeName = type.getTypeName();
            if (typeName.contains("<")) {
                typeName = StringUtils.substringBeforeLast(typeName, "<");
            }
            if (typeName.equals("?")) {
                log.warn("请写明返回值泛型，\"?\"没办法解析"+methodDoc.toString());
                continue;
            }
            if (!ServerApiPlugin.classDocMap.containsKey(typeName)) {
                serverApiPlugin.init(getSourcePath(Class.forName(typeName)));
            }
            ClassEntity classEntity = new ClassEntity();
            classEntity.setClassName(type.getTypeName());
            ClassDoc classDoc = ServerApiPlugin.classDocMap.get(typeName);
            if (null == classDoc) {
                log.warn("typeName 没有获得classDoc " + typeName);
                continue;
            }
            for (FieldDoc fieldDoc : classDoc.serializableFields()) {
                ParameterEntity parameterEntity = new ParameterEntity();
                parameterEntity.setParamCode(fieldDoc.name());
                parameterEntity.setParamName(fieldDoc.commentText());
                parameterEntity.setParamType(fieldDoc.type().simpleTypeName());
                classEntity.getParams().add(parameterEntity);
            }
            interfaceEntity.getResponseClass().add(classEntity);
        }
    }

    public String getSourcePath(Class cls) {
        //检查用户传入的参数是否为空
        if (cls == null)
            throw new java.lang.IllegalArgumentException("参数不能为空！");
        ClassLoader loader = cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        //获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        //如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax."))
                throw new java.lang.IllegalArgumentException("不要传送系统类！");
            //在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            //判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(".") < 0)
                path = packName + "/";
            else {//否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + "/";
            }
        }

        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(path + clsName);
        //从URL对象中获取路径信息

        String realPath = url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1)
            realPath = realPath.substring(pos + 5);


        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);

        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.replace(".jar!", "-sources.jar");
        }
        //结果字符串可能因平台默认编码不同而不同。因此，改用 decode(String,String) 方法指定编码。

        if (realPath.startsWith("/")) {
            realPath = StringUtils.substringAfter(realPath, "/");
        }
        realPath = realPath.replace("/", File.separator);
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
            //解压源文件
            Runtime.getRuntime().exec(new String[]{"cmd", "/C", "jar xvf " + realPath}, null
                    , new File(getClass().getResource("/").getFile()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        realPath = getClass().getResource("/").getFile().replace("/", File.separator);
        return StringUtils.substringAfter(realPath, File.separator);
    }

    private List<Type> getReturnGeneric(Type genericReturnType) {
        return getReturnGeneric(genericReturnType, new ArrayList<>());
    }

    private List<Type> getReturnGeneric(Type genericReturnType, List<Type> list) {
        if (genericReturnType instanceof ParameterizedType)/* 如果是泛型类型 */ {
            Type[] types = ((ParameterizedType) genericReturnType).getActualTypeArguments();// 泛型类型列表
            Collections.addAll(list, types);
            for (Type type : types) {
                getReturnGeneric(type, list);
            }
        }
        return list;
    }

    private void analyzeErrors(InterfaceEntity interfaceEntity, MethodDoc methodDoc) throws ClassNotFoundException, IllegalAccessException {
        if (ArrayUtils.isEmpty(methodDoc.thrownExceptions())) {
            return;
        }

        for (ClassDoc classDoc : methodDoc.thrownExceptions()) {
            Class exceptionClass = Class.forName(classDoc.toString());
            if(Exception.class.equals(exceptionClass)){
                continue;
            }
            if (!isAssignableFrom(BaseException.class,exceptionClass)) {
                continue;
            }
            for (Field field : exceptionClass.getFields()) {
                if (isAssignableFrom(ExceptionBody.class,field.getType())) {
                    field.setAccessible(true);
//                    ExceptionBody exceptionBody = (ExceptionBody) field.get(null);
                    Object value = field.get(null);
                    try {
                        Field  code = field.get(null).getClass().getDeclaredField("code");
                        code.setAccessible(true);
                        Field  message = field.get(null).getClass().getDeclaredField("message");
                        message.setAccessible(true);
                        interfaceEntity.getErrors().put(code.get(value).toString(), message.get(value).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * java原生的isAssignableFrom在maven环境下无效，未找到原因
     * @param targetClass
     * @return
     */
    private boolean isAssignableFrom(Class sourceClass, Class targetClass){
        if(Object.class.equals(targetClass)){
            return false;
        }
        if(targetClass.getTypeName().equals(sourceClass.getTypeName())){
            return true;
        }
        return isAssignableFrom(sourceClass,targetClass.getSuperclass());
    }

    protected String xlsxName = "serverApi.xlsx";

    public void tearDown() throws Exception {
        {
            int rowIndex = 0;
            Row titleRow = errorSheet.createRow(rowIndex++);
            createHeaderCell(workbook, titleRow, 0, "异常码");
            createHeaderCell(workbook, titleRow, 1, "描述");
            errorSheet.setColumnWidth(0, 30 * 256);
            errorSheet.setColumnWidth(1, 70 * 256);
            {
                Row row = errorSheet.createRow(rowIndex++);
                createValueCell(workbook, row, 0, -1);
                createValueCell(workbook, row, 1, "未登录");
            }
            {
                Row row = errorSheet.createRow(rowIndex++);
                createValueCell(workbook, row, 0, 0);
                createValueCell(workbook, row, 1, "异常");
            }
            {
                Row row = errorSheet.createRow(rowIndex++);
                createValueCell(workbook, row, 0, 1);
                createValueCell(workbook, row, 1, "成功");
            }
            {
                Row row = errorSheet.createRow(rowIndex++);
                createValueCell(workbook, row, 0, "");
                createValueCell(workbook, row, 1, "以下是业务自定义状态值");
            }
            for (Map.Entry<Integer, String> e : errorMap.entrySet()) {
                Row row = errorSheet.createRow(rowIndex++);
                createValueCell(workbook, row, 0, e.getKey());
                createValueCell(workbook, row, 1, e.getValue());
            }
        }
        workbook.write(new FileOutputStream(xlsxName));
    }


    protected Cell createValueCell(SXSSFWorkbook workbook, Row row, int index) {
//        Cell cell = row.createCell(index);
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setWrapText(true);
//        Font font = workbook.createFont();
//        font.setFontName("宋体");
//        font.setFontHeightInPoints((short) 14);
//        cellStyle.setFont(font);
//        setBorder(cellStyle);
//        cell.setCellStyle(cellStyle);
        return createValueCell(workbook, row, index, null);
    }

    protected Cell createValueCell(SXSSFWorkbook workbook, Row row, int index, Object value) {
        return createValueCell(workbook, row, index, value, workbook.createFont());
    }

    private Cell createValueCell(SXSSFWorkbook workbook, Row row, int index, Object value, Font font) {
        Cell cell = row.createCell(index);
        CellStyle cellStyle = workbook.createCellStyle();
        setBorder(cellStyle);
        {
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 14);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
        }
        if (value == null) {
            return cell;
        }
        if (value.toString().contains("\n")) {
            cell.setCellValue(new XSSFRichTextString((String) value));
        } else {
            cell.setCellValue(value.toString());
        }
        return cell;
    }

    protected Cell createHeaderCell(SXSSFWorkbook workbook, Row row, int index, String header) {
        return createHeaderCell(workbook, row, index, header, true);
    }


    protected Cell createHeaderCell(SXSSFWorkbook workbook, Row row, int index, String header, boolean border) {
        Cell cell = row.createCell(index);
        cell.setCellValue(header);

        CellStyle cellStyle = workbook.createCellStyle();
        {
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 15);
            cellStyle.setFont(font);
        }
        if (border) {
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
            setBorder(cellStyle);
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    private void setBorder(CellStyle cellStyle) {
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    }
}

