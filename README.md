# 任务管理系统

一个基于Java Web的个人任务管理系统，帮助用户高效管理和组织日常任务。

## 项目简介

本项目是一个基于Servlet、JDBC、JavaScript的Web应用程序，实现了用户注册登录、任务分类、任务创建、编辑、删除、查询、筛选等功能，帮助用户有效地管理个人任务。

## 技术栈

- **后端**：Java Servlet、JDBC、MySQL
- **前端**：HTML、CSS、JavaScript
- **构建工具**：Maven
- **数据库连接池**：Apache DBCP2
- **JSON处理**：Gson
- **单元测试**：JUnit、Mockito

## 功能特性

### 用户管理
- 用户注册与登录
- 会话管理
- 用户个人信息维护

### 分类管理
- 创建、更新、删除任务分类
- 分类列表查看

### 任务管理
- 创建、编辑、删除任务
- 设置任务优先级（低、中、高、紧急）
- 设置任务状态（未完成、进行中、已完成）
- 设置任务截止日期
- 按状态、优先级、截止日期查询任务
- 按关键词搜索任务
- 任务排序（按截止日期或优先级）

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── taskmanager/
│   │           ├── bean/          # 实体类
│   │           ├── dao/           # 数据访问层
│   │           ├── service/       # 业务逻辑层
│   │           ├── servlet/       # Servlet控制器
│   │           └── util/          # 工具类
│   ├── resources/                 # 资源文件
│   │   └── db.properties          # 数据库配置
│   └── webapp/                    # Web资源
│       ├── WEB-INF/
│       ├── login.html             # 登录页面
│       ├── register.html          # 注册页面
│       ├── tasks.html             # 任务管理页面
│       ├── script.js              # 登录注册脚本
│       ├── tasks.js               # 任务管理脚本
│       ├── styles.css             # 通用样式
│       └── tasks.css              # 任务页面样式
└── test/                          # 测试代码
    └── java/
        └── com/
            └── taskmanager/
                ├── dao/           # DAO层测试
                ├── service/       # Service层测试
                ├── servlet/       # Servlet层测试
                └── util/          # 工具类测试
```

## 数据库设计

### 用户表（users）
- user_id：用户ID，主键，自增
- username：用户名，唯一
- password：密码
- email：邮箱，唯一
- registration_date：注册日期
- last_login：最后登录时间
- status：用户状态（1:活跃，0:禁用）

### 分类表（categories）
- category_id：分类ID，主键，自增
- user_id：用户ID，外键
- category_name：分类名称
- description：分类描述
- created_date：创建日期

### 任务表（tasks）
- task_id：任务ID，主键，自增
- user_id：用户ID，外键
- category_id：分类ID，外键
- title：任务标题
- content：任务内容
- priority：优先级（1:低，2:中，3:高，4:紧急）
- due_date：截止日期
- status：任务状态（0:未完成，1:进行中，2:已完成）
- created_date：创建日期
- modified_date：修改日期

## 安装部署

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Tomcat 9.0+

### 步骤

1. **克隆项目**
```bash
git clone https://github.com/yourusername/task-management-system.git
cd task-management-system
```

2. **创建数据库**
```sql
CREATE DATABASE task_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库脚本**
```bash
mysql -u root -p task_management_system < task_management_db.sql
```

4. **配置数据库连接**

编辑 `src/main/resources/db.properties` 文件，修改数据库连接信息：
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/task_management_system?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
db.username=你的数据库用户名
db.password=你的数据库密码
```

5. **编译打包**
```bash
mvn clean package
```

6. **部署到Tomcat**

将生成的 `target/task-management-system.war` 文件复制到Tomcat的webapps目录下。

7. **启动Tomcat**
```bash
cd path/to/tomcat
bin/startup.sh  # Linux/Mac
bin\startup.bat  # Windows
```

8. **访问应用**

打开浏览器，访问 `http://localhost:8080/task-management-system`

## 使用说明

1. **注册/登录**
   - 首次使用需注册账号
   - 使用用户名和密码登录系统

2. **管理分类**
   - 点击"添加分类"按钮创建新分类
   - 点击分类可查看该分类下的所有任务

3. **管理任务**
   - 点击"新建任务"按钮创建新任务
   - 设置任务标题、描述、所属分类、截止日期和优先级
   - 使用过滤器筛选不同状态、优先级的任务
   - 使用搜索框搜索特定任务
   - 点击任务操作按钮完成、编辑或删除任务

## 运行测试

```bash
mvn test
```

## 开发调试

使用Maven Tomcat插件进行本地开发和调试：

```bash
mvn tomcat7:run
```

应用将在 `http://localhost:8090/taskmanager` 上运行。

## 项目特点

1. **分层架构**：采用DAO、Service、Servlet三层架构，代码结构清晰
2. **单元测试**：包含完整的单元测试，覆盖核心业务逻辑
3. **数据库连接池**：使用DBCP2提高数据库连接效率
4. **异常处理**：完善的异常处理机制，提供友好的用户提示
5. **前后端分离**：采用RESTful风格的API，前后端分离设计
6. **响应式设计**：移动端友好的界面设计

## 扩展与优化方向

1. **任务提醒功能**：增加邮件或浏览器通知提醒功能
2. **任务分享**：允许用户之间分享任务或协作完成
3. **数据导入导出**：支持任务数据的导入导出
4. **任务报表**：提供任务完成情况的统计和报表功能
5. **多主题支持**：允许用户自定义界面主题和风格
6. **接入第三方日历**：与Google日历等第三方系统集成
