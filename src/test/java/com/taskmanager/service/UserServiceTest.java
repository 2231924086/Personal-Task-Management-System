package com.taskmanager.service;

import com.taskmanager.bean.User;
import com.taskmanager.dao.UserDao;
import com.taskmanager.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl();
        // 使用反射设置userDao
        try {
            java.lang.reflect.Field field = UserServiceImpl.class.getDeclaredField("userDao");
            field.setAccessible(true);
            field.set(userService, userDao);
        } catch (Exception e) {
            fail("设置userDao失败：" + e.getMessage());
        }
    }

    @Test
    public void testRegister_Success() {
        // 准备测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        // 模拟DAO层行为
        when(userDao.findByUsername("testuser")).thenReturn(null);
        when(userDao.insert(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.register(user);

        // 验证结果
        assertTrue(result);
        verify(userDao).insert(any(User.class));
    }

    @Test
    public void testRegister_DuplicateUsername() {
        // 准备测试数据
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        // 模拟DAO层行为
        when(userDao.findByUsername("testuser")).thenReturn(new User());

        // 执行测试
        boolean result = userService.register(user);

        // 验证结果
        assertFalse(result);
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    public void testLogin_Success() {
        // 准备测试数据
        String username = "testuser";
        String password = "password";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setPassword(password);

        // 模拟DAO层行为
        when(userDao.validateLogin(username, password)).thenReturn(expectedUser);

        // 执行测试
        User result = userService.login(username, password);

        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
    }

    @Test
    public void testLogin_Failure() {
        // 准备测试数据
        String username = "testuser";
        String password = "wrongpassword";

        // 模拟DAO层行为
        when(userDao.validateLogin(username, password)).thenReturn(null);

        // 执行测试
        User result = userService.login(username, password);

        // 验证结果
        assertNull(result);
    }

    @Test
    public void testUpdateUser_Success() {
        // 准备测试数据
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // 模拟DAO层行为
        when(userDao.findById(1)).thenReturn(user);
        when(userDao.update(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.updateUser(user);

        // 验证结果
        assertTrue(result);
        verify(userDao).update(user);
    }

    @Test
    public void testDeleteUser_Success() {
        // 准备测试数据
        Integer userId = 1;

        // 模拟DAO层行为
        when(userDao.delete(userId)).thenReturn(1);

        // 执行测试
        boolean result = userService.deleteUser(userId);

        // 验证结果
        assertTrue(result);
        verify(userDao).delete(userId);
    }

    @Test
    public void testGetUserById_Success() {
        // 准备测试数据
        Integer userId = 1;
        User expectedUser = new User();
        expectedUser.setUserId(userId);
        expectedUser.setUsername("testuser");

        // 模拟DAO层行为
        when(userDao.findById(userId)).thenReturn(expectedUser);

        // 执行测试
        User result = userService.getUserById(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testGetAllUsers_Success() {
        // 准备测试数据
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        // 模拟DAO层行为
        when(userDao.findAll()).thenReturn(expectedUsers);

        // 执行测试
        List<User> result = userService.getAllUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testIsUsernameExists_True() {
        // 准备测试数据
        String username = "testuser";

        // 模拟DAO层行为
        when(userDao.findByUsername(username)).thenReturn(new User());

        // 执行测试
        boolean result = userService.isUsernameExists(username);

        // 验证结果
        assertTrue(result);
    }

    @Test
    public void testIsUsernameExists_False() {
        // 准备测试数据
        String username = "testuser";

        // 模拟DAO层行为
        when(userDao.findByUsername(username)).thenReturn(null);

        // 执行测试
        boolean result = userService.isUsernameExists(username);

        // 验证结果
        assertFalse(result);
    }
}