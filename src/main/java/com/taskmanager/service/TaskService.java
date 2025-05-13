package com.taskmanager.service;

import com.taskmanager.bean.Task;
import java.util.Date;
import java.util.List;

public interface TaskService {
    // 创建任务
    boolean createTask(Task task);

    // 更新任务
    boolean updateTask(Task task);

    // 删除任务
    boolean deleteTask(Integer taskId);

    // 获取任务信息
    Task getTaskById(Integer taskId);

    // 获取用户的所有任务
    List<Task> getUserTasks(Integer userId);

    // 获取分类下的所有任务
    List<Task> getCategoryTasks(Integer categoryId);

    // 获取指定状态的任务
    List<Task> getTasksByStatus(Integer userId, Integer status);

    // 获取指定优先级的任务
    List<Task> getTasksByPriority(Integer userId, Integer priority);

    // 获取指定日期范围内的任务
    List<Task> getTasksByDateRange(Integer userId, Date startDate, Date endDate);

    // 更新任务状态
    boolean updateTaskStatus(Integer taskId, Integer status);

    // 搜索任务
    List<Task> searchTasks(Integer userId, String keyword);

    // 获取任务统计信息
    int getTaskCountByStatus(Integer userId, Integer status);

    // 验证任务是否属于指定用户
    boolean isTaskBelongsToUser(Integer taskId, Integer userId);
}