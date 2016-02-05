package com.github.jyoghurt.generator.java;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class Java_Code_Generator {

    private static final String BEAN = "bean";
    private static final String MAPPER = "mapper";
    private static final String MAPPER_XML = "mapper_xml";
    private static final String SERVICE = "service";
    private static final String CONTROLLER = "controller";
    private static final String HTML = "html";
    private static ResourceBundle res = ResourceBundle.getBundle("environment-config");
    private static String username = res.getString("jdbc_username");
    private static String passWord = res.getString("jdbc_password");
    private static String dbInstance = res.getString("jdbc_database");
    private static String url = res.getString("jdbc_url").replace("${jdbc_database}", dbInstance).replace
            ("${jdbc_ip}", res.getString("jdbc_ip"));

    /**
     * @param tableName     表名
     * @param codeName      表名对应的中文注释
     * @param modulePackage 模块包：com.fdcz.pro.system
     * @param moduleName    模块名
     * @param catalogue     自定义目录，要生成多个项目文件时，需要指定不同项目目录
     * @param templates     生成那些模板
     */
    public static void create(String tableName, String codeName, String moduleName, String modulePackage,
                              String catalogue, String... templates) {
        if (null == tableName || "".equals(tableName)) {
            return;
        }

        if (null == codeName || "".equals(codeName)) {
            return;
        }


        if (null == modulePackage || "".equals(modulePackage)) {
            return;
        }

        CreateBean createBean = new CreateBean();
        createBean.setMysqlInfo(url, username, passWord, dbInstance);
        /** 此处修改成你的 表名 和 中文注释 ***/
        String className = createBean.getTablesNameToClassName(tableName);
        String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());

        // 项目跟路径路径，此处修改为你的项目路径
        String rootPath = CommonPageParser.getRootPath();// "F:\\openwork\\open\\";
        // 资源路径
        String resourcePath = File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator;
        // java路径
        String javaPath = File.separator + "src" + File.separator + "main" + File.separator + "java"
                + File.separator;
        // webapp路径
        String webappPath = File.separator + "src" + File.separator + "main" + File.separator + "webapp"
                + File.separator + "module" + File.separator;

        String moduleSimplePackage = modulePackage
                .substring(modulePackage.lastIndexOf(".") + 1, modulePackage.length());

        // //根路径
        // String srcPath = rootPath + "src" + File.separator + "main" + File.separator + "java";
        // //包路径
        // String pckPath = rootPath + "com" + File.separator + "fdcz" + File.separator + "pro" + File.separator +
        // "system";
        //
        // File file=new File(pckPath);
        // java,xml文件名称
        String modelPath = File.separator + "domain" + File.separator + className + ".java";
        String searchFormPath = File.separator + "controller" + File.separator + "form" + File.separator + className
                + "SearchForm.java";
        String mapperPath = File.separator + "dao" + File.separator + className + "Mapper.java";
        String mapperXmlPath = File.separator + "dao" + File.separator + className + "Mapper.xml";
        String servicePath = File.separator + "service" + File.separator + className + "Service.java";
        String serviceImplPath = File.separator + "service" + File.separator + "impl" + File.separator + className
                + "ServiceImpl.java";
        String controllerPath = File.separator + "controller" + File.separator + className + "Controller.java";
        String sqlMapperPath = File.separator + "dao" + File.separator + className + "Mapper.xml";
        String htmlPath = File.separator + className + ".html";

        String listJSPPath = lowerName + File.separator + className + "List.jsp";
        String editJSPPath = lowerName + File.separator + "edit" + className + ".jsp";

        // String springPath="conf" + File.separator + "spring" + File.separator ;
        // String sqlMapPath="conf" + File.separator + "mybatis" + File.separator ;

        VelocityContext context = new VelocityContext();
        context.put("className", className); //
        context.put("lowerName", lowerName);
        context.put("codeName", codeName);
        context.put("moduleName", moduleName);
        context.put("tableName", tableName);
        context.put("modulePackage", modulePackage);
        context.put("importPackage", modulePackage);
        context.put("moduleSimplePackage", moduleSimplePackage);
        /****************************** 生成bean字段 *********************************/
        try {
            context.put("feilds", createBean.getBeanFeilds(tableName)); // 生成bean

        } catch (Exception e) {
            e.printStackTrace();
        }

        /******************************* 生成sql语句 **********************************/
        try {
            Map<String, Object> sqlMap = createBean.getAutoCreateSql(tableName);
            List<ColumnData> columnDatas = createBean.getColumnDatas(tableName);
            List<ColumnData> normalColumns = new ArrayList<>();
            ColumnData columnDataPriKey = new ColumnData();
            for (ColumnData columnData : columnDatas) {
                if (columnData.getIsPriKey()) {
                    columnDataPriKey = columnData;
                    continue;
                }
                normalColumns.add(columnData);
            }
            context.put("columnDatas", columnDatas); // 生成bean
            context.put("prikey", columnDataPriKey.getColumnName()); // 生成主见
            context.put("normalColumns", normalColumns); // 生成非主键列表
            context.put("SQL", sqlMap);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // -------------------生成文件代码---------------------/
        String realPath = StringUtils.isEmpty(catalogue) ? rootPath : catalogue;
        String modulePakPath = modulePackage.replaceAll("\\.", "/");
        // 生成Model
        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, BEAN)) {
            CommonPageParser.WriterPage(context, "Bean.java.vm", realPath + javaPath + modulePakPath, modelPath); //
        }
        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, MAPPER)) {
            CommonPageParser.WriterPage(context, "Mapper.java.vm", realPath + javaPath + modulePakPath, mapperPath); // 生成MybatisMapper接口
        }

        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, MAPPER_XML)) {//生成xml
            CommonPageParser.WriterPage(context, "Mapper.xml.vm", realPath + resourcePath + modulePakPath, mapperXmlPath); //
            // 生成MybatisMapper接口
        }
        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, SERVICE)) {
            CommonPageParser.WriterPage(context, "Service.java.vm", realPath + javaPath + modulePakPath, servicePath);// 生成Service
            CommonPageParser.WriterPage(context, "ServiceImpl.java.vm", realPath + javaPath + modulePakPath, serviceImplPath);// 生成Service
        }

//      配置controller
        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, CONTROLLER)) {
            CommonPageParser.WriterPage(context, "Controller.java.vm", rootPath + javaPath + modulePakPath, controllerPath);// 生成Controller
        }
//      配置html
        if (ArrayUtils.isNotEmpty(templates) && ArrayUtils.contains(templates, HTML)) {
            CommonPageParser.WriterPage(context, "Html.html.vm", rootPath + webappPath + StringUtils.substringAfterLast
                    (modulePackage, "."), htmlPath);//
            // 生成Controller
        }

        // CommonPageParser.WriterPage(context, "TempList.jsp", webappPath, listJSPPath);// 生成list jsp
        // CommonPageParser.WriterPage(context, "editTemp.jsp", webappPath, editJSPPath);// 生成edit jsp
    }
}
