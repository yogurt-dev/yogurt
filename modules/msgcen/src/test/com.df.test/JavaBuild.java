package com.df.test;


import com.github.jyoghurt.generator.java.JavaCodeGenerator;
import org.junit.Test;

/**
 * Created by jtwu on 2015/5/22.
 */
public class JavaBuild {

    @Test
    public void testJavaBuild(){
        /**
         *
         * @param tableName
         *            表名
         * @param codeName
         *            表名对应的中文注释
         * @param catalogue
         *           自定义目录,默认是本工程
         * @param modulePackage
         *            模块包：com.fdcz.pro.system
         */
        String tableName = "MsgT";
        String codeName = "消息发送对象";
        String moduleName = "消息发送对象";
        String modulePackage = "com.df.community.msgcen";
        JavaCodeGenerator.create(tableName, codeName, moduleName, modulePackage,
              // "C:\\lvsvn\\java_common\\branch\\develop\\com.df" +
              //  ".msgcen\\msgcen-core\\src\\main\\java\\com\\df\\msgcen\\module\\manager",
                null,
                "html",
                "bean"
                , "controller"
                , "service"
                , "mapper"
        );
    }
}