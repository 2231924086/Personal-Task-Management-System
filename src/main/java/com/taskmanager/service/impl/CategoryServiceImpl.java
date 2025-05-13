package com.taskmanager.service.impl;

import com.taskmanager.bean.Category;
import com.taskmanager.dao.CategoryDao;
import com.taskmanager.dao.impl.CategoryDaoImpl;
import com.taskmanager.service.CategoryService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public boolean createCategory(Category category) {
        // 参数验证
        if (category == null || category.getUserId() == null
                || StringUtils.isBlank(category.getCategoryName())) {
            return false;
        }

        // 检查分类名称是否已存在
        if (isCategoryNameExists(category.getUserId(), category.getCategoryName())) {
            return false;
        }

        return categoryDao.insert(category) > 0;
    }

    @Override
    public boolean updateCategory(Category category) {
        // 参数验证
        if (category == null || category.getCategoryId() == null
                || category.getUserId() == null
                || StringUtils.isBlank(category.getCategoryName())) {
            return false;
        }

        // 验证分类是否属于该用户
        if (!isCategoryBelongsToUser(category.getCategoryId(), category.getUserId())) {
            return false;
        }

        // 检查新分类名称是否与其他分类重复
        Category existingCategory = categoryDao.findById(category.getCategoryId());
        if (existingCategory != null
                && !existingCategory.getCategoryName().equals(category.getCategoryName())
                && isCategoryNameExists(category.getUserId(), category.getCategoryName())) {
            return false;
        }

        return categoryDao.update(category) > 0;
    }

    @Override
    public boolean deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            return false;
        }
        return categoryDao.delete(categoryId) > 0;
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryDao.findById(categoryId);
    }

    @Override
    public List<Category> getUserCategories(Integer userId) {
        if (userId == null) {
            return null;
        }
        return categoryDao.findByUserId(userId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public boolean isCategoryNameExists(Integer userId, String categoryName) {
        if (userId == null || StringUtils.isBlank(categoryName)) {
            return false;
        }
        return categoryDao.isNameExists(userId, categoryName);
    }

    @Override
    public boolean isCategoryBelongsToUser(Integer categoryId, Integer userId) {
        if (categoryId == null || userId == null) {
            return false;
        }
        Category category = categoryDao.findById(categoryId);
        return category != null && category.getUserId().equals(userId);
    }
}