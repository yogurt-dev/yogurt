package com.github.jyoghurt.core.utils;

import com.github.jyoghurt.core.spring.context.support.PropertyConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jtwu
 * Date: 2009-12-7
 * Time: 21:32:57
 * spring工具类
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    public static final String DATA_SOURCE = "dataSource";
    private static ApplicationContext applicationContext;

    public SpringContextUtils() {
    }

    public void setApplicationContext(ApplicationContext _applicationContext)  {
        applicationContext = _applicationContext;
    }

    public static void refresh() {
        ((AbstractRefreshableWebApplicationContext) applicationContext).refresh();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> Map<String, T> getBeans(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }


    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }


    public static Connection getConnection() {
        try {
            return DataSourceUtils.getConnection((DataSource) getBean(DATA_SOURCE));
        } catch (CannotGetJdbcConnectionException e) {
            throw e;
        }
    }

    public static Connection getConnection(String dataSource) {
        try {
            return DataSourceUtils.getConnection((DataSource) getBean(dataSource));
        } catch (CannotGetJdbcConnectionException e) {
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, (DataSource) getBean(DATA_SOURCE));            //spring 2.0
    }

    public static Boolean containsBean(String beanName) {
        return applicationContext.containsBean(beanName);
    }

    public static String getProperty(String key) {
        return PropertyConfigurer.getProperty(key);
    }
}
