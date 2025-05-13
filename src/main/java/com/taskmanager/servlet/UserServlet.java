package com.taskmanager.servlet;

import com.taskmanager.bean.User;
import com.taskmanager.service.UserService;
import com.taskmanager.service.impl.UserServiceImpl;
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
import java.util.Map;

@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            if ("/register".equals(pathInfo)) {
                handleRegister(request, response, result);
            } else if ("/login".equals(pathInfo)) {
                handleLogin(request, response, result);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response, result);
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
            if ("/info".equals(pathInfo)) {
                handleGetUserInfo(request, response, result);
            } else if ("/checkUsername".equals(pathInfo)) {
                handleCheckUsername(request, response, result);
            } else if ("/checkEmail".equals(pathInfo)) {
                handleCheckEmail(request, response, result);
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

    private void handleRegister(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (username == null || password == null || email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        if (userService.register(user)) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "注册失败，用户名或邮箱已存在");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        User user = userService.login(username, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());

            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", user);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        result.put("success", true);
        result.put("message", "退出成功");
    }

    private void handleGetUserInfo(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "未登录");
            return;
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            result.put("success", true);
            result.put("user", user);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            result.put("success", false);
            result.put("message", "用户不存在");
        }
    }

    private void handleCheckUsername(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        String username = request.getParameter("username");
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        boolean exists = userService.isUsernameExists(username);
        result.put("success", true);
        result.put("exists", exists);
    }

    private void handleCheckEmail(HttpServletRequest request, HttpServletResponse response,
            Map<String, Object> result) throws IOException {
        String email = request.getParameter("email");
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "参数不完整");
            return;
        }

        boolean exists = userService.isEmailExists(email);
        result.put("success", true);
        result.put("exists", exists);
    }
}