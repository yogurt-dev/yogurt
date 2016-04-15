package com.github.jyoghurt.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取property
 * Created by limiao on 2016/4/15.
 */
public class PropertyReader {

    protected static Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    /**
     * 读取property文件
     *
     * @param filename 文件的名字,如config
     * @param subName  property key的名字
     * @return String value
     * @since V2.0.2-SNAPSHOT
     */
    public static String readProperty(String filename, String subName) {
        String str = "";
        InputStream is = null;
        try {
            is = PropertyReader.class.getClassLoader().getResourceAsStream(
                    filename + ".properties");
            Properties props = new Properties();
            props.load(is);
            str = props.getProperty(subName);
        } catch (IOException e) {
            logger.error("读取配置文件失败！", e);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                logger.error("readProperty inputStream 关闭失败！", e);
            }
        }
        return str;
    }
}
