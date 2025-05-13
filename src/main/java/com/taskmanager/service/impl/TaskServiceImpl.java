package com.taskmanager.service.impl;

import com.taskmanager.bean.Task;
import com.taskmanager.dao.TaskDao;
import com.taskmanager.dao.impl.TaskDaoImpl;
import com.taskmanager.service.TaskService;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao = new TaskDaoImpl();

    @Override
    public boolean createTask(Task task) {
        // 参数验证
        if (task == null || task.getUserId() == null
                || StringUtils.isBlank(task.getTitle())
                || task.getPriority() == null
                || task.getDueDate() == null) {
            return false;
        }

        // 设置默认状态为未完成
        task.setStatus(0);

        return taskDao.insert(task) > 0;
    }

    @Override
    public boolean updateTask(Task task) {
        // 参数验证
        if (task == null || task.getTaskId() == null
                || task.getUserId() == null
                || StringUtils.isBlank(task.getTitle())
                || task.getPriority() == null
                || task.getDueDate() == null) {
            return false;
        }

        // 验证任务是否属于该用户
        if (!isTaskBelongsToUser(task.getTaskId(), task.getUserId())) {
            return false;
        }

        return taskDao.update(task) > 0;
    }

    @Override
    public boolean deleteTask(Integer taskId) {
        if (taskId == null) {
            return false;
        }
        return taskDao.delete(taskId) > 0;
    }

    @Override
    public Task getTaskById(Integer taskId) {
        if (taskId == null) {
            return null;
        }
        return taskDao.findById(taskId);
    }

    @Override
    public List<Task> getUserTasks(Integer userId) {
        if (userId == null) {
            return null;
        }
        return taskDao.findByUserId(userId);
    }

    @Override
    public List<Task> getCategoryTasks(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return taskDao.findByCategoryId(categoryId);
    }

    @Override
    public List<Task> getTasksByStatus(Integer userId, Integer status) {
        if (userId == null || status == null) {
            return null;
        }
        return taskDao.findByStatus(userId, status);
    }

    @Override
    public List<Task> getTasksByPriority(Integer userId, Integer priority) {
        if (userId == null || priority == null) {
            return null;
        }
        return taskDao.findByPriority(userId, priority);
    }

    @Override
    public List<Task> getTasksByDateRange(Integer userId, Date startDate, Date endDate) {
        if (userId == null || startDate == null || endDate == null) {
            return null;
        }
        return taskDao.findByDateRange(userId, startDate, endDate);
    }

    @Override
    public boolean updateTaskStatus(Integer taskId, Integer status) {
        if (taskId == null || status == null) {
            return false;
        }
        return taskDao.updateStatus(taskId, status) > 0;
    }

    @Override
    public List<Task> searchTasks(Integer userId, String keyword) {
        if (userId == null || StringUtils.isBlank(keyword)) {
            return null;
        }
        return taskDao.search(userId, keyword);
    }

    @Override
    public int getTaskCountByStatus(Integer userId, Integer status) {
        if (userId == null || status == null) {
            return 0;
        }
        return taskDao.countByStatus(userId, status);
    }

    @Override
    public boolean isTaskBelongsToUser(Integer taskId, Integer userId) {
        if (taskId == null || userId == null) {
            return false;
        }
        Task task = taskDao.findById(taskId);
        return task != null && task.getUserId().equals(userId);
    }
}