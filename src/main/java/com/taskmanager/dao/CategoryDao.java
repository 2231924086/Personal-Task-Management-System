package com.taskmanager.dao;

import com.taskmanager.bean.Category;
import java.util.List;

public interface CategoryDao {
    // 添加分类
    int insert(Category category);

    // 更新分类
    int update(Category category);

    // 删除分类
    int delete(Integer categoryId);

    // 根据ID查询分类
    Category findById(Integer categoryId);

    // 查询用户的所有分类
    List<Category> findByUserId(Integer userId);

    // 查询所有分类
    List<Category> findAll();

    // 检查分类名称是否已存在
    boolean isNameExists(Integer userId, String categoryName);

    // 根据用户ID和分类名称查询分类
    Category findByUserIdAndName(Integer userId, String categoryName);
}