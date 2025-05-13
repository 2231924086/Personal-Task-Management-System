package com.taskmanager.service.impl;

import com.taskmanager.bean.User;
import com.taskmanager.dao.UserDao;
import com.taskmanager.dao.impl.UserDaoImpl;
import com.taskmanager.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public boolean register(User user) {
        // 参数验证
        if (user == null || StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())
                || StringUtils.isBlank(user.getEmail())) {
            return false;
        }

        // 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            return false;
        }

        // 检查邮箱是否已存在
        if (isEmailExists(user.getEmail())) {
            return false;
        }

        // 设置默认状态为活跃
        user.setStatus(1);

        // 保存用户信息
        return userDao.insert(user) > 0;
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        return userDao.validateLogin(username, password);
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }

        // 检查用户是否存在
        User existingUser = userDao.findById(user.getUserId());
        if (existingUser == null) {
            return false;
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername())
                && isUsernameExists(user.getUsername())) {
            return false;
        }

        // 如果修改了邮箱，检查新邮箱是否已存在
        if (!existingUser.getEmail().equals(user.getEmail())
                && isEmailExists(user.getEmail())) {
            return false;
        }

        return userDao.update(user) > 0;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        if (userId == null) {
            return false;
        }
        return userDao.delete(userId) > 0;
    }

    @Override
    public User getUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userDao.findById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public boolean updateUserStatus(Integer userId, Integer status) {
        if (userId == null || status == null) {
            return false;
        }
        return userDao.updateStatus(userId, status) > 0;
    }

    @Override
    public boolean isUsernameExists(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        return userDao.findByUsername(username) != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        // 由于UserDao中没有直接通过邮箱查询的方法，这里通过查询所有用户来检查
        List<User> users = userDao.findAll();
        return users.stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
}