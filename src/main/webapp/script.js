// 基本配置
const BASE_URL = '/taskmanager'; // 你的应用上下文路径
const API_URL = `${BASE_URL}/api/user`;

// 工具函数
function showError(message) {
    const errorElement = document.getElementById('error-message');
    errorElement.textContent = message;
    errorElement.classList.add('show');
    
    // 5秒后自动隐藏错误信息
    setTimeout(() => {
        errorElement.classList.remove('show');
    }, 5000);
}

function redirect(url) {
    window.location.href = url;
}

// 页面加载时执行
document.addEventListener('DOMContentLoaded', function() {
    // 登录表单处理
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value;
            
            if (!username || !password) {
                showError('请填写所有必填字段！');
                return;
            }
            
            // 发送登录请求
            fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 登录成功，跳转到任务页面
                    redirect(`${BASE_URL}/tasks.html`);
                } else {
                    // 登录失败，显示错误信息
                    showError(data.message || '登录失败，请检查用户名和密码');
                }
            })
            .catch(error => {
                console.error('登录请求出错:', error);
                showError('登录失败，请稍后再试');
            });
        });
    }
    
    // 注册表单处理
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        // 用户名实时检查
        const usernameInput = document.getElementById('username');
        const usernameCheck = document.getElementById('username-check');
        
        usernameInput.addEventListener('blur', function() {
            const username = this.value.trim();
            if (username.length < 3) {
                usernameCheck.textContent = '用户名长度不能少于3个字符';
                usernameCheck.className = 'check-message error';
                return;
            }
            
            // 检查用户名是否可用
            fetch(`${API_URL}/checkUsername?username=${encodeURIComponent(username)}`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        if (data.exists) {
                            usernameCheck.textContent = '用户名已存在';
                            usernameCheck.className = 'check-message error';
                        } else {
                            usernameCheck.textContent = '用户名可用';
                            usernameCheck.className = 'check-message success';
                        }
                    } else {
                        usernameCheck.textContent = '';
                    }
                })
                .catch(error => {
                    console.error('检查用户名请求出错:', error);
                    usernameCheck.textContent = '';
                });
        });
        
        // 邮箱检查
        const emailInput = document.getElementById('email');
        const emailCheck = document.getElementById('email-check');
        
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (!email) return;
            
            // 检查邮箱是否可用
            fetch(`${API_URL}/checkEmail?email=${encodeURIComponent(email)}`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        if (data.exists) {
                            emailCheck.textContent = '邮箱已被注册';
                            emailCheck.className = 'check-message error';
                        } else {
                            emailCheck.textContent = '邮箱可用';
                            emailCheck.className = 'check-message success';
                        }
                    } else {
                        emailCheck.textContent = '';
                    }
                })
                .catch(error => {
                    console.error('检查邮箱请求出错:', error);
                    emailCheck.textContent = '';
                });
        });
        
        // 表单提交
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            const email = document.getElementById('email').value.trim();
            
            // 验证表单
            if (!username || !password || !confirmPassword || !email) {
                showError('请填写所有必填字段！');
                return;
            }
            
            if (username.length < 3) {
                showError('用户名长度不能少于3个字符');
                return;
            }
            
            if (password.length < 6) {
                showError('密码长度不能少于6个字符');
                return;
            }
            
            if (password !== confirmPassword) {
                showError('两次输入的密码不一致');
                return;
            }
            
            // 发送注册请求
            fetch(`${API_URL}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}&email=${encodeURIComponent(email)}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 注册成功，跳转到登录页面
                    alert('注册成功，请登录！');
                    redirect(`${BASE_URL}/login.html`);
                } else {
                    // 注册失败，显示错误信息
                    showError(data.message || '注册失败，请稍后再试');
                }
            })
            .catch(error => {
                console.error('注册请求出错:', error);
                showError('注册失败，请稍后再试');
            });
        });
    }
});