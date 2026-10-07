# 学习工坊 LearnLab

用一套能跑起来的小系统，把 Vue 前端和 Java 后端里最常碰到的技术串在一起。页面是个人学习台：课程、笔记、任务、标签、评论、附件、仪表盘，另外有一个管理员停用账号的入口。

每个人只能看到自己的数据。管理员多出来的能力只有用户启停，不会看见别人的笔记。

## 用了什么技术

### 前端

| 技术 | 在项目里干什么 |
| --- | --- |
| Vue 3 | 页面组件，写法是 `script setup` |
| TypeScript | 给接口数据和组件参数加类型 |
| Vite | 开发服务器和打包。开发时把 `/api` 代理到 `localhost:8080` |
| Vue Router | 页面路由、懒加载，以及未登录不能进内部页面 |
| Pinia | 保存登录状态。token 同时放进 `localStorage`，刷新还在 |
| Axios | 发请求。拦截器自动带上 token，401 时退回登录页 |
| Element Plus | 表格、表单、分页、对话框，语言是中文 |
| Vitest | 前端单元测试，目前覆盖 Markdown 预览的转义 |

### 后端

| 技术 | 在项目里干什么 |
| --- | --- |
| Java 25 | 语言版本 |
| Spring Boot 4.1 | 应用骨架，内嵌 Tomcat，端口 8080 |
| Spring MVC | REST 接口。创建类接口返回 201 |
| Bean Validation | 请求体上的长度、邮箱、非空校验 |
| Spring Data JPA | 实体、仓库、一对多和多对多 |
| Flyway | 用 SQL 建表，不让 Hibernate 自动改表 |
| Spring Security | 登录态、角色、接口放行规则 |
| JWT（Nimbus，HS256） | 登录后发 token，后续请求放在 `Authorization` 头里 |
| BCrypt | 密码只存哈希 |
| Caffeine | 缓存标签名，新建标签时清掉缓存 |
| Spring AOP（`spring-boot-starter-aspectj`） | 记录每个接口花了多久 |
| `@Scheduled` | 每天早上扫描逾期任务 |
| Actuator | `/actuator/health` 健康检查 |
| springdoc OpenAPI | Swagger UI：`/swagger-ui.html` |

### 数据库和其他

| 技术 | 在项目里干什么 |
| --- | --- |
| H2 | 默认数据库，文件放在 `server/data/`，不装 MySQL 也能学 |
| MySQL 8.4 | 可选。`docker compose up -d` 后用 `mysql` 配置启动 |
| Docker Compose | 只负责拉起那一个 MySQL |
| Maven | 后端构建和测试 |
| JUnit 5 + MockMvc | 注册、登录、建课、校验失败、未登录访问 |

接口统一包一层 `{ success, code, message, data, fields }`。业务错误在 `GlobalExceptionHandler` 里变成同一种 JSON，页面只读 `message`。

阅读顺序在 [docs/学习指南.md](docs/学习指南.md)。表和关系在 [docs/数据库.md](docs/数据库.md)。

## 先跑起来

需要 JDK 17 或更高、Maven 3.9+、Node.js 20。

在项目根目录开两个终端：

```bat
启动后端.bat
```

```bat
启动前端.bat
```

打开 <http://localhost:5173>。

| 账号 | 密码 | 用来看什么 |
| --- | --- | --- |
| ada | learn123 | 两门课、笔记、任务和一条评论 |
| admin | admin123 | 左侧多出「用户管理」，可以停用账号 |

后端接口文档：<http://localhost:8080/swagger-ui.html>

H2 控制台：<http://localhost:8080/h2-console>。JDBC URL 用 `server/src/main/resources/application-dev.yml` 里那一条，用户名 `sa`，密码留空。应用开着才能连上。

第一次启动会下载 Maven 依赖和 npm 包，需要几分钟。

## 目录

```text
learnlab
├── 启动后端.bat
├── 启动前端.bat
├── docker-compose.yml
├── docs                      学习指南和表结构
├── server                    Spring Boot
│   └── src/main/java/com/learnlab
│       ├── course note task
│       ├── user security
│       └── common
└── web                       Vue 3
    └── src
        ├── views api stores router
        └── composables components
```

## 换成 MySQL

```bat
docker compose up -d
cd server
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

库名、账号、密码都是 `learnlab`。Flyway 用同一份 `V1__init_schema.sql`。H2 开了 MySQL 兼容模式，这份 SQL 两边都能执行。

## 测试

```bat
cd server
mvn test
```

```bat
cd web
npm test
```

演示数据在 `dev` 和 `mysql` 配置下，如果账号 `ada` 还没有课程，启动时会补一份。关掉它：把 `application.yml` 里的 `learnlab.seed.enabled` 改成 `false`。
