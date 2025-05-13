package com.taskmanager.service;

import com.taskmanager.bean.Category;
import java.util.List;

public interface CategoryService {
    // 创建分类
    boolean createCategory(Category category);

    // 更新分类
    boolean updateCategory(Category category);

    // 删除分类
    boolean deleteCategory(Integer categoryId);

    // 获取分类信息
    Category getCategoryById(Integer categoryId);

    // 获取用户的所有分类
    List<Category> getUserCategories(Integer userId);

    // 获取所有分类
    List<Category> getAllCategories();

    // 检查分类名称是否已存在
    boolean isCategoryNameExists(Integer userId, String categoryName);

    // 验证分类是否属于指定用户
    boolean isCategoryBelongsToUser(Integer categoryId, Integer userId);
}