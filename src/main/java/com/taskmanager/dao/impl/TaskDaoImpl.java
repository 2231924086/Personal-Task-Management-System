package com.taskmanager.dao.impl;

import com.taskmanager.bean.Task;
import com.taskmanager.dao.TaskDao;
import com.taskmanager.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDaoImpl implements TaskDao {

    @Override
    public int insert(Task task) {
        String sql = "INSERT INTO tasks (user_id, category_id, title, content, priority, due_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, task.getUserId());
            pstmt.setInt(2, task.getCategoryId());
            pstmt.setString(3, task.getTitle());
            pstmt.setString(4, task.getContent());
            pstmt.setInt(5, task.getPriority());
            pstmt.setDate(6, new java.sql.Date(task.getDueDate().getTime()));
            pstmt.setInt(7, task.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTaskId(rs.getInt(1));
                    }
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int update(Task task) {
        String sql = "UPDATE tasks SET category_id=?, title=?, content=?, priority=?, due_date=?, status=? " +
                "WHERE task_id=? AND user_id=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, task.getCategoryId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getContent());
            pstmt.setInt(4, task.getPriority());
            pstmt.setDate(5, new java.sql.Date(task.getDueDate().getTime()));
            pstmt.setInt(6, task.getStatus());
            pstmt.setInt(7, task.getTaskId());
            pstmt.setInt(8, task.getUserId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(Integer taskId) {
        String sql = "DELETE FROM tasks WHERE task_id=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Task findById(Integer taskId) {
        String sql = "SELECT * FROM tasks WHERE task_id=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaskFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Task> findByUserId(Integer userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id=? ORDER BY due_date, priority DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public List<Task> findByCategoryId(Integer categoryId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE category_id=? ORDER BY due_date, priority DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public List<Task> findByStatus(Integer userId, Integer status) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id=? AND status=? ORDER BY due_date, priority DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public List<Task> findByPriority(Integer userId, Integer priority) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id=? AND priority=? ORDER BY due_date";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, priority);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public List<Task> findByDateRange(Integer userId, Date startDate, Date endDate) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id=? AND due_date BETWEEN ? AND ? ORDER BY due_date, priority DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public int updateStatus(Integer taskId, Integer status) {
        String sql = "UPDATE tasks SET status=? WHERE task_id=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, status);
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Task> search(Integer userId, String keyword) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id=? AND (title LIKE ? OR content LIKE ?) " +
                "ORDER BY due_date, priority DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public int countByStatus(Integer userId, Integer status) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE user_id=? AND status=?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Task extractTaskFromResultSet(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("task_id"));
        task.setUserId(rs.getInt("user_id"));
        task.setCategoryId(rs.getInt("category_id"));
        task.setTitle(rs.getString("title"));
        task.setContent(rs.getString("content"));
        task.setPriority(rs.getInt("priority"));
        task.setDueDate(rs.getDate("due_date"));
        task.setStatus(rs.getInt("status"));
        task.setCreatedDate(rs.getTimestamp("created_date"));
        task.setModifiedDate(rs.getTimestamp("modified_date"));
        return task;
    }
}