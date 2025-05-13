package com.taskmanager.dao;

import com.taskmanager.bean.User;
import com.taskmanager.dao.impl.UserDaoImpl;
import com.taskmanager.util.DBUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

public class UserDaoTest {
    private UserDao userDao;
    private User testUser;

    @Before
    public void setUp() {
        userDao = new UserDaoImpl();
        // 清理测试数据
        cleanTestData();
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser_" + UUID.randomUUID().toString().substring(0, 8));
        testUser.setPassword("testpass");
        testUser.setEmail("test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        testUser.setStatus(1);
    }

    private void cleanTestData() {
        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            // 删除测试用户数据
            stmt.executeUpdate("DELETE FROM users WHERE username LIKE 'testuser_%'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() {
        int result = userDao.insert(testUser);
        assertTrue("插入用户应该成功", result > 0);
        assertNotNull("用户ID应该被设置", testUser.getUserId());
    }

    @Test
    public void testFindById() {
        // 先插入用户
        userDao.insert(testUser);
        // 然后查找
        User found = userDao.findById(testUser.getUserId());
        assertNotNull("应该能找到用户", found);
        assertEquals("用户名应该匹配", testUser.getUsername(), found.getUsername());
    }

    @Test
    public void testFindByUsername() {
        // 先插入用户
        userDao.insert(testUser);
        // 然后查找
        User found = userDao.findByUsername(testUser.getUsername());
        assertNotNull("应该能找到用户", found);
        assertEquals("用户名应该匹配", testUser.getUsername(), found.getUsername());
    }

    @Test
    public void testUpdate() {
        // 先插入用户
        userDao.insert(testUser);
        // 修改用户信息
        testUser.setEmail("updated@example.com");
        int result = userDao.update(testUser);
        assertTrue("更新应该成功", result > 0);
        // 验证更新
        User updated = userDao.findById(testUser.getUserId());
        assertEquals("邮箱应该已更新", "updated@example.com", updated.getEmail());
    }

    @Test
    public void testDelete() {
        // 先插入用户
        userDao.insert(testUser);
        // 删除用户
        int result = userDao.delete(testUser.getUserId());
        assertTrue("删除应该成功", result > 0);
        // 验证删除
        User deleted = userDao.findById(testUser.getUserId());
        assertNull("用户应该已被删除", deleted);
    }

    @Test
    public void testValidateLogin() {
        // 先插入用户
        userDao.insert(testUser);
        // 测试登录验证
        User loggedIn = userDao.validateLogin(testUser.getUsername(), testUser.getPassword());
        assertNotNull("登录应该成功", loggedIn);
        assertEquals("用户名应该匹配", testUser.getUsername(), loggedIn.getUsername());
    }

    @Test
    public void testUpdateStatus() {
        // 先插入用户
        userDao.insert(testUser);
        // 更新状态
        int result = userDao.updateStatus(testUser.getUserId(), 0);
        assertTrue("状态更新应该成功", result > 0);
        // 验证状态更新
        User updated = userDao.findById(testUser.getUserId());
        assertEquals("状态应该已更新", (int) 0, (int) updated.getStatus());
    }
}