package com.bitspace.food.controller.db;

import com.bitspace.food.config.SystemConfig;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.controller.db
 * @class_name
 * @auth erik
 * @create_time 18-3-14 下午3:58
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DataSourceUtil {
    private static Logger log = LoggerFactory.getLogger(DataSourceUtil.class);
    private static String dbUrl = SystemConfig.getDbConfig().getUrl();
    private static String dbUser = SystemConfig.getDbConfig().getUser();
    private static String dbPass = SystemConfig.getDbConfig().getPassword();
    
    public static void setUtf8mb4(Connection c) {
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("SET NAMES utf8mb4");
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("set utf8mb4 error");
        }
    }
    
    public static void setUtf8mb4(SqlSession s) {
        setUtf8mb4(s.getConnection());
    }
    
    public static SqlSessionFactory getSqlSessionFactory() {
        return InnerUtil.getSqlSessionFactory();
    }
    
    public static Connection getLongConnection(){
        try {
            Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            c.setAutoCommit(false);
            setUtf8mb4(c);
            return c;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public static void closeSilently(Connection conn) {
        if (conn != null) {
            rollbackSilently(conn);
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("commitSilently()", e);
            }
        }
    }
    
    public static void commitSilently(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                log.error("commitSilently()", e);
            }
        }
    }
    
    public static void rollbackSilently(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("rollbackSilently()", e);
            }
        }
    }
    
    // This class, use inner class' static init block to achieve
    // lazy-initialization purpose, and don't need to handle sync
    private static class InnerUtil {
        private static SqlSessionFactory instance;
        
        public static SqlSessionFactory createSessionFactory(String dbUrl, String dbUser, String dbPass) {
            PooledDataSource dataSource = new PooledDataSource(
                    "com.mysql.jdbc.Driver",
                    dbUrl,
                    dbUser, dbPass);
            
            dataSource.setPoolMaximumActiveConnections(6000);
            dataSource.setPoolMaximumIdleConnections(6000);
            dataSource.setPoolPingEnabled(true);
            dataSource.setPoolPingQuery("select 1");
            dataSource.setPoolPingConnectionsNotUsedFor(1000 * 60 * 5);
            dataSource.setPoolTimeToWait(5000);
            
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            
            // Supporting data module
            configuration.addMappers("com.bitspace.food.mapper");
            return new SqlSessionFactoryBuilder().build(configuration);
        }
        
        static {
            instance = createSessionFactory(dbUrl, dbUser, dbPass);
        }
        
        public static SqlSessionFactory getSqlSessionFactory() {
            return instance;
        }
        
    }
}
