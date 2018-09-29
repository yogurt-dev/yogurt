package com.github.jyoghurt.cg;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mojo(name="generate",defaultPhase= LifecyclePhase.PACKAGE)
public class CodeGenerator extends AbstractMojo {

    @Parameter
    private String jdbcUrl;

    @Parameter
    private String jdbcUser;

    @Parameter
    private String jdbcPassword;

    /**
     * 表名
     */
    @Parameter
    private String table;


    @Parameter
    private String schema;
    /**
     * 模块包名
     */
    @Parameter
    private String packageName;

    /**
     * 项目地址
     */
    @Parameter(defaultValue = "${basedir}")
    private String basedir;
    /**
     * 生成内容
     */
    @Parameter
    private GenerateBean generate;

    public void execute()  {
        CreateBean createBean = new CreateBean();
        createBean.setMysqlInfo(jdbcUrl, jdbcUser, jdbcPassword, schema);
        /** 此处修改成你的 表名 和 中文注释 ***/

        String className = createBean.getTablesNameToClassName(table);

        String lowerName = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());

        // 资源路径
        String resourcePath = File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator;
        // java路径
        String javaPath = File.separator + "src" + File.separator + "main" + File.separator + "java"
                + File.separator;
        // webapp路径
        String webappPath = File.separator + "src" + File.separator + "main" + File.separator + "webapp"
                + File.separator + "module" + File.separator;

        // //根路径
        // String srcPath = rootPath + "src" + File.separator + "main" + File.separator + "java";
        // //包路径
        // String pckPath = rootPath + "com" + File.separator + "fdcz" + File.separator + "pro" + File.separator +
        // "system";
        //
        // File file=new File(pckPath);
        // java,xml文件名称
        String modelPath = File.separator + "po" + File.separator + className + "PO.java";
        String searchFormPath = File.separator + "controller" + File.separator + "form" + File.separator + className
                + "SearchForm.java";

        //modify by limiao 20160307 以下className都修改成lowerName
        String mapperPath = File.separator + "dao" + File.separator + className + "Mapper.java";

        String mapperXmlPath = File.separator + "dao" + File.separator + className + "Mapper.xml";

        String servicePath = File.separator + "service" + File.separator + className + "Service.java";

        String serviceImplPath = File.separator + "service" + File.separator + "impl" + File.separator + className
                + "ServiceImpl.java";
        String controllerPath = File.separator + "controller" + File.separator + className + "Controller.java";

        String htmlPath = File.separator + lowerName + ".html";

        // String springPath="conf" + File.separator + "spring" + File.separator ;
        // String sqlMapPath="conf" + File.separator + "mybatis" + File.separator ;

        VelocityContext context = new VelocityContext();
        context.put("className", className); //
        context.put("lowerName", lowerName);
        context.put("codeName", lowerName);
        context.put("table", table);
        context.put("modulePackage", packageName);
        context.put("importPackage", packageName);
        context.put("moduleSimplePackage", packageName);

        //add by limiao 20160307 增加两个变量到context
        context.put("lowerName", lowerName);
        /****************************** 生成bean字段 *********************************/
        try {
            context.put("feilds", createBean.getBeanFeilds(table)); // 生成bean

        } catch (Exception e) {
            e.printStackTrace();
        }

        /******************************* 生成sql语句 **********************************/
        try {
            Map<String, Object> sqlMap = createBean.getAutoCreateSql(table);
            List<ColumnData> columnDatas = createBean.getColumnDatas(table);
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
        String modulePakPath = packageName.replaceAll("\\.", "/");
        // 生成Model
        if (generate.getPo()) {
            CommonPageParser.WriterPage(context, "PO.java.vm", basedir + javaPath + modulePakPath, modelPath); //
        }

        if (generate.getDao()) {
            CommonPageParser.WriterPage(context, "Mapper.java.vm", basedir + javaPath + modulePakPath, mapperPath); // 生成MybatisMapper接口
        }

        if (generate.getService()) {
            CommonPageParser.WriterPage(context, "Service.java.vm", basedir + javaPath + modulePakPath, servicePath);// 生成Service
            CommonPageParser.WriterPage(context, "ServiceImpl.java.vm", basedir + javaPath + modulePakPath, serviceImplPath);// 生成Service
        }

//      配置controller
        if (generate.getController()) {
            CommonPageParser.WriterPage(context, "Controller.java.vm", basedir + javaPath + modulePakPath, controllerPath);// 生成Controller
        }

    }
}
