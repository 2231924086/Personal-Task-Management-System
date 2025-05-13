package com.taskmanager.servlet;

import com.taskmanager.bean.Task;
import com.taskmanager.service.TaskService;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskServletTest {
    private TaskServlet taskServlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private TaskService taskService;
    @Mock
    private PrintWriter writer;

    private StringWriter stringWriter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        taskServlet = new TaskServlet();
        // 使用反射注入模拟的TaskService
        java.lang.reflect.Field field = TaskServlet.class.getDeclaredField("taskService");
        field.setAccessible(true);
        field.set(taskServlet, taskService);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // 设置基本的mock行为
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute("userId")).thenReturn(1);
    }

    @Test
    public void testHandleCreateTask_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("taskName")).thenReturn("测试任务");
        when(request.getParameter("description")).thenReturn("任务描述");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(request.getParameter("dueDate")).thenReturn("2024-12-31");
        when(taskService.createTask(any(Task.class))).thenReturn(true);

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"创建任务成功\""));
    }

    @Test
    public void testHandleCreateTask_MissingParameters() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("taskName")).thenReturn(null);
        when(request.getParameter("description")).thenReturn("任务描述");
        when(request.getParameter("categoryId")).thenReturn("1");

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"参数不完整\""));
    }

    @Test
    public void testHandleCreateTask_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("taskName")).thenReturn("测试任务");
        when(request.getParameter("description")).thenReturn("任务描述");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(request.getParameter("dueDate")).thenReturn("2024-12-31");
        when(taskService.createTask(any(Task.class))).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleUpdateTask_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("taskId")).thenReturn("1");
        when(request.getParameter("taskName")).thenReturn("更新后的任务");
        when(request.getParameter("description")).thenReturn("更新后的描述");
        when(request.getParameter("categoryId")).thenReturn("2");
        when(request.getParameter("dueDate")).thenReturn("2024-12-31");
        when(taskService.updateTask(any(Task.class))).thenReturn(true);

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"更新任务成功\""));
    }

    @Test
    public void testHandleUpdateTask_InvalidId() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("taskId")).thenReturn("invalid");
        when(request.getParameter("taskName")).thenReturn("更新后的任务");

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"任务ID格式错误\""));
    }

    @Test
    public void testHandleDeleteTask_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("taskId")).thenReturn("1");
        when(taskService.deleteTask(1)).thenReturn(true);

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"删除任务成功\""));
    }

    @Test
    public void testHandleDeleteTask_NotFound() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("taskId")).thenReturn("1");
        when(taskService.deleteTask(1)).thenReturn(false);

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"删除任务失败，任务不存在\""));
    }

    @Test
    public void testHandleGetUserTasks_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/list");
        List<Task> mockTasks = Arrays.asList(
                createTask(1, "任务1"),
                createTask(2, "任务2"));
        when(taskService.getUserTasks(1)).thenReturn(mockTasks);

        // 执行测试
        taskServlet.doGet(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含任务列表", responseContent.contains("\"tasks\""));
    }

    @Test
    public void testHandleGetUserTasks_NotLoggedIn() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/list");
        when(session.getAttribute("userId")).thenReturn(null);

        // 执行测试
        taskServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"未登录\""));
    }

    @Test
    public void testHandleGetUserTasks_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/list");
        when(taskService.getUserTasks(1)).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        taskServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleUpdateTaskStatus_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/updateStatus");
        when(request.getParameter("taskId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("1");
        when(taskService.updateTaskStatus(1, 1)).thenReturn(true);

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:true", responseContent.contains("\"success\":true"));
        assertTrue("响应应包含成功消息", responseContent.contains("\"message\":\"更新任务状态成功\""));
    }

    @Test
    public void testHandleUpdateTaskStatus_InvalidStatus() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/updateStatus");
        when(request.getParameter("taskId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("999");

        // 执行测试
        taskServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"无效的任务状态\""));
    }

    @Test
    public void testInvalidPath() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        taskServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"请求的接口不存在\""));
    }

    private Task createTask(int id, String name) {
        Task task = new Task();
        task.setTaskId(id);
        task.setTitle(name);
        task.setUserId(1);
        return task;
    }
}