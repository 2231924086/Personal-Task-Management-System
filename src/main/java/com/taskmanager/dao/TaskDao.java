package com.taskmanager.dao;

import com.taskmanager.bean.Task;
import java.util.Date;
import java.util.List;

public interface TaskDao {
    // 添加任务
    int insert(Task task);

    // 更新任务
    int update(Task task);

    // 删除任务
    int delete(Integer taskId);

    // 根据ID查询任务
    Task findById(Integer taskId);

    // 查询用户的所有任务
    List<Task> findByUserId(Integer userId);

    // 根据分类查询任务
    List<Task> findByCategoryId(Integer categoryId);

    // 根据状态查询任务
    List<Task> findByStatus(Integer userId, Integer status);

    // 根据优先级查询任务
    List<Task> findByPriority(Integer userId, Integer priority);

    // 根据截止日期范围查询任务
    List<Task> findByDateRange(Integer userId, Date startDate, Date endDate);

    // 更新任务状态
    int updateStatus(Integer taskId, Integer status);

    // 搜索任务（标题和内容）
    List<Task> search(Integer userId, String keyword);

    // 获取用户的任务统计信息
    int countByStatus(Integer userId, Integer status);
}