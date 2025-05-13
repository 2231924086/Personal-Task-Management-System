package com.taskmanager.service;

import com.taskmanager.bean.User;
import java.util.List;

public interface UserService {
    // 用户注册
    boolean register(User user);

    // 用户登录
    User login(String username, String password);

    // 更新用户信息
    boolean updateUser(User user);

    // 删除用户
    boolean deleteUser(Integer userId);

    // 获取用户信息
    User getUserById(Integer userId);

    // 获取用户信息（通过用户名）
    User getUserByUsername(String username);

    // 获取所有用户
    List<User> getAllUsers();

    // 更新用户状态
    boolean updateUserStatus(Integer userId, Integer status);

    // 检查用户名是否已存在
    boolean isUsernameExists(String username);

    // 检查邮箱是否已存在
    boolean isEmailExists(String email);
}