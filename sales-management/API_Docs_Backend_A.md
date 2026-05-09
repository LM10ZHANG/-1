# 后端 A API 对接文档

面向前端 A 使用。本文只写后端 A 目前已经提供的接口：登录、用户、角色、菜单/按钮权限、操作日志。

## 1. 公共约定

### 1.1 Base URL

本地默认：

```text
http://localhost:8080
```

如果后端临时改端口，例如 `8090`，把 Base URL 改成：

```text
http://localhost:8090
```

### 1.2 Content-Type

有请求体的接口统一使用：

```http
Content-Type: application/json
```

### 1.3 Token

除登录接口外，后端 A 其他接口都需要 Token：

```http
Authorization: Bearer <accessToken>
```

前端登录成功后建议保存：

```text
data.accessToken
data.user
data.user.permissions
data.menus
```

### 1.4 统一响应结构

所有后端 A 接口外层统一是：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "traceId": "请求链路ID",
  "timestamp": "2026-05-07 22:00:00"
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | number | 业务状态码，`200` 表示成功 |
| message | string | 提示信息 |
| data | any | 具体业务数据 |
| traceId | string | 链路追踪 ID，排查问题时使用 |
| timestamp | string | 响应时间 |

常见错误码：

| code | 场景 | 前端建议 |
| --- | --- | --- |
| 400 | 参数错误或业务参数不合法 | 提示 `message` |
| 401 | 未登录或 Token 失效 | 清理登录态，跳转登录页 |
| 403 | 权限不足 | 提示无权限 |
| 404 | 资源不存在 | 提示 `message` |
| 500 | 系统异常 | 提示系统异常，可带 `traceId` 找后端排查 |

### 1.5 分页响应结构

分页接口的 `data` 格式：

```json
{
  "total": 100,
  "pageNum": 1,
  "pageSize": 10,
  "pages": 10,
  "list": []
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| total | number | 总记录数 |
| pageNum | number | 当前页码，从 `1` 开始 |
| pageSize | number | 每页条数 |
| pages | number | 总页数 |
| list | array | 当前页数据 |

## 2. 认证接口

### 2.1 登录

```http
POST /api/auth/login
```

是否需要 Token：否

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 登录用户名 |
| password | string | 是 | 登录密码 |

请求示例：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应 `data`：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| accessToken | string | JWT Token |
| tokenType | string | 固定为 `Bearer` |
| expiresIn | number | Token 有效期，单位秒 |
| user | object | 当前用户信息，结构见 `UserResponse` |
| menus | array | 当前用户可访问菜单树，结构见 `MenuResponse[]` |

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "mobile": null,
      "email": null,
      "status": 1,
      "roles": ["ADMIN"],
      "permissions": [
        "system:user:list",
        "system:user:create",
        "system:role:list",
        "system:menu:list",
        "system:log:list"
      ]
    },
    "menus": [
      {
        "id": 1,
        "parentId": 0,
        "menuName": "系统管理",
        "menuType": "MENU",
        "path": "/system",
        "component": null,
        "permissionCode": null,
        "sortNo": 1,
        "status": 1,
        "children": []
      }
    ]
  },
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

前端处理建议：

- `accessToken` 用于后续接口请求头。
- `user.permissions` 用于按钮权限判断。
- `menus` 可用于系统菜单渲染。

## 3. 用户管理

### UserResponse 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 用户 ID |
| username | string | 用户名 |
| realName | string | 真实姓名 |
| mobile | string | 手机号 |
| email | string | 邮箱 |
| status | number | `1` 启用，`0` 停用 |
| roles | string[] | 角色编码列表 |
| permissions | string[] | 权限码列表 |

### 3.1 用户分页

```http
GET /api/users
```

是否需要 Token：是

权限码：`system:user:list`

Query 参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| pageNum | number | 否 | 1 | 页码 |
| pageSize | number | 否 | 10 | 每页条数 |

请求示例：

```http
GET /api/users?pageNum=1&pageSize=10
Authorization: Bearer <accessToken>
```

响应 `data`：`PageResponse<UserResponse>`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "mobile": null,
        "email": null,
        "status": 1,
        "roles": ["ADMIN"],
        "permissions": ["system:user:list"]
      }
    ]
  },
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

### 3.2 新增用户

```http
POST /api/users
```

是否需要 Token：是

权限码：`system:user:create`

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名，不能重复 |
| password | string | 是 | 初始密码，后端会 BCrypt 加密 |
| realName | string | 否 | 真实姓名 |
| mobile | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| status | number | 否 | `1` 启用，`0` 停用；默认 `1` |
| roleIds | number[] | 否 | 角色 ID 列表 |

请求示例：

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

响应 `data`：`UserResponse`

### 3.3 更新用户

```http
PUT /api/users/{id}
```

是否需要 Token：是

权限码：`system:user:update`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 用户 ID |

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名 |
| password | string | 否 | 新密码；不传或空字符串表示不修改密码 |
| realName | string | 否 | 真实姓名 |
| mobile | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| status | number | 否 | `1` 启用，`0` 停用 |
| roleIds | number[] | 否 | 角色 ID 列表 |

请求示例：

```json
{
  "username": "sales01",
  "password": "",
  "realName": "销售一号",
  "mobile": "13800138000",
  "email": "sales01@example.com",
  "status": 1,
  "roleIds": [1]
}
```

响应 `data`：`UserResponse`

### 3.4 修改用户状态

```http
PUT /api/users/{id}/status
```

是否需要 Token：是

权限码：`system:user:status`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 用户 ID |

Query 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| status | number | 是 | `1` 启用，`0` 停用 |

请求示例：

```http
PUT /api/users/2/status?status=0
Authorization: Bearer <accessToken>
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

## 4. 角色管理

### RoleResponse 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 角色 ID |
| roleCode | string | 角色编码 |
| roleName | string | 角色名称 |
| status | number | `1` 启用，`0` 停用 |
| remark | string | 备注 |
| menuIds | number[] | 该角色已拥有的菜单/按钮 ID |

### 4.1 角色列表

```http
GET /api/roles
```

是否需要 Token：是

权限码：`system:role:list`

响应 `data`：`RoleResponse[]`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "roleCode": "ADMIN",
      "roleName": "系统管理员",
      "status": 1,
      "remark": "系统初始化角色",
      "menuIds": [1, 2, 3]
    }
  ],
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

### 4.2 新增角色

```http
POST /api/roles
```

是否需要 Token：是

权限码：`system:role:create`

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| roleCode | string | 是 | 角色编码，不能重复 |
| roleName | string | 是 | 角色名称 |
| status | number | 否 | `1` 启用，`0` 停用；默认 `1` |
| remark | string | 否 | 备注 |

请求示例：

```json
{
  "roleCode": "SALES",
  "roleName": "销售人员",
  "status": 1,
  "remark": "销售业务角色"
}
```

响应 `data`：`RoleResponse`

### 4.3 更新角色

```http
PUT /api/roles/{id}
```

是否需要 Token：是

权限码：`system:role:update`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 角色 ID |

请求 body：同新增角色。

响应 `data`：`RoleResponse`

### 4.4 分配角色菜单/按钮权限

```http
PUT /api/roles/{id}/menus
```

是否需要 Token：是

权限码：`system:role:menus`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 角色 ID |

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| menuIds | number[] | 否 | 菜单/按钮 ID 列表；空数组表示清空权限 |

请求示例：

```json
{
  "menuIds": [1, 2, 3, 4]
}
```

响应 `data`：`null`

## 5. 菜单和按钮权限

### MenuResponse 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 菜单/按钮 ID |
| parentId | number | 父级 ID，根节点为 `0` |
| menuName | string | 菜单或按钮名称 |
| menuType | string | `MENU` 菜单，`BUTTON` 按钮 |
| path | string | 前端路由路径 |
| component | string | 前端组件路径 |
| permissionCode | string | 权限码 |
| sortNo | number | 排序号 |
| status | number | `1` 启用，`0` 停用 |
| children | MenuResponse[] | 子菜单/按钮 |

### 5.1 菜单树

```http
GET /api/menus
```

是否需要 Token：是

权限码：`system:menu:list`

响应 `data`：`MenuResponse[]`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "系统管理",
      "menuType": "MENU",
      "path": "/system",
      "component": null,
      "permissionCode": null,
      "sortNo": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "menuName": "用户管理",
          "menuType": "MENU",
          "path": "/system/users",
          "component": null,
          "permissionCode": "system:user:list",
          "sortNo": 10,
          "status": 1,
          "children": []
        }
      ]
    }
  ],
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

### 5.2 新增菜单/按钮

```http
POST /api/menus
```

是否需要 Token：是

权限码：`system:menu:create`

请求 body：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| parentId | number | 否 | 父级 ID，默认 `0` |
| menuName | string | 是 | 菜单或按钮名称 |
| menuType | string | 是 | `MENU` 或 `BUTTON` |
| path | string | 否 | 前端路由路径，按钮一般为空 |
| component | string | 否 | 前端组件路径，按钮一般为空 |
| permissionCode | string | 否 | 权限码 |
| sortNo | number | 否 | 排序号，默认 `0` |
| status | number | 否 | `1` 启用，`0` 停用；默认 `1` |

新增菜单示例：

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

新增按钮示例：

```json
{
  "parentId": 1,
  "menuName": "新增客户",
  "menuType": "BUTTON",
  "path": null,
  "component": null,
  "permissionCode": "customer:create",
  "sortNo": 101,
  "status": 1
}
```

响应 `data`：`MenuResponse`

### 5.3 更新菜单/按钮

```http
PUT /api/menus/{id}
```

是否需要 Token：是

权限码：`system:menu:update`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 菜单/按钮 ID |

请求 body：同新增菜单/按钮。

响应 `data`：`MenuResponse`

### 5.4 删除菜单/按钮

```http
DELETE /api/menus/{id}
```

是否需要 Token：是

权限码：`system:menu:delete`

Path 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | number | 是 | 菜单/按钮 ID |

响应 `data`：`null`

说明：当前是逻辑删除。

## 6. 操作日志

### OperationLogResponse 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | number | 日志 ID |
| moduleName | string | 模块名称 |
| actionName | string | 操作名称 |
| bizType | string | 业务类型 |
| operatorUserId | number | 操作人用户 ID |
| operatorUsername | string | 操作用户名 |
| requestMethod | string | 请求方法 |
| requestUri | string | 请求路径 |
| clientIp | string | 客户端 IP |
| traceId | string | 链路追踪 ID |
| successFlag | number | `1` 成功，`0` 失败 |
| errorMessage | string | 错误信息 |
| costMs | number | 耗时，单位毫秒 |
| createdAt | string | 创建时间 |

### 6.1 操作日志分页

```http
GET /api/logs/operations
```

是否需要 Token：是

权限码：`system:log:list`

Query 参数：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| moduleName | string | 否 | 无 | 模块名称，精确匹配 |
| username | string | 否 | 无 | 操作用户名，模糊匹配 |
| pageNum | number | 否 | 1 | 页码 |
| pageSize | number | 否 | 10 | 每页条数 |

请求示例：

```http
GET /api/logs/operations?moduleName=用户管理&username=admin&pageNum=1&pageSize=10
Authorization: Bearer <accessToken>
```

响应 `data`：`PageResponse<OperationLogResponse>`

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1,
    "list": [
      {
        "id": 1,
        "moduleName": "用户管理",
        "actionName": "新增用户",
        "bizType": "SYS_USER",
        "operatorUserId": 1,
        "operatorUsername": "admin",
        "requestMethod": "POST",
        "requestUri": "/api/users",
        "clientIp": "127.0.0.1",
        "traceId": "xxx",
        "successFlag": 1,
        "errorMessage": null,
        "costMs": 25,
        "createdAt": "2026-05-07 22:00:00"
      }
    ]
  },
  "traceId": "xxx",
  "timestamp": "2026-05-07 22:00:00"
}
```

## 7. 权限码汇总

| 权限码 | 说明 |
| --- | --- |
| `system:user:list` | 用户分页 |
| `system:user:create` | 新增用户 |
| `system:user:update` | 更新用户 |
| `system:user:status` | 修改用户状态 |
| `system:role:list` | 角色列表 |
| `system:role:create` | 新增角色 |
| `system:role:update` | 更新角色 |
| `system:role:menus` | 分配角色菜单权限 |
| `system:menu:list` | 菜单树 |
| `system:menu:create` | 新增菜单/按钮 |
| `system:menu:update` | 更新菜单/按钮 |
| `system:menu:delete` | 删除菜单/按钮 |
| `system:log:list` | 操作日志分页 |

## 8. 前端接入顺序

建议前端 A 按这个顺序接：

1. 登录页：`POST /api/auth/login`
2. 保存 `accessToken`
3. 请求拦截器统一加 `Authorization`
4. 根据 `user.permissions` 做按钮权限
5. 根据 `menus` 或 `GET /api/menus` 渲染系统菜单
6. 接用户管理页面
7. 接角色管理页面
8. 接菜单/按钮权限页面
9. 接操作日志页面

## 9. PowerShell 登录示例

```powershell
$body = @{
  username = "admin"
  password = "123456"
} | ConvertTo-Json

$res = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/auth/login" `
  -ContentType "application/json" `
  -Body $body

$token = $res.data.accessToken
$headers = @{ Authorization = "Bearer $token" }

Invoke-RestMethod -Method Get `
  -Uri "http://localhost:8080/api/users?pageNum=1&pageSize=10" `
  -Headers $headers
```
