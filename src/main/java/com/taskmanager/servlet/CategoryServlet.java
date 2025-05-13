package com.taskmanager.servlet;

import com.taskmanager.bean.Category;
import com.taskmanager.service.CategoryService;
import com.taskmanager.service.impl.CategoryServiceImpl;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/category/*")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/create".equals(pathInfo)) {
                handleCreateCategory(request, response, result);
            } else if ("/update".equals(pathInfo)) {
                handleUpdateCategory(request, response, result);
            } else if ("/delete".equals(pathInfo)) {
                handleDeleteCategory(request, response, result);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "请求的接口不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }

        out.print(gson.toJson(result));
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/list".equals(pathInfo)) {
                handleGetUserCategories(request, response, result);
            } else if ("/checkName".equals(pathInfo)) {
                handleCheckCategoryName(request, response, result);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "请求的接口不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }

        out.print(gson.toJson(result));
        out.flush();
    }

    private void handleCreateCategory(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String categoryName = request.getParameter("categoryName");
        if (categoryName == null || categoryName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "分类名称不能为空");
            return;
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setCategoryName(categoryName);

        if (categoryService.createCategory(category)) {
            result.put("success", true);
            result.put("message", "创建分类成功");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "创建分类失败，分类名称已存在");
        }
    }

    private void handleUpdateCategory(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String categoryIdStr = request.getParameter("categoryId");
        String categoryName = request.getParameter("categoryName");
        if (categoryIdStr == null || categoryName == null || categoryName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "分类名称不能为空");
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            Category category = new Category();
            category.setCategoryId(categoryId);
            category.setUserId(userId);
            category.setCategoryName(categoryName);

            if (categoryService.updateCategory(category)) {
                result.put("success", true);
                result.put("message", "更新分类成功");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "更新分类失败，分类不存在或名称已存在");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "分类ID格式错误");
        }
    }

    private void handleDeleteCategory(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            if (categoryService.deleteCategory(categoryId)) {
                result.put("success", true);
                result.put("message", "删除分类成功");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "删除分类失败，分类不存在");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "分类ID格式错误");
        }
    }

    private void handleGetUserCategories(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        List<Category> categories = categoryService.getUserCategories(userId);
        result.put("success", true);
        result.put("categories", categories);
    }

    private void handleCheckCategoryName(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String categoryName = request.getParameter("categoryName");
        if (categoryName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        boolean exists = categoryService.isCategoryNameExists(userId, categoryName);
        result.put("success", true);
        result.put("exists", exists);
    }
}