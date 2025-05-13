package com.taskmanager.util;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        try {
            // 加载配置文件
            Properties props = new Properties();
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (in == null) {
                throw new RuntimeException("找不到数据库配置文件");
            }
            props.load(in);
            in.close();

            // 配置数据源
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            if (driver == null || url == null || username == null || password == null) {
                throw new RuntimeException("数据库配置信息不完整");
            }

            dataSource.setDriverClassName(driver);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            // 配置连接池
            try {
                dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.initialSize", "5").trim()));
                dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.maxActive", "20").trim()));
                dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.maxIdle", "10").trim()));
                dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.minIdle", "5").trim()));
                dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.maxWait", "30000").trim()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("连接池配置参数格式错误: " + e.getMessage());
            }

        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}