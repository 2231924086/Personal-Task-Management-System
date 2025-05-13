package com.taskmanager.service;

import com.taskmanager.bean.Category;
import com.taskmanager.dao.CategoryDao;
import com.taskmanager.service.impl.CategoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
    private CategoryService categoryService;

    @Mock
    private CategoryDao categoryDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        categoryService = new CategoryServiceImpl();
        // 使用反射设置categoryDao
        try {
            java.lang.reflect.Field field = CategoryServiceImpl.class.getDeclaredField("categoryDao");
            field.setAccessible(true);
            field.set(categoryService, categoryDao);
        } catch (Exception e) {
            fail("设置categoryDao失败：" + e.getMessage());
        }
    }

    @Test
    public void testCreateCategory_Success() {
        // 准备测试数据
        Category category = new Category();
        category.setUserId(1);
        category.setCategoryName("测试分类");

        // 模拟DAO层行为
        when(categoryDao.findByUserIdAndName(1, "测试分类")).thenReturn(null);
        when(categoryDao.insert(any(Category.class))).thenReturn(1);

        // 执行测试
        boolean result = categoryService.createCategory(category);

        // 验证结果
        assertTrue(result);
        verify(categoryDao).insert(any(Category.class));
    }

    @Test
    public void testCreateCategory_DuplicateName() {
        // 准备测试数据
        Category category = new Category();
        category.setUserId(1);
        category.setCategoryName("测试分类");

        // 模拟DAO层行为
        when(categoryDao.isNameExists(1, "测试分类")).thenReturn(true);

        // 执行测试
        boolean result = categoryService.createCategory(category);

        // 验证结果
        assertFalse(result);
        verify(categoryDao, never()).insert(any(Category.class));
        verify(categoryDao).isNameExists(1, "测试分类");
    }

    @Test
    public void testUpdateCategory_Success() {
        // 准备测试数据
        Category category = new Category();
        category.setCategoryId(1);
        category.setUserId(1);
        category.setCategoryName("更新后的分类");

        // 模拟DAO层行为
        when(categoryDao.findById(1)).thenReturn(category);
        when(categoryDao.update(any(Category.class))).thenReturn(1);

        // 执行测试
        boolean result = categoryService.updateCategory(category);

        // 验证结果
        assertTrue(result);
        verify(categoryDao).update(category);
    }

    @Test
    public void testDeleteCategory_Success() {
        // 准备测试数据
        Integer categoryId = 1;

        // 模拟DAO层行为
        when(categoryDao.delete(categoryId)).thenReturn(1);

        // 执行测试
        boolean result = categoryService.deleteCategory(categoryId);

        // 验证结果
        assertTrue(result);
        verify(categoryDao).delete(categoryId);
    }

    @Test
    public void testGetCategoryById_Success() {
        // 准备测试数据
        Integer categoryId = 1;
        Category expectedCategory = new Category();
        expectedCategory.setCategoryId(categoryId);
        expectedCategory.setCategoryName("测试分类");

        // 模拟DAO层行为
        when(categoryDao.findById(categoryId)).thenReturn(expectedCategory);

        // 执行测试
        Category result = categoryService.getCategoryById(categoryId);

        // 验证结果
        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("测试分类", result.getCategoryName());
    }

    @Test
    public void testGetUserCategories_Success() {
        // 准备测试数据
        Integer userId = 1;
        List<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(new Category());
        expectedCategories.add(new Category());

        // 模拟DAO层行为
        when(categoryDao.findByUserId(userId)).thenReturn(expectedCategories);

        // 执行测试
        List<Category> result = categoryService.getUserCategories(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testIsCategoryNameExists_True() {
        // 准备测试数据
        Category existingCategory = new Category();
        existingCategory.setUserId(1);
        existingCategory.setCategoryName("测试分类");

        // 模拟DAO层行为
        when(categoryDao.isNameExists(1, "测试分类")).thenReturn(true);

        // 执行测试
        boolean result = categoryService.isCategoryNameExists(1, "测试分类");

        // 验证结果
        assertTrue(result);
        verify(categoryDao).isNameExists(1, "测试分类");
    }

    @Test
    public void testIsCategoryNameExists_False() {
        // 准备测试数据
        Integer userId = 1;
        String categoryName = "测试分类";

        // 模拟DAO层行为
        when(categoryDao.findByUserIdAndName(userId, categoryName)).thenReturn(null);

        // 执行测试
        boolean result = categoryService.isCategoryNameExists(userId, categoryName);

        // 验证结果
        assertFalse(result);
    }

    @Test
    public void testIsCategoryBelongsToUser_True() {
        // 准备测试数据
        Integer categoryId = 1;
        Integer userId = 1;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setUserId(userId);

        // 模拟DAO层行为
        when(categoryDao.findById(categoryId)).thenReturn(category);

        // 执行测试
        boolean result = categoryService.isCategoryBelongsToUser(categoryId, userId);

        // 验证结果
        assertTrue(result);
    }

    @Test
    public void testIsCategoryBelongsToUser_False() {
        // 准备测试数据
        Integer categoryId = 1;
        Integer userId = 1;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setUserId(2); // 不同的用户ID

        // 模拟DAO层行为
        when(categoryDao.findById(categoryId)).thenReturn(category);

        // 执行测试
        boolean result = categoryService.isCategoryBelongsToUser(categoryId, userId);

        // 验证结果
        assertFalse(result);
    }
}