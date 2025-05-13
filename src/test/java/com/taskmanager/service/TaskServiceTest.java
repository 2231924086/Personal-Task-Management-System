package com.taskmanager.service;

import com.taskmanager.bean.Task;
import com.taskmanager.dao.TaskDao;
import com.taskmanager.service.impl.TaskServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private TaskService taskService;

    @Mock
    private TaskDao taskDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        taskService = new TaskServiceImpl();
        // 使用反射设置taskDao
        try {
            java.lang.reflect.Field field = TaskServiceImpl.class.getDeclaredField("taskDao");
            field.setAccessible(true);
            field.set(taskService, taskDao);
        } catch (Exception e) {
            fail("设置taskDao失败：" + e.getMessage());
        }
    }

    @Test
    public void testCreateTask_Success() {
        // 准备测试数据
        Task task = new Task();
        task.setUserId(1);
        task.setCategoryId(1);
        task.setTitle("测试任务");
        task.setDescription("测试任务描述");
        task.setPriority(1);
        task.setStatus(0);
        task.setDueDate(new Date());

        // 模拟DAO层行为
        when(taskDao.insert(any(Task.class))).thenReturn(1);

        // 执行测试
        boolean result = taskService.createTask(task);

        // 验证结果
        assertTrue(result);
        verify(taskDao).insert(any(Task.class));
    }

    @Test
    public void testUpdateTask_Success() {
        // 准备测试数据
        Task task = new Task();
        task.setTaskId(1);
        task.setUserId(1);
        task.setTitle("更新后的任务");
        task.setDescription("更新后的任务描述");
        task.setPriority(1);
        task.setDueDate(new Date());
        task.setCategoryId(1);

        // 模拟DAO层行为
        when(taskDao.findById(1)).thenReturn(task);
        when(taskDao.update(any(Task.class))).thenReturn(1);

        // 执行测试
        boolean result = taskService.updateTask(task);

        // 验证结果
        assertTrue(result);
        verify(taskDao).update(task);
    }

    @Test
    public void testDeleteTask_Success() {
        // 准备测试数据
        Integer taskId = 1;

        // 模拟DAO层行为
        when(taskDao.delete(taskId)).thenReturn(1);

        // 执行测试
        boolean result = taskService.deleteTask(taskId);

        // 验证结果
        assertTrue(result);
        verify(taskDao).delete(taskId);
    }

    @Test
    public void testGetTaskById_Success() {
        // 准备测试数据
        Integer taskId = 1;
        Task expectedTask = new Task();
        expectedTask.setTaskId(taskId);
        expectedTask.setTitle("测试任务");

        // 模拟DAO层行为
        when(taskDao.findById(taskId)).thenReturn(expectedTask);

        // 执行测试
        Task result = taskService.getTaskById(taskId);

        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getTaskId());
        assertEquals("测试任务", result.getTitle());
    }

    @Test
    public void testGetUserTasks_Success() {
        // 准备测试数据
        Integer userId = 1;
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.findByUserId(userId)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getUserTasks(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetCategoryTasks_Success() {
        // 准备测试数据
        Integer categoryId = 1;
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.findByCategoryId(categoryId)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getCategoryTasks(categoryId);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetTasksByStatus_Success() {
        // 准备测试数据
        Integer userId = 1;
        Integer status = 1;
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.findByStatus(userId, status)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getTasksByStatus(userId, status);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetTasksByPriority_Success() {
        // 准备测试数据
        Integer userId = 1;
        Integer priority = 1;
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.findByPriority(userId, priority)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getTasksByPriority(userId, priority);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetTasksByDateRange_Success() {
        // 准备测试数据
        Integer userId = 1;
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + 86400000); // 明天
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.findByDateRange(userId, startDate, endDate)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getTasksByDateRange(userId, startDate, endDate);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateTaskStatus_Success() {
        // 准备测试数据
        Integer taskId = 1;
        Integer status = 1;

        // 模拟DAO层行为
        when(taskDao.updateStatus(taskId, status)).thenReturn(1);

        // 执行测试
        boolean result = taskService.updateTaskStatus(taskId, status);

        // 验证结果
        assertTrue(result);
        verify(taskDao).updateStatus(taskId, status);
    }

    @Test
    public void testSearchTasks_Success() {
        // 准备测试数据
        Integer userId = 1;
        String keyword = "测试";
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(new Task());
        expectedTasks.add(new Task());

        // 模拟DAO层行为
        when(taskDao.search(userId, keyword)).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.searchTasks(userId, keyword);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetTaskCountByStatus_Success() {
        // 准备测试数据
        Integer userId = 1;
        Integer status = 1;

        // 模拟DAO层行为
        when(taskDao.countByStatus(userId, status)).thenReturn(5);

        // 执行测试
        int result = taskService.getTaskCountByStatus(userId, status);

        // 验证结果
        assertEquals(5, result);
    }

    @Test
    public void testIsTaskBelongsToUser_True() {
        // 准备测试数据
        Integer taskId = 1;
        Integer userId = 1;
        Task task = new Task();
        task.setTaskId(taskId);
        task.setUserId(userId);

        // 模拟DAO层行为
        when(taskDao.findById(taskId)).thenReturn(task);

        // 执行测试
        boolean result = taskService.isTaskBelongsToUser(taskId, userId);

        // 验证结果
        assertTrue(result);
    }

    @Test
    public void testIsTaskBelongsToUser_False() {
        // 准备测试数据
        Integer taskId = 1;
        Integer userId = 1;
        Task task = new Task();
        task.setTaskId(taskId);
        task.setUserId(2); // 不同的用户ID

        // 模拟DAO层行为
        when(taskDao.findById(taskId)).thenReturn(task);

        // 执行测试
        boolean result = taskService.isTaskBelongsToUser(taskId, userId);

        // 验证结果
        assertFalse(result);
    }
}