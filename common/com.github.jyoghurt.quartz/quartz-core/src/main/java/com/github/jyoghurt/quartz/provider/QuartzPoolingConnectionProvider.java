package com.github.jyoghurt.quartz.provider;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * user:dell
 * date: 2016/9/2.
 */
public class QuartzPoolingConnectionProvider implements ConnectionProvider {
    private static Logger logger = LoggerFactory.getLogger(QuartzPoolingConnectionProvider.class);
    public static final String DB_URL = "jdbc_url";
    public static final String DB_IP = "jdbc_ip";
    public static final String DB_DATABASE = "jdbc_database";
    public static final String DB_USER = "jdbc_username";
    public static final String DB_PASSWORD = "jdbc_password";
    public static final String DB_VALIDATION_QUERY = "validationQuery";


    private DruidDataSource datasource;

    public QuartzPoolingConnectionProvider() throws SQLException, SchedulerException {
    }


    private String analysisDBUrl(String urlTmpl, String ip, String database) {
        String tmpl = urlTmpl;
        return tmpl.replace("${jdbc_ip}", ip).replace("${jdbc_database}", database);
    }

    public void initialize() throws SQLException {
        logger.debug("-------------------------------------");
        logger.debug("start init quartz database");
        logger.debug("-------------------------------------");
        this.datasource = new DruidDataSource();
        this.datasource.setUrl(analysisDBUrl(SpringContextUtils.getProperty(DB_URL), SpringContextUtils.getProperty(DB_IP), SpringContextUtils.getProperty
                (DB_DATABASE)));
        this.datasource.setUsername(SpringContextUtils.getProperty(DB_USER));
        this.datasource.setPassword(SpringContextUtils.getProperty(DB_PASSWORD));
        this.datasource.setValidationQuery(SpringContextUtils.getProperty(DB_VALIDATION_QUERY));
        this.datasource.setInitialSize(1);
        this.datasource.setMaxActive(20);
        this.datasource.setMinIdle(1);
        this.datasource.setMaxWait(60000);
        this.datasource.setTestOnBorrow(false);
        this.datasource.setTestOnReturn(false);
        this.datasource.setTestWhileIdle(true);
        this.datasource.setTimeBetweenEvictionRunsMillis(60000);
        this.datasource.setMinEvictableIdleTimeMillis(25200000);
        this.datasource.setRemoveAbandonedTimeout(1800);
        this.datasource.setLogAbandoned(true);
        this.datasource.setFilters("mergeStat");
        logger.debug("-------------------------------------");
        logger.debug("end init quartz database");
        logger.debug("-------------------------------------");
    }


    protected DruidDataSource getDataSource() {
        return this.datasource;
    }

    public Connection getConnection() throws SQLException {
        return this.datasource.getConnection();
    }

    public void shutdown() throws SQLException {
        this.datasource.close();
    }
}
