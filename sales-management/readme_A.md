# 后端 A 开发与联调说明

这份文档主要给组长、其他后端同学和前端同学看。内容以“怎么跑、怎么调、哪些能力可以复用”为主，不展开太多实现细节。

## 1. 当前完成内容

后端 A 这次先补了项目底座和系统管理相关能力：

- 登录认证：`/api/auth/login`
- JWT Token 鉴权
- BCrypt 密码加密
- 用户、角色、菜单、按钮权限
- 统一响应结构
- 全局异常处理
- TraceId
- 全局接口访问日志
- 关键操作日志
- 后端 A API 冒烟测试脚本
- MySQL 建表 SQL

当前默认本地跑 H2 内存库，方便先联调。MySQL 建表脚本已经有，但 MySQL profile 还没正式接入。

## 2. 运行方式

进入后端目录：

```powershell
cd D:\桌面\Projects\side-project\-1-main\sales-management
```

编译：

```powershell
mvn -DskipTests compile
```

打包：

```powershell
mvn -DskipTests package
```

启动：

```powershell
java -jar target\sales-management-0.0.1-SNAPSHOT-exec.jar
```

默认端口：

```text
8080
```

如果 `8080` 被占用，可以临时换端口：

```powershell
java -jar target\sales-management-0.0.1-SNAPSHOT-exec.jar --server.port=8090
```

查端口占用：

```powershell
netstat -ano | findstr :8080
```

结束占用进程：

```powershell
taskkill /PID <PID> /F
```

## 3. 默认账号

```text
用户名：admin
密码：123456
角色：ADMIN
```

密码不会明文存储，启动初始化时会通过 BCrypt 加密。

## 4. H2 和 MySQL

当前默认 H2：

```text
jdbc:h2:mem:sales_management
```

H2 控制台：

```text
http://localhost:8080/h2-console
```

登录信息：

```text
JDBC URL: jdbc:h2:mem:sales_management
User Name: sa
Password: 留空
```

MySQL 建表脚本：

```text
docs/sql/schema_backend_a.sql
```

执行示例：

```powershell
mysql -u root -p sales_management < docs\sql\schema_backend_a.sql
```

注意：现在只提供了 SQL，应用默认配置还没有切到 MySQL。

## 5. 其他模块需要知道的事

### 5.1 接口默认需要 Token

除了下面两个地址，其他接口默认都需要登录：

```text
/api/auth/login
/h2-console/**
```

调试业务接口时，先登录，再带请求头：

```http
Authorization: Bearer <accessToken>
```

### 5.2 可以复用的公共能力

| 能力 | 位置 | 说明 |
| --- | --- | --- |
| 统一返回 | `ApiResponse` | 返回 `code/message/data/traceId/timestamp` |
| 分页返回 | `PageResponse` | 列表接口统一分页格式 |
| 业务异常 | `BusinessException` | 业务错误直接抛这个 |
| 审计字段 | `BaseEntity` | `createdBy/createdAt/updatedBy/updatedAt/deletedFlag` |
| 当前用户 | `SecurityUtils` | 获取当前登录用户 ID、用户名 |
| 权限控制 | `@PreAuthorize` | 接口级权限控制 |
| 操作日志 | `@OperationLogRecord` | 记录关键写操作 |

### 5.3 权限码怎么配

如果其他模块要控制接口权限，可以这样写：

```java
@PreAuthorize("hasAuthority('customer:create')")
```

权限码需要同步给后端 A，写入 `sys_menu.permission_code`，再分配给角色。

### 5.4 操作日志怎么接

关键写操作可以加注解：

```java
@OperationLogRecord(moduleName = "客户管理", actionName = "新增客户", bizType = "CUSTOMER")
```

查询接口一般不建议加，避免日志太多。

### 5.5 TraceId 和接口访问日志

每个请求都会有 `traceId`。响应体和日志里都会带，排查问题时可以按这个 ID 找。

`ApiAccessLogFilter` 会自动记录 `/api/**` 的访问日志，其他模块不用额外接。

安全上不会记录：

- 请求体
- 响应体
- `Authorization`
- 密码

## 6. 登录和前端接入

登录接口：

```http
POST /api/auth/login
Content-Type: application/json
```

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

登录成功后，前端主要保存这些字段：

```text
data.accessToken
data.user
data.user.permissions
data.menus
```

后续请求加：

```http
Authorization: Bearer <accessToken>
```

按钮权限可以用：

```js
permissions.includes('system:user:create')
```

菜单可以优先用登录返回的 `menus`。

## 7. 统一响应格式

后端 A 新接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

常见状态：

| code | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 参数错误或业务参数不合法 |
| 401 | 未登录或 Token 失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 系统错误 |

分页返回：

```json
{
  "total": 1,
  "pageNum": 1,
  "pageSize": 10,
  "pages": 1,
  "list": []
}
```

## 8. 后端 A API 清单

### 8.1 认证

| 方法 | 地址 | 说明 | Token |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | 登录 | 不需要 |

### 8.2 用户管理

| 方法 | 地址 | 说明 | 权限码 |
| --- | --- | --- | --- |
| GET | `/api/users?pageNum=1&pageSize=10` | 用户分页 | `system:user:list` |
| POST | `/api/users` | 新增用户 | `system:user:create` |
| PUT | `/api/users/{id}` | 更新用户 | `system:user:update` |
| PUT | `/api/users/{id}/status?status=0` | 启用/停用用户 | `system:user:status` |

新增用户：

```json
{
  "username": "sales01",
  "password": "123456",
  "realName": "销售一号",
  "mobile": "13800138000",
  "email": "sales01@example.com",
  "status": 1,
  "roleIds": [1]
}
```

更新用户时，`password` 不传或为空表示不改密码。

### 8.3 角色管理

| 方法 | 地址 | 说明 | 权限码 |
| --- | --- | --- | --- |
| GET | `/api/roles` | 角色列表 | `system:role:list` |
| POST | `/api/roles` | 新增角色 | `system:role:create` |
| PUT | `/api/roles/{id}` | 更新角色 | `system:role:update` |
| PUT | `/api/roles/{id}/menus` | 分配菜单/按钮权限 | `system:role:menus` |

新增角色：

```json
{
  "roleCode": "SALES",
  "roleName": "销售人员",
  "status": 1,
  "remark": "销售业务角色"
}
```

分配权限：

```json
{
  "menuIds": [1, 2, 3, 4]
}
```

### 8.4 菜单和按钮权限

| 方法 | 地址 | 说明 | 权限码 |
| --- | --- | --- | --- |
| GET | `/api/menus` | 菜单树 | `system:menu:list` |
| POST | `/api/menus` | 新增菜单/按钮 | `system:menu:create` |
| PUT | `/api/menus/{id}` | 更新菜单/按钮 | `system:menu:update` |
| DELETE | `/api/menus/{id}` | 删除菜单/按钮 | `system:menu:delete` |

菜单示例：

```json
{
  "parentId": 0,
  "menuName": "客户管理",
  "menuType": "MENU",
  "path": "/customers",
  "component": "customer/CustomerListView",
  "permissionCode": "customer:list",
  "sortNo": 100,
  "status": 1
}
```

按钮示例：

```json
{
  "parentId": 1,
  "menuName": "新增客户",
  "menuType": "BUTTON",
  "permissionCode": "customer:create",
  "sortNo": 101,
  "status": 1
}
```

### 8.5 操作日志

| 方法 | 地址 | 说明 | 权限码 |
| --- | --- | --- | --- |
| GET | `/api/logs/operations?pageNum=1&pageSize=10` | 操作日志分页 | `system:log:list` |

可选参数：

```text
moduleName=用户管理
username=admin
pageNum=1
pageSize=10
```

## 9. 已接入业务操作日志的接口

目前这些接口会写 `operation_log`：

- `POST /api/auth/login`
- `POST /api/users`
- `PUT /api/users/{id}`
- `PUT /api/users/{id}/status`
- `POST /api/roles`
- `PUT /api/roles/{id}`
- `PUT /api/roles/{id}/menus`
- `POST /api/menus`
- `PUT /api/menus/{id}`
- `DELETE /api/menus/{id}`

记录内容包括模块、动作、业务类型、操作人、请求地址、IP、traceId、成功/失败、错误信息和耗时。

## 10. 测试脚本

脚本位置：

```text
scripts/test_backend_a_api.ps1
```

先启动后端：

```powershell
java -jar target\sales-management-0.0.1-SNAPSHOT-exec.jar
```

再开一个 PowerShell 执行：

```powershell
cd D:\桌面\Projects\side-project\-1-main\sales-management
powershell -ExecutionPolicy Bypass -File .\scripts\test_backend_a_api.ps1
```

如果后端不是 `8080`：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test_backend_a_api.ps1 -BaseUrl "http://localhost:8090"
```

脚本会验证登录、菜单、角色、用户、操作日志等后端 A 主要接口。

## 11. 本次迭代变更说明

这次迭代的目标是先把后端 A 负责的“技术底座 + 系统管理”跑通，保证项目能启动、能登录、能做基础权限管理，也方便其他模块后续接入。

### 11.1 本次创建的内容

| 类型 | 文件/目录 | 说明 |
| --- | --- | --- |
| API 文档 | `API_Docs_Backend_A.md` | 给前端 A 对接用，包含路径、参数、请求体、响应体和权限码 |
| 使用说明 | `readme_A.md` | 后端 A 运行、联调、交接说明 |
| 过程记录 | `process.md` | 记录本次开发做过的调整 |
| 测试脚本 | `scripts/test_backend_a_api.ps1` | 后端 A 冒烟测试脚本 |
| SQL | `docs/sql/schema_backend_a.sql` | MySQL 建表和初始化数据 |
| 认证鉴权 | `src/Main/java/com/yourcompany/sales/security/**` | JWT、Token 过滤器、登录态、安全配置 |
| 系统管理 | `src/Main/java/com/yourcompany/sales/modules/system/**` | 登录、用户、角色、菜单、操作日志相关代码 |
| 请求链路 | `TraceIdFilter.java` | 每个请求生成或透传 traceId |
| 接口日志 | `ApiAccessLogFilter.java` | 自动记录 `/api/**` 访问日志 |

### 11.2 本次调整的内容

| 文件 | 说明 |
| --- | --- |
| `pom.xml` | 补充 Spring Web、Security、JPA、AOP、H2、MySQL 等依赖和可运行 jar 打包配置 |
| `src/Main/resources/application.yml` | 配置默认端口 `8080`、H2、JPA、JWT |
| `ApiResponse.java` | 统一响应结构，带 `traceId` |
| `PageResponse.java` | 统一分页结构 |
| `BaseEntity.java` | 统一审计字段 |
| `BusinessException.java` | 业务异常基础类 |
| `GlobalExceptionHandler.java` | 全局异常统一处理 |
| `SecurityUtils.java` | 改为从当前登录态获取用户信息 |

### 11.3 对其他模块的影响

- 除 `/api/auth/login` 和 `/h2-console/**` 外，其他接口默认需要 Token。
- 其他模块如果调用 `SecurityUtils`，现在拿到的是当前登录用户，不再是固定用户 ID。
- 其他模块可以复用 `ApiResponse`、`PageResponse`、`BusinessException`、`BaseEntity`、`@OperationLogRecord`。
- 如果其他模块要加接口权限，需要把权限码同步给后端 A，写入 `sys_menu.permission_code` 后再分配角色。

## 12. 新增和修改的主要文件

### 12.1 后端 A 新增代码

| 路径 | 说明 |
| --- | --- |
| `src/Main/java/com/yourcompany/sales/security/LoginUser.java` | 当前登录用户信息 |
| `src/Main/java/com/yourcompany/sales/security/JwtTokenProvider.java` | JWT 生成和校验 |
| `src/Main/java/com/yourcompany/sales/security/JwtAuthenticationFilter.java` | Token 解析过滤器 |
| `src/Main/java/com/yourcompany/sales/security/SecurityConfig.java` | 安全配置 |
| `src/Main/java/com/yourcompany/sales/config/TraceIdFilter.java` | TraceId |
| `src/Main/java/com/yourcompany/sales/config/ApiAccessLogFilter.java` | 接口访问日志 |
| `src/Main/java/com/yourcompany/sales/modules/system/config/SystemDataInitializer.java` | 初始化 admin、角色、菜单权限 |
| `src/Main/java/com/yourcompany/sales/modules/system/controller/*Controller.java` | 系统管理接口 |
| `src/Main/java/com/yourcompany/sales/modules/system/service/*Service.java` | 系统管理业务逻辑 |
| `src/Main/java/com/yourcompany/sales/modules/system/repository/*Repository.java` | 系统管理数据访问 |
| `src/Main/java/com/yourcompany/sales/modules/system/entity/*.java` | 系统管理实体 |
| `src/Main/java/com/yourcompany/sales/modules/system/dto/*.java` | 系统管理 DTO |
| `src/Main/java/com/yourcompany/sales/modules/system/log/OperationLogRecord.java` | 操作日志注解 |
| `src/Main/java/com/yourcompany/sales/modules/system/log/OperationLogAspect.java` | 操作日志切面 |
| `scripts/test_backend_a_api.ps1` | API 冒烟测试 |
| `docs/sql/schema_backend_a.sql` | MySQL 建表 SQL |

### 12.2 公共基础设施调整

| 路径 | 说明 |
| --- | --- |
| `src/Main/java/com/yourcompany/sales/common/dto/ApiResponse.java` | 统一响应 |
| `src/Main/java/com/yourcompany/sales/common/dto/PageResponse.java` | 统一分页 |
| `src/Main/java/com/yourcompany/sales/common/dto/BaseEntity.java` | 审计字段 |
| `src/Main/java/com/yourcompany/sales/common/exception/BusinessException.java` | 业务异常 |
| `src/Main/java/com/yourcompany/sales/config/GlobalExceptionHandler.java` | 全局异常处理 |
| `src/Main/java/com/yourcompany/sales/utils/SecurityUtils.java` | 当前登录用户工具 |
| `src/Main/resources/application.yml` | 端口、H2、JPA、JWT 配置 |
| `pom.xml` | 依赖和打包配置 |

## 13. 目前还需要继续补的点

- MySQL profile
- 刷新 Token
- 数据权限规则，比如本人、部门、全部
- 权限码命名规范需要和其他后端统一
- 前端 A 完成登录和系统管理页面后，再按实际字段做一次小范围对齐
- 其他模块里还保留 `Result` 的接口，后续可以逐步统一到 `ApiResponse`
- 当前源码目录还是 `src/Main`，后续有时间再统一成 Maven 常规的 `src/main`

## 14. 和其他同学同步时可以这么说

给组长：

```text
后端 A 已经把登录、Token、RBAC、统一响应、异常、日志和系统管理接口先补起来了。
现在本地默认 H2 可以跑，MySQL SQL 已经准备好，但 MySQL profile 还没接。
后续需要确认权限码规范、数据权限和刷新 Token 要不要继续做。
```

给其他后端：

```text
业务接口默认需要 Token。
公共能力可以复用 ApiResponse、PageResponse、BusinessException、BaseEntity、SecurityUtils、@OperationLogRecord。
如果要加接口权限，把权限码同步给后端 A，写到 sys_menu 后再给角色授权。
```

给前端：

```text
登录接口是 POST /api/auth/login。
默认账号 admin / 123456。
登录后保存 accessToken、user.permissions、menus。
后续请求统一带 Authorization: Bearer <accessToken>。
```
