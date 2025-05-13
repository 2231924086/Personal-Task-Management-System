package com.taskmanager.util;

import org.junit.Test;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import static org.junit.Assert.*;

public class DBUtilTest {

    @Test
    public void testDatabaseConnection()
    {
        try (Connection conn = DBUtil.getConnection()) {
            assertNotNull("数据库连接不应该为null", conn);
            assertFalse("数据库连接不应该关闭", conn.isClosed());

            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("数据库连接成功！");
            System.out.println("数据库URL: " + metaData.getURL());
            System.out.println("数据库产品名称: " + metaData.getDatabaseProductName());
            System.out.println("数据库产品版本: " + metaData.getDatabaseProductVersion());
            System.out.println("数据库驱动名称: " + metaData.getDriverName());
            System.out.println("数据库驱动版本: " + metaData.getDriverVersion());

        } catch (Exception e) {
            fail("数据库连接测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}