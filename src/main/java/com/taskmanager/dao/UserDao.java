package com.taskmanager.dao;

import com.taskmanager.bean.User;
import java.util.List;

public interface UserDao {
    // 添加用户
    int insert(User user);

    // 更新用户信息
    int update(User user);

    // 删除用户
    int delete(Integer userId);

    // 根据ID查询用户
    User findById(Integer userId);

    // 根据用户名查询用户
    User findByUsername(String username);

    // 查询所有用户
    List<User> findAll();

    // 更新用户最后登录时间
    int updateLastLogin(Integer userId);

    // 更新用户状态
    int updateStatus(Integer userId, Integer status);

    // 验证用户登录
    User validateLogin(String username, String password);
}