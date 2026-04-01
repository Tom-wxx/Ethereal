# Ethereal

一款高颜值社交 App，采用 Glassmorphism 设计语言，集成 AI 智能推荐与陪聊功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 · Spring Boot 3.2 · MyBatis-Plus · Sa-Token · Netty-SocketIO |
| App 端 | Vue 3 · Vant 4 · TypeScript · Pinia · Socket.IO Client |
| 管理后台 | Vue 3 · Element Plus · ECharts |
| AI | LangChain4j · 通义千问 (Qwen) |
| 存储 | MySQL 8 · Redis 7 · MinIO |

## 项目结构

```
├── ethereal-server/     # Spring Boot 后端（端口 8080）
├── ethereal-app/        # Vue3 移动端 H5（端口 5173）
├── ethereal-admin/      # Vue3 管理后台（端口 3001）
└── sql/                 # 数据库脚本
    ├── schema.sql       # 建表 DDL
    └── data.sql         # 测试数据
```

## 快速启动

### 1. 数据库初始化

用有权限的账号创建数据库并授权：

```sql
CREATE DATABASE IF NOT EXISTS ethereal DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON ethereal.* TO 'your_user'@'%';
FLUSH PRIVILEGES;
```

选中 `ethereal` 库后依次执行：

```bash
sql/schema.sql   # 建表
sql/data.sql     # 测试数据
```

### 2. 修改后端配置

编辑 `ethereal-server/src/main/resources/application.yml`，填写实际的数据库连接信息和 AI API Key：

```yaml
spring:
  datasource:
    url: jdbc:mysql://<host>:3306/ethereal?...
    username: <your_user>
    password: <your_password>

langchain4j:
  open-ai:
    api-key: <your_qwen_api_key>   # 通义千问 API Key
```

### 3. 启动服务

```bash
# 后端
cd ethereal-server && mvn spring-boot:run

# App 端
cd ethereal-app && npm install && npm run dev

# 管理后台
cd ethereal-admin && npm install && npm run dev
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 后端 API | http://localhost:8080 |
| API 文档 (Knife4j) | http://localhost:8080/doc.html |
| 移动端 App | http://localhost:5173 |
| 管理后台 | http://localhost:3001 |

> 管理员账号：`admin` / 密码：`admin123`

## 核心功能

- **探索** — 卡片式用户浏览，左划跳过，右划喜欢
- **喜欢** — 互相喜欢自动匹配，实时心跳通知
- **聊天** — 基于 WebSocket 的实时聊天
- **动态** — 发布图文动态，支持点赞评论
- **AI 功能** — Ethereal Concierge 智能陪聊 · 破冰话术生成 · 智能匹配评分
- **管理后台** — 用户管理 · 内容审核 · 数据统计

## 环境依赖

- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Redis 7+
- MinIO（可选，用于图片存储）
