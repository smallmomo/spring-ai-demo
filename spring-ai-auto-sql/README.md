# Spring AI Auto SQL

一个基于Spring AI和MyBatis-Plus的自动SQL生成系统，能够根据自然语言问题自动生成SQL查询语句并执行。

## 功能特点

- 🤖 使用Spring AI将自然语言问题转换为SQL查询
- 🔒 严格的SQL安全验证，只允许SELECT查询
- 📊 自动检测数据库表结构和字段信息
- 🚀 RESTful API接口，易于集成
- 🧪 完整的单元测试
- 💻 使用MyBatis-Plus作为ORM框架

## 技术栈

- **后端框架**: Spring Boot 3.2.4
- **AI框架**: Spring AI 1.0.0
- **数据库ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL
- **构建工具**: Maven

## 系统架构

### 核心组件

1. **数据库元数据管理服务** (`DatabaseMetadataService`)
   - 自动检测数据库表结构
   - 获取表、字段、数据类型等信息

2. **Prompt构造器** (`PromptBuilder`)
   - 构造AI提示信息
   - 包含数据库结构和用户问题

3. **SQL生成服务** (`SqlGenerationService`)
   - 使用Spring AI生成SQL查询
   - 基于数据库结构和用户问题

4. **SQL验证服务** (`SqlValidationService`)
   - 验证SQL安全性
   - 只允许SELECT查询
   - 过滤危险关键词

5. **查询执行服务** (`QueryExecutionService`)
   - 执行生成的SQL查询
   - 返回查询结果

### API接口

#### 1. 查询接口
```
POST /api/query
Content-Type: application/json

Request Body:
{
  "question": "查找所有订单金额大于100的用户信息",
  "databaseSchema": "可选，如果不提供则使用系统自动检测的schema"
}

Response Body:
{
  "success": true,
  "sql": "SELECT u.*, o.* FROM users u JOIN orders o ON u.id = o.user_id WHERE o.amount > ?",
  "parameters": [100],
  "result": [...],
  "message": "查询成功",
  "execution_time": "120ms"
}
```

#### 2. 获取数据库结构接口
```
GET /api/schema

Response Body:
[
  {
    "tableName": "users",
    "tableComment": "用户表",
    "columns": [
      {
        "columnName": "id",
        "dataType": "INT",
        "columnComment": "用户ID",
        "isPrimaryKey": true,
        "isNullable": false
      }
    ]
  }
]
```

## 使用方法

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 配置数据库
在 `application.yml` 中配置数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 配置AI服务
在 `application.yml` 中配置Azure OpenAI：
```yaml
spring:
  ai:
    azure:
      openai:
        api-key: ${AZURE_OPENAI_API_KEY:your-api-key}
        endpoint: ${AZURE_OPENAI_ENDPOINT:your-endpoint}
```

### 4. 测试接口
使用curl测试查询接口：
```bash
curl -X POST http://localhost:8089/api/query \
  -H "Content-Type: application/json" \
  -d '{"question": "查找所有用户"}'
```

## 安全特性

- 只允许SELECT查询语句
- 过滤危险的SQL关键词
- 参数化查询防止SQL注入
- 严格的输入验证

## 扩展功能

- 缓存数据库元数据
- 支持手动配置数据库结构
- 更复杂的参数提取
- 日志记录和监控

## 依赖

- **Spring Boot**: 3.2.4
- **Spring AI**: 1.0.0
- **MyBatis-Plus**: 3.5.3.1
- **MySQL Connector**: 8.0.33
- **JUnit 5**: 5.9.3
- **Lombok**: 1.18.28

## 开发者

- 基于 Spring AI 框架
- 使用 Maven 构建工具
- 支持单元测试