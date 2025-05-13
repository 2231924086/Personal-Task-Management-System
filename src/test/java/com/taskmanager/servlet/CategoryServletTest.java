package com.taskmanager.servlet;

import com.taskmanager.bean.Category;
import com.taskmanager.service.CategoryService;
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

public class CategoryServletTest {
    private CategoryServlet categoryServlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private CategoryService categoryService;
    @Mock
    private PrintWriter writer;

    private StringWriter stringWriter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        categoryServlet = new CategoryServlet();
        // 使用反射注入模拟的CategoryService
        java.lang.reflect.Field field = CategoryServlet.class.getDeclaredField("categoryService");
        field.setAccessible(true);
        field.set(categoryServlet, categoryService);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);

        // 设置基本的mock行为
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(writer);
        when(session.getAttribute("userId")).thenReturn(1);
    }

    @Test
    public void testHandleCreateCategory_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("categoryName")).thenReturn("测试分类");
        when(categoryService.createCategory(any(Category.class))).thenReturn(true);

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("\"message\":\"创建分类成功\""));
    }

    @Test
    public void testHandleCreateCategory_NotLoggedIn() throws ServletException, IOException {
        // 设置未登录状态
        when(session.getAttribute("userId")).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/create");

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("\"message\":\"未登录\""));
    }

    @Test
    public void testHandleCreateCategory_MissingParameter() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("categoryName")).thenReturn(null);

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"分类名称不能为空\""));
    }

    @Test
    public void testHandleGetUserCategories_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/list");
        List<Category> mockCategories = Arrays.asList(
                createCategory(1, "分类1"),
                createCategory(2, "分类2"));
        when(categoryService.getUserCategories(1)).thenReturn(mockCategories);

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("\"categories\""));
    }

    @Test
    public void testHandleUpdateCategory_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(request.getParameter("categoryName")).thenReturn("更新后的分类");
        when(categoryService.updateCategory(any(Category.class))).thenReturn(true);

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("\"message\":\"更新分类成功\""));
    }

    @Test
    public void testHandleUpdateCategory_InvalidId() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("categoryId")).thenReturn("invalid");
        when(request.getParameter("categoryName")).thenReturn("更新后的分类");

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("\"message\":\"分类ID格式错误\""));
    }

    @Test
    public void testHandleDeleteCategory_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.deleteCategory(1)).thenReturn(true);

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("\"message\":\"删除分类成功\""));
    }

    @Test
    public void testHandleDeleteCategory_NotFound() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.deleteCategory(1)).thenReturn(false);

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("\"message\":\"删除分类失败，分类不存在\""));
    }

    @Test
    public void testHandleCheckCategoryName_Success() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/checkName");
        when(request.getParameter("categoryName")).thenReturn("测试分类");
        when(categoryService.isCategoryNameExists(1, "测试分类")).thenReturn(true);

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("\"exists\":true"));
    }

    @Test
    public void testHandleCheckCategoryName_MissingParameter() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/checkName");
        when(request.getParameter("categoryName")).thenReturn(null);

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("\"message\":\"参数不完整\""));
    }

    @Test
    public void testInvalidPath() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/invalid");

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(stringWriter.toString().contains("\"success\":false"));
        assertTrue(stringWriter.toString().contains("\"message\":\"请求的接口不存在\""));
    }

    @Test
    public void testHandleCreateCategory_EmptyName() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("categoryName")).thenReturn("");

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"分类名称不能为空\""));
    }

    @Test
    public void testHandleCreateCategory_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("categoryName")).thenReturn("测试分类");
        when(categoryService.createCategory(any(Category.class))).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleUpdateCategory_EmptyName() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(request.getParameter("categoryName")).thenReturn("");

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"分类名称不能为空\""));
    }

    @Test
    public void testHandleUpdateCategory_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/update");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(request.getParameter("categoryName")).thenReturn("更新后的分类");
        when(categoryService.updateCategory(any(Category.class))).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleDeleteCategory_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.deleteCategory(1)).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        categoryServlet.doPost(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleGetUserCategories_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/list");
        when(categoryService.getUserCategories(1)).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    @Test
    public void testHandleCheckCategoryName_ServiceException() throws ServletException, IOException {
        // 设置请求参数
        when(request.getPathInfo()).thenReturn("/checkName");
        when(request.getParameter("categoryName")).thenReturn("测试分类");
        when(categoryService.isCategoryNameExists(1, "测试分类")).thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        categoryServlet.doGet(request, response);

        // 验证结果
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String responseContent = stringWriter.toString();
        assertTrue("响应应包含success:false", responseContent.contains("\"success\":false"));
        assertTrue("响应应包含错误消息", responseContent.contains("\"message\":\"服务器内部错误\""));
    }

    private Category createCategory(int id, String name) {
        Category category = new Category();
        category.setCategoryId(id);
        category.setCategoryName(name);
        category.setUserId(1);
        return category;
    }
}