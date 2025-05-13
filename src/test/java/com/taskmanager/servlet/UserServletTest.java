package com.taskmanager.servlet;

import com.taskmanager.bean.User;
import com.taskmanager.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServletTest {
    private UserServlet userServlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private UserService userService;
    @Mock
    private PrintWriter writer;

    private StringWriter stringWriter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userServlet = new UserServlet();
        // 使用反射注入模拟的UserService
        java.lang.reflect.Field field = UserServlet.class.getDeclaredField("userService");
        field.setAccessible(true);
        field.set(userServlet, userService);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // 设置基本的mock行为
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testHandleRegister_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(userService.register(any(User.class))).thenReturn(true);

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"注册成功\""));
    }

    @Test
    public void testHandleRegister_MissingParameters() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("email")).thenReturn("test@example.com");

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"参数不完整\""));
    }

    @Test
    public void testHandleRegister_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(userService.register(any(User.class))).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleLogin_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");
        when(userService.login("testuser", "password123")).thenReturn(mockUser);

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"登录成功\""));
        verify(session).setAttribute("userId", 1);
    }

    @Test
    public void testHandleLogin_InvalidCredentials() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(userService.login("testuser", "wrongpassword")).thenReturn(null);

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"用户名或密码错误\""));
    }

    @Test
    public void testHandleLogout_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/logout");

        // 执行测试
        userServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"退出成功\""));
        verify(session).invalidate();
    }

    @Test
    public void testHandleGetUserInfo_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/info");
        when(session.getAttribute("userId")).thenReturn(1);
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        when(userService.getUserById(1)).thenReturn(mockUser);

        // 执行测试
        userServlet.doGet(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含用户信息", responseContent.contains("\"username\":\"testuser\""));
        assertTrue("响应应包含用户信息", responseContent.contains("\"email\":\"test@example.com\""));
    }

    @Test
    public void testHandleGetUserInfo_NotLoggedIn() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/info");
        when(session.getAttribute("userId")).thenReturn(null);

        // 执行测试
        userServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"未登录\""));
    }

    @Test
    public void testHandleGetUserInfo_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/info");
        when(session.getAttribute("userId")).thenReturn(1);
        when(userService.getUserById(1)).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        userServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testInvalidPath() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        userServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"请求的接口不存在\""));
    }
}