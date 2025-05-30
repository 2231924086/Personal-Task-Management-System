// 基本配置
const BASE_URL = '/taskmanager'; // 应用上下文路径
const API_URL = {
    user: `${BASE_URL}/api/user`,
    task: `${BASE_URL}/api/task`,
    category: `${BASE_URL}/api/category`
};

// DOM元素
const elements = {
    usernameDisplay: document.getElementById('username-display'),
    logoutBtn: document.getElementById('logout-btn'),
    categoryList: document.getElementById('category-list'),
    addCategoryBtn: document.getElementById('add-category-btn'),
    taskList: document.getElementById('task-list'),
    addTaskBtn: document.getElementById('add-task-btn'),
    statusFilter: document.getElementById('status-filter'),
    searchInput: document.getElementById('search-input'),
    
    // 任务模态框
    taskModal: document.getElementById('task-modal'),
    taskForm: document.getElementById('task-form'),
    taskIdInput: document.getElementById('task-id'),
    taskNameInput: document.getElementById('task-name'),
    taskDescriptionInput: document.getElementById('task-description'),
    taskCategorySelect: document.getElementById('task-category'),
    taskDueDateInput: document.getElementById('task-due-date'),
    cancelTaskBtn: document.getElementById('cancel-task-btn'),
    
    // 分类模态框
    categoryModal: document.getElementById('category-modal'),
    categoryForm: document.getElementById('category-form'),
    categoryIdInput: document.getElementById('category-id'),
    categoryNameInput: document.getElementById('category-name'),
    cancelCategoryBtn: document.getElementById('cancel-category-btn'),

    priorityFilter: document.getElementById('priority-filter'),
    sortBy: document.getElementById('sort-by'),
    // 新增
    taskPriorityInput: document.getElementById('task-priority'),
};

// 关闭模态框按钮
document.querySelectorAll('.close-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        elements.taskModal.style.display = 'none';
        elements.categoryModal.style.display = 'none';
    });
});

// 工具函数
function showError(message) {
    alert(message); // 简单起见，使用alert
}

function formatDate(dateString) {
    if (!dateString) return '未设置';

    try {
        // 处理多种可能的日期格式
        let date;
        // 尝试处理时间戳（数字或字符串形式）
        if (!isNaN(Number(dateString))) {
            date = new Date(Number(dateString));
        }
        // 处理ISO格式的字符串
        else {
            date = new Date(dateString);
        }

        // 检查日期是否有效
        if (isNaN(date.getTime())) {
            return '日期无效';
        }

        return date.toLocaleDateString('zh-CN');
    } catch (e) {
        console.error('日期格式化错误:', e, dateString);
        return '日期错误';
    }
}

function getStatusText(status) {
    const statusMap = {
        0: '未完成',
        1: '进行中',
        2: '已完成'
    };
    return statusMap[status] || '未知';
}

// 获取用户信息
async function loadUserInfo() {
    try {
        const response = await fetch(`${API_URL.user}/info`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            elements.usernameDisplay.textContent = data.user.username;
        } else {
            // 未登录，跳转到登录页
            window.location.href = `${BASE_URL}/login.html`;
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
        showError('获取用户信息失败，请刷新重试');
    }
}

// 加载分类列表
async function loadCategories() {
    try {
        const response = await fetch(`${API_URL.category}/list`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            renderCategories(data.categories);
            updateTaskCategorySelect(data.categories);
        } else {
            elements.categoryList.innerHTML = '<li>加载分类失败</li>';
        }
    } catch (error) {
        console.error('加载分类失败:', error);
        elements.categoryList.innerHTML = '<li>加载分类失败，请刷新重试</li>';
    }
}

// 渲染分类列表
function renderCategories(categories) {
    if (!categories || categories.length === 0) {
        elements.categoryList.innerHTML = '<li class="empty-message">暂无分类</li>';
        return;
    }
    
    const html = categories.map(category => {
        return `<li data-id="${category.categoryId}" class="category-item">
            ${category.categoryName}
        </li>`;
    }).join('');
    
    elements.categoryList.innerHTML = html;
    
    // 添加点击事件
    document.querySelectorAll('.category-item').forEach(item => {
        item.addEventListener('click', function() {
            const categoryId = this.getAttribute('data-id');
            loadTasksByCategory(categoryId);
            
            // 更新选中状态
            document.querySelectorAll('.category-item').forEach(i => i.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

// 更新任务分类下拉框
function updateTaskCategorySelect(categories) {
    if (!categories || categories.length === 0) {
        elements.taskCategorySelect.innerHTML = '<option value="">暂无分类</option>';
        return;
    }
    
    const options = categories.map(category => {
        return `<option value="${category.categoryId}">${category.categoryName}</option>`;
    }).join('');
    
    elements.taskCategorySelect.innerHTML = '<option value="">请选择分类</option>' + options;
}

// 加载用户的所有任务
async function loadTasks() {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在加载任务...</div>';
        
        const response = await fetch(`${API_URL.task}/list`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">加载任务失败</div>';
        }
    } catch (error) {
        console.error('加载任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">加载任务失败，请刷新重试</div>';
    }
}

// 加载指定分类的任务
async function loadTasksByCategory(categoryId) {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在加载任务...</div>';
        
        const response = await fetch(`${API_URL.task}/category?categoryId=${categoryId}`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">加载任务失败</div>';
        }
    } catch (error) {
        console.error('加载分类任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">加载任务失败，请刷新重试</div>';
    }
}

// 渲染任务列表
function getPriorityText(priority) {
    const priorityMap = {
        1: '低',
        2: '中',
        3: '高',
        4: '紧急'
    };
    return priorityMap[priority] || '未知';
}
function renderTasks(tasks) {
    if (!tasks || tasks.length === 0) {
        elements.taskList.innerHTML = '<div class="empty-message">暂无任务</div>';
        return;
    }

    const html = tasks.map(task => {
        return `<div class="task-item" data-id="${task.taskId}">
            <div class="task-info">
                <div class="task-title">${task.title}</div>
                <div class="task-description">${task.description || '无描述'}</div>
                <div class="task-meta">
                    <span class="task-status status-${task.status}">${getStatusText(task.status)}</span>
                    <!-- 优先级显示 -->
                    <span class="priority priority-${task.priority}">
                        <span class="priority-icon priority-icon-${task.priority}"></span>
                        ${getPriorityText(task.priority)}
                    </span>
                    <span class="task-due-date">截止: ${formatDate(task.dueDate)}</span>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn-task-action complete" data-action="complete" title="标记完成">
                    ✓
                </button>
                <button class="btn-task-action edit" data-action="edit" title="编辑">
                    ✎
                </button>
                <button class="btn-task-action delete" data-action="delete" title="删除">
                    ✕
                </button>
            </div>
        </div>`;
    }).join('');

    elements.taskList.innerHTML = html;
    
    // 添加任务操作事件
    document.querySelectorAll('.btn-task-action').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const taskId = this.closest('.task-item').getAttribute('data-id');
            const action = this.getAttribute('data-action');
            
            switch (action) {
                case 'complete':
                    completeTask(taskId);
                    break;
                case 'edit':
                    openEditTaskModal(taskId);
                    break;
                case 'delete':
                    confirmDeleteTask(taskId);
                    break;
            }
        });
    });
}

// 标记任务完成
async function completeTask(taskId) {
    try {
        const response = await fetch(`${API_URL.task}/updateStatus`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `taskId=${taskId}&status=2`,
            credentials: 'include'
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 重新加载任务列表
            loadTasks();
        } else {
            showError(data.message || '更新任务状态失败');
        }
    } catch (error) {
        console.error('更新任务状态失败:', error);
        showError('更新任务状态失败，请稍后再试');
    }
}

// 打开编辑任务模态框
// 打开编辑任务模态框
async function openEditTaskModal(taskId) {
    try {
        // Show loading or disable form during fetch
        elements.taskModal.style.display = 'block';
        const submitButton = elements.taskForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        elements.taskForm.reset();

        // Fetch task data from server
        const response = await fetch(`${API_URL.task}/detail?taskId=${taskId}`, {
            credentials: 'include'
        });

        const data = await response.json();

        if (data.success) {
            const task = data.task;

            // Set form values with complete task data
            elements.taskIdInput.value = task.taskId;
            elements.taskNameInput.value = task.title;
            elements.taskDescriptionInput.value = task.description || task.content || '';
            elements.taskCategorySelect.value = task.categoryId;
            elements.taskPriorityInput.value = task.priority;

            // Format date for the input
            const dueDate = new Date(task.dueDate);
            const year = dueDate.getFullYear();
            const month = String(dueDate.getMonth() + 1).padStart(2, '0');
            const day = String(dueDate.getDate()).padStart(2, '0');
            elements.taskDueDateInput.value = `${year}-${month}-${day}`;
        } else {
            elements.taskModal.style.display = 'none';
            showError(data.message || '无法获取任务详情');
        }

        // Re-enable the submit button
        submitButton.disabled = false;
    } catch (error) {
        console.error('打开编辑模态框失败:', error);
        elements.taskModal.style.display = 'none';
        showError('操作失败，请稍后再试');
    }
}

// 确认删除任务
function confirmDeleteTask(taskId) {
    if (confirm('确定要删除这个任务吗？')) {
        deleteTask(taskId);
    }
}

// 删除任务
async function deleteTask(taskId) {
    try {
        const response = await fetch(`${API_URL.task}/delete`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `taskId=${taskId}`,
            credentials: 'include'
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 重新加载任务列表
            loadTasks();
        } else {
            showError(data.message || '删除任务失败');
        }
    } catch (error) {
        console.error('删除任务失败:', error);
        showError('删除任务失败，请稍后再试');
    }
}

// 打开添加任务模态框
function openAddTaskModal() {
    // 清空表单
    elements.taskForm.reset();
    elements.taskIdInput.value = '';
    
    // 设置今天的日期为默认值
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    elements.taskDueDateInput.value = `${year}-${month}-${day}`;
    
    // 打开模态框
    elements.taskModal.style.display = 'block';
}

// 处理任务表单提交
async function handleTaskFormSubmit(event) {
    event.preventDefault();

    const taskId = elements.taskIdInput.value.trim();
    const taskName = elements.taskNameInput.value.trim();
    const description = elements.taskDescriptionInput.value.trim();
    const categoryId = elements.taskCategorySelect.value;
    const dueDate = elements.taskDueDateInput.value;
    const priority = elements.taskPriorityInput.value; // 获取优先级

    if (!taskName || !categoryId || !dueDate || !priority) {
        showError('请填写所有必填字段');
        return;
    }

    // 构建请求参数
    const formData = new URLSearchParams();
    formData.append('taskName', taskName);
    formData.append('description', description);
    formData.append('categoryId', categoryId);
    formData.append('dueDate', dueDate);
    formData.append('priority', priority); // 添加优先级参数

    try {
        // 根据是否有taskId判断是创建还是更新
        const url = taskId
            ? `${API_URL.task}/update`
            : `${API_URL.task}/create`;

        if (taskId) {
            formData.append('taskId', taskId);
        }

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData.toString(),
            credentials: 'include'
        });

        const data = await response.json();

        if (data.success) {
            // 关闭模态框
            elements.taskModal.style.display = 'none';
            // 重新加载任务列表
            loadTasks();
        } else {
            showError(data.message || '操作失败');
        }
    } catch (error) {
        console.error('任务操作失败:', error);
        showError('操作失败，请稍后再试');
    }
}

// 打开添加分类模态框
function openAddCategoryModal() {
    // 清空表单
    elements.categoryForm.reset();
    elements.categoryIdInput.value = '';
    
    // 打开模态框
    elements.categoryModal.style.display = 'block';
}

// 处理分类表单提交
async function handleCategoryFormSubmit(event) {
    event.preventDefault();
    
    const categoryId = elements.categoryIdInput.value.trim();
    const categoryName = elements.categoryNameInput.value.trim();
    
    if (!categoryName) {
        showError('请填写分类名称');
        return;
    }
    
    // 构建请求参数
    const formData = new URLSearchParams();
    formData.append('categoryName', categoryName);
    
    try {
        // 根据是否有categoryId判断是创建还是更新
        const url = categoryId 
            ? `${API_URL.category}/update` 
            : `${API_URL.category}/create`;
            
        if (categoryId) {
            formData.append('categoryId', categoryId);
        }
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData.toString(),
            credentials: 'include'
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 关闭模态框
            elements.categoryModal.style.display = 'none';
            // 重新加载分类列表
            loadCategories();
        } else {
            showError(data.message || '操作失败');
        }
    } catch (error) {
        console.error('分类操作失败:', error);
        showError('操作失败，请稍后再试');
    }
}

// 退出登录
async function logout() {
    try {
        const response = await fetch(`${API_URL.user}/logout`, {
            method: 'POST',
            credentials: 'include'
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 跳转到登录页
            window.location.href = `${BASE_URL}/login.html`;
        } else {
            showError('退出失败，请刷新重试');
        }
    } catch (error) {
        console.error('退出失败:', error);
        showError('退出失败，请刷新重试');
    }
}

// 过滤器变化时重新加载任务
function handleFilterChange() {
    const status = elements.statusFilter.value;
    
    if (status === 'all') {
        loadTasks();
    } else {
        loadTasksByStatus(status);
    }
}

// 按状态加载任务
async function loadTasksByStatus(status) {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在加载任务...</div>';
        
        const response = await fetch(`${API_URL.task}/status?status=${status}`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">加载任务失败</div>';
        }
    } catch (error) {
        console.error('加载状态任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">加载任务失败，请刷新重试</div>';
    }
}

// 搜索任务
function handleSearch() {
    const keyword = elements.searchInput.value.trim();
    
    if (keyword) {
        searchTasks(keyword);
    } else {
        loadTasks();
    }
}

// 搜索任务
async function searchTasks(keyword) {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在搜索任务...</div>';
        
        const response = await fetch(`${API_URL.task}/search?keyword=${encodeURIComponent(keyword)}`, {
            credentials: 'include'
        });
        const data = await response.json();
        
        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">搜索任务失败</div>';
        }
    } catch (error) {
        console.error('搜索任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">搜索任务失败，请刷新重试</div>';
    }
}

// 事件监听
function setupEventListeners() {
    // 添加任务按钮
    elements.addTaskBtn.addEventListener('click', openAddTaskModal);
    
    // 取消任务按钮
    elements.cancelTaskBtn.addEventListener('click', () => {
        elements.taskModal.style.display = 'none';
    });
    
    // 任务表单提交
    elements.taskForm.addEventListener('submit', handleTaskFormSubmit);
    
    // 添加分类按钮
    elements.addCategoryBtn.addEventListener('click', openAddCategoryModal);
    
    // 取消分类按钮
    elements.cancelCategoryBtn.addEventListener('click', () => {
        elements.categoryModal.style.display = 'none';
    });
    
    // 分类表单提交
    elements.categoryForm.addEventListener('submit', handleCategoryFormSubmit);
    
    // 退出登录按钮
    elements.logoutBtn.addEventListener('click', logout);
    
    // 状态过滤器变化
    elements.statusFilter.addEventListener('change', handleFilterChange);
    
    // 搜索框输入
    elements.searchInput.addEventListener('input', () => {
        // 使用防抖处理，300ms后执行搜索
        clearTimeout(elements.searchInput.timer);
        elements.searchInput.timer = setTimeout(handleSearch, 300);
    });

    // 优先级筛选器变化
    elements.priorityFilter.addEventListener('change', handlePriorityFilterChange);

    // 排序方式变化
    elements.sortBy.addEventListener('change', handleSortChange);
    
    // 点击模态框外部关闭模态框
    window.addEventListener('click', function(e) {
        if (e.target === elements.taskModal) {
            elements.taskModal.style.display = 'none';
        }
        if (e.target === elements.categoryModal) {
            elements.categoryModal.style.display = 'none';
        }
    });
}

// 页面初始化
async function initialize() {
    // 设置事件监听
    setupEventListeners();
    
    // 加载用户信息
    await loadUserInfo();
    
    // 加载分类列表
    await loadCategories();
    
    // 加载任务列表
    await loadTasks();
}

// 按优先级筛选任务
async function loadTasksByPriority(priority) {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在加载任务...</div>';

        const response = await fetch(`${API_URL.task}/priority?priority=${priority}`, {
            credentials: 'include'
        });
        const data = await response.json();

        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">加载任务失败</div>';
        }
    } catch (error) {
        console.error('按优先级加载任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">加载任务失败，请刷新重试</div>';
    }
}

// 按优先级排序任务
async function loadTasksOrderByPriority() {
    try {
        elements.taskList.innerHTML = '<div class="loading-message">正在加载任务...</div>';

        // 默认降序（高优先级在前）
        const response = await fetch(`${API_URL.task}/orderByPriority`, {
            credentials: 'include'
        });
        const data = await response.json();

        if (data.success) {
            renderTasks(data.tasks);
        } else {
            elements.taskList.innerHTML = '<div class="empty-message">加载任务失败</div>';
        }
    } catch (error) {
        console.error('按优先级排序任务失败:', error);
        elements.taskList.innerHTML = '<div class="empty-message">加载任务失败，请刷新重试</div>';
    }
}

// 处理排序变化
function handleSortChange() {
    const sortBy = elements.sortBy.value;

    if (sortBy === 'dueDate') {
        loadTasks(); // 默认就是按截止日期排序
    } else if (sortBy === 'priority') {
        loadTasksOrderByPriority();
    }
}

// 处理优先级筛选变化
function handlePriorityFilterChange() {
    const priority = elements.priorityFilter.value;

    if (priority === 'all') {
        loadTasks();
    } else {
        loadTasksByPriority(priority);
    }
}

// 页面加载时执行初始化
document.addEventListener('DOMContentLoaded', initialize);