package com.taskmanager.servlet;

import com.taskmanager.bean.Task;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.impl.TaskServiceImpl;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/task/*")
public class TaskServlet extends HttpServlet {
    private final TaskService taskService = new TaskServiceImpl();
    private final Gson gson = new Gson();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/create".equals(pathInfo)) {
                handleCreateTask(request, response, result);
            } else if ("/update".equals(pathInfo)) {
                handleUpdateTask(request, response, result);
            } else if ("/delete".equals(pathInfo)) {
                handleDeleteTask(request, response, result);
            } else if ("/updateStatus".equals(pathInfo)) {
                handleUpdateTaskStatus(request, response, result);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "请求的接口不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误：" + e.getMessage());
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
                handleGetUserTasks(request, response, result);
            } else if ("/category".equals(pathInfo)) {
                handleGetCategoryTasks(request, response, result);
            } else if ("/status".equals(pathInfo)) {
                handleGetTasksByStatus(request, response, result);
            } else if ("/priority".equals(pathInfo)) {
                handleGetTasksByPriority(request, response, result);
            } else if ("/dateRange".equals(pathInfo)) {
                handleGetTasksByDateRange(request, response, result);
            } else if ("/search".equals(pathInfo)) {
                handleSearchTasks(request, response, result);
            } else if ("/count".equals(pathInfo)) {
                handleGetTaskCount(request, response, result);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "请求的接口不存在");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误：" + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }

    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String title = request.getParameter("taskName");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String dueDateStr = request.getParameter("dueDate");

        if (title == null || categoryIdStr == null || dueDateStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            Date dueDate = dateFormat.parse(dueDateStr);

            Task task = new Task();
            task.setUserId(userId);
            task.setTitle(title);
            task.setDescription(description);
            task.setCategoryId(categoryId);
            task.setPriority(1);
            task.setDueDate(dueDate);

            if (taskService.createTask(task)) {
                result.put("success", true);
                result.put("message", "创建任务成功");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("success", false);
                result.put("message", "创建任务失败");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数格式错误");
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "日期格式错误");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }
    }

    private void handleUpdateTask(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            String title = request.getParameter("taskName");
            String description = request.getParameter("description");
            String categoryIdStr = request.getParameter("categoryId");
            String dueDateStr = request.getParameter("dueDate");

            if (title == null || categoryIdStr == null || dueDateStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "参数不完整");
                return;
            }

            Integer categoryId = Integer.parseInt(categoryIdStr);
            Date dueDate = dateFormat.parse(dueDateStr);

            Task task = new Task();
            task.setTaskId(taskId);
            task.setUserId(userId);
            task.setTitle(title);
            task.setDescription(description);
            task.setCategoryId(categoryId);
            task.setPriority(1);
            task.setDueDate(dueDate);

            if (taskService.updateTask(task)) {
                result.put("success", true);
                result.put("message", "更新任务成功");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "任务不存在");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "任务ID格式错误");
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "日期格式错误");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }
    }

    private void handleDeleteTask(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            if (taskService.deleteTask(taskId)) {
                result.put("success", true);
                result.put("message", "删除任务成功");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "删除任务失败，任务不存在");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "任务ID格式错误");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }
    }

    private void handleUpdateTaskStatus(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String taskIdStr = request.getParameter("taskId");
        String statusStr = request.getParameter("status");

        if (taskIdStr == null || statusStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer taskId = Integer.parseInt(taskIdStr);
            Integer status = Integer.parseInt(statusStr);

            if (status < 0 || status > 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "无效的任务状态");
                return;
            }

            if (taskService.updateTaskStatus(taskId, status)) {
                result.put("success", true);
                result.put("message", "更新任务状态成功");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "任务不存在");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数格式错误");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误：" + e.getMessage());
        }
    }

    private void handleGetUserTasks(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        try {
            List<Task> tasks = taskService.getUserTasks(userId);
            result.put("success", true);
            result.put("tasks", tasks);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }
    }

    private void handleGetCategoryTasks(HttpServletRequest request, HttpServletResponse response,
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
            List<Task> tasks = taskService.getCategoryTasks(categoryId);
            result.put("success", true);
            result.put("tasks", tasks);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "分类ID格式错误");
        }
    }

    private void handleGetTasksByStatus(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String statusStr = request.getParameter("status");
        if (statusStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer status = Integer.parseInt(statusStr);
            List<Task> tasks = taskService.getTasksByStatus(userId, status);
            result.put("success", true);
            result.put("tasks", tasks);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "状态格式错误");
        }
    }

    private void handleGetTasksByPriority(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String priorityStr = request.getParameter("priority");
        if (priorityStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer priority = Integer.parseInt(priorityStr);
            List<Task> tasks = taskService.getTasksByPriority(userId, priority);
            result.put("success", true);
            result.put("tasks", tasks);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "优先级格式错误");
        }
    }

    private void handleGetTasksByDateRange(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        if (startDateStr == null || endDateStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            List<Task> tasks = taskService.getTasksByDateRange(userId, startDate, endDate);
            result.put("success", true);
            result.put("tasks", tasks);
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "日期格式错误");
        }
    }

    private void handleSearchTasks(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        List<Task> tasks = taskService.searchTasks(userId, keyword);
        result.put("success", true);
        result.put("tasks", tasks);
    }

    private void handleGetTaskCount(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        String statusStr = request.getParameter("status");
        if (statusStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        try {
            Integer status = Integer.parseInt(statusStr);
            int count = taskService.getTaskCountByStatus(userId, status);
            result.put("success", true);
            result.put("count", count);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "状态格式错误");
        }
    }
}