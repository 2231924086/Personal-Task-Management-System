package com.taskmanager.dao;

import com.taskmanager.bean.Task;
import com.taskmanager.bean.User;
import com.taskmanager.bean.Category;
import com.taskmanager.dao.impl.TaskDaoImpl;
import com.taskmanager.dao.impl.UserDaoImpl;
import com.taskmanager.dao.impl.CategoryDaoImpl;
import com.taskmanager.util.DBUtil;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskDaoTest {
    private TaskDao taskDao;
    private UserDao userDao;
    private CategoryDao categoryDao;
    private Task testTask;
    private User testUser;
    private Category testCategory;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        taskDao = new TaskDaoImpl();
        userDao = new UserDaoImpl();
        categoryDao = new CategoryDaoImpl();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Create a test user first
        testUser = new User();
        testUser.setUsername("testuser_" + UUID.randomUUID().toString().substring(0, 8));
        testUser.setPassword("testpass");
        testUser.setEmail("test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
        testUser.setStatus(1);

        // Insert the user and get the real user ID
        userDao.insert(testUser);
        assertNotNull("User ID should be set after insert", testUser.getUserId());

        // Create and insert a test category
        testCategory = new Category();
        testCategory.setUserId(testUser.getUserId());
        testCategory.setCategoryName("TestCategory_" + UUID.randomUUID().toString().substring(0, 8));
        testCategory.setDescription("Test category description");
        categoryDao.insert(testCategory);
        assertNotNull("Category ID should be set after insert", testCategory.getCategoryId());

        // Create test task with the actual user and category IDs
        testTask = new Task();
        testTask.setUserId(testUser.getUserId());
        testTask.setCategoryId(testCategory.getCategoryId());
        testTask.setTitle("TestTask_" + UUID.randomUUID().toString().substring(0, 8));
        testTask.setContent("Test task content");
        testTask.setDescription("Test task description");
        testTask.setPriority(1);
        testTask.setStatus(0); // Not completed
        testTask.setDueDate(new Date()); // Today
    }

    @After
    public void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete test tasks first (due to foreign key constraints)
            if (testUser != null && testUser.getUserId() != null) {
                stmt.executeUpdate("DELETE FROM tasks WHERE user_id = " + testUser.getUserId());
                // Then delete test categories
                stmt.executeUpdate("DELETE FROM categories WHERE user_id = " + testUser.getUserId());
                // Finally delete the test user
                stmt.executeUpdate("DELETE FROM users WHERE user_id = " + testUser.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() {
        int result = taskDao.insert(testTask);
        assertTrue("Task insertion should succeed", result > 0);
        assertNotNull("Task ID should be set", testTask.getTaskId());

        // Verify task was inserted correctly
        Task found = taskDao.findById(testTask.getTaskId());
        assertNotNull("Should find the inserted task", found);
        assertEquals("Task title should match", testTask.getTitle(), found.getTitle());
        assertEquals("Task content should match", testTask.getContent(), found.getContent());
    }

    @Test
    public void testUpdate() {
        // First insert the task
        taskDao.insert(testTask);

        // Modify the task
        String updatedTitle = "Updated_" + UUID.randomUUID().toString().substring(0, 8);
        String updatedContent = "Updated content";
        int updatedPriority = 2;

        testTask.setTitle(updatedTitle);
        testTask.setContent(updatedContent);
        testTask.setPriority(updatedPriority);

        // Perform update
        int result = taskDao.update(testTask);
        assertTrue("Task update should succeed", result > 0);

        // Verify update
        Task updated = taskDao.findById(testTask.getTaskId());
        assertNotNull("Should find the updated task", updated);
        assertEquals("Task title should be updated", updatedTitle, updated.getTitle());
        assertEquals("Task content should be updated", updatedContent, updated.getContent());
        assertEquals("Task priority should be updated", updatedPriority, (int)updated.getPriority());
    }

    @Test
    public void testDelete() {
        // First insert the task
        taskDao.insert(testTask);
        Integer taskId = testTask.getTaskId();

        // Verify it exists
        assertNotNull("Task should exist before deletion", taskDao.findById(taskId));

        // Delete the task
        int result = taskDao.delete(taskId);
        assertTrue("Task deletion should succeed", result > 0);

        // Verify deletion
        assertNull("Task should no longer exist after deletion", taskDao.findById(taskId));
    }

    @Test
    public void testFindById() {
        // First insert the task
        taskDao.insert(testTask);

        // Find by ID
        Task found = taskDao.findById(testTask.getTaskId());

        // Verify
        assertNotNull("Should find task by ID", found);
        assertEquals("Task ID should match", testTask.getTaskId(), found.getTaskId());
        assertEquals("Task title should match", testTask.getTitle(), found.getTitle());
        assertEquals("Task user ID should match", testTask.getUserId(), found.getUserId());
    }

    @Test
    public void testFindByUserId() {
        // Insert the test task
        taskDao.insert(testTask);

        // Create and insert a second task for the same user
        Task secondTask = new Task();
        secondTask.setUserId(testUser.getUserId());
        secondTask.setCategoryId(testCategory.getCategoryId());
        secondTask.setTitle("TestTask2_" + UUID.randomUUID().toString().substring(0, 8));
        secondTask.setContent("Second test task");
        secondTask.setPriority(2);
        secondTask.setStatus(0);
        secondTask.setDueDate(new Date());
        taskDao.insert(secondTask);

        // Find tasks by user ID
        List<Task> tasks = taskDao.findByUserId(testUser.getUserId());

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertEquals("Should find exactly 2 tasks", 2, tasks.size());

        // Verify the tasks belong to the correct user
        for (Task task : tasks) {
            assertEquals("Task should belong to test user", testUser.getUserId(), task.getUserId());
        }
    }

    @Test
    public void testFindByCategoryId() {
        // Insert the test task
        taskDao.insert(testTask);

        // Find tasks by category ID
        List<Task> tasks = taskDao.findByCategoryId(testCategory.getCategoryId());

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertFalse("Tasks list should not be empty", tasks.isEmpty());

        // Verify the tasks belong to the correct category
        for (Task task : tasks) {
            assertEquals("Task should belong to test category", testCategory.getCategoryId(), task.getCategoryId());
        }
    }

    @Test
    public void testFindByStatus() {
        // Insert the test task with status 0
        taskDao.insert(testTask);

        // Find tasks by status
        List<Task> tasks = taskDao.findByStatus(testUser.getUserId(), 0);

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertFalse("Tasks list should not be empty", tasks.isEmpty());

        // Verify task status
        for (Task task : tasks) {
            assertEquals("Task status should match", 0, (int)task.getStatus());
        }
    }

    @Test
    public void testFindByPriority() {
        // Insert the test task with priority 1
        taskDao.insert(testTask);

        // Find tasks by priority
        List<Task> tasks = taskDao.findByPriority(testUser.getUserId(), 1);

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertFalse("Tasks list should not be empty", tasks.isEmpty());

        // Verify task priority
        for (Task task : tasks) {
            assertEquals("Task priority should match", 1, (int)task.getPriority());
        }
    }

    @Test
    public void testFindByDateRange() {
        // Insert the test task with today's date
        taskDao.insert(testTask);

        // Create date range (yesterday to tomorrow)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date startDate = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = cal.getTime();

        // Find tasks by date range
        List<Task> tasks = taskDao.findByDateRange(testUser.getUserId(), startDate, endDate);

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertFalse("Tasks list should not be empty", tasks.isEmpty());

        // Verify task belongs to user
        for (Task task : tasks) {
            assertEquals("Task should belong to test user", testUser.getUserId(), task.getUserId());
        }
    }

    @Test
    public void testUpdateStatus() {
        // Insert the test task with status 0
        taskDao.insert(testTask);

        // Update status to completed (1)
        int result = taskDao.updateStatus(testTask.getTaskId(), 1);
        assertTrue("Status update should succeed", result > 0);

        // Verify update
        Task updated = taskDao.findById(testTask.getTaskId());
        assertEquals("Task status should be updated", 1, (int)updated.getStatus());
    }

    @Test
    public void testSearch() {
        // Create a task with specific keywords in title and content
        Task searchableTask = new Task();
        searchableTask.setUserId(testUser.getUserId());
        searchableTask.setCategoryId(testCategory.getCategoryId());
        searchableTask.setTitle("SEARCHKEYWORD Task Title");
        searchableTask.setContent("This content contains SEARCHKEYWORD for testing");
        searchableTask.setPriority(1);
        searchableTask.setStatus(0);
        searchableTask.setDueDate(new Date());
        taskDao.insert(searchableTask);

        // Search for tasks with the keyword
        List<Task> tasks = taskDao.search(testUser.getUserId(), "SEARCHKEYWORD");

        // Verify
        assertNotNull("Should find tasks list", tasks);
        assertFalse("Tasks list should not be empty", tasks.isEmpty());

        // Verify at least one task contains the keyword
        boolean foundKeyword = false;
        for (Task task : tasks) {
            if (task.getTitle().contains("SEARCHKEYWORD") ||
                    (task.getContent() != null && task.getContent().contains("SEARCHKEYWORD"))) {
                foundKeyword = true;
                break;
            }
        }
        assertTrue("Should find task with the search keyword", foundKeyword);
    }

    @Test
    public void testCountByStatus() {
        // Insert task with status 0
        taskDao.insert(testTask); // Status 0

        // Create and insert a completed task
        Task completedTask = new Task();
        completedTask.setUserId(testUser.getUserId());
        completedTask.setCategoryId(testCategory.getCategoryId());
        completedTask.setTitle("Completed Task");
        completedTask.setContent("Completed task content");
        completedTask.setPriority(1);
        completedTask.setStatus(1); // Completed
        completedTask.setDueDate(new Date());
        taskDao.insert(completedTask);

        // Count tasks with status 0 (not completed)
        int count = taskDao.countByStatus(testUser.getUserId(), 0);

        // Verify
        assertTrue("Should find at least one task with status 0", count >= 1);

        // Count tasks with status 1 (completed)
        count = taskDao.countByStatus(testUser.getUserId(), 1);

        // Verify
        assertTrue("Should find at least one task with status 1", count >= 1);
    }
}