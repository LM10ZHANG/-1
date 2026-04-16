# Sales Management System - Backend B (Master Data)

本目录为软件工程实训项目的“后端B（主数据）”起步版本：提供客户/联系人/跟进、商品（SPU/SKU）与分类的基础 CRUD 接口骨架，并统一使用 `ApiResponse` 返回结构（含 `traceId`）。

## 运行方式

1. 使用默认配置启动（本地默认使用 H2，方便你先跑通接口）
2. 在项目目录执行：

```powershell
gradle bootRun
```

3. 打开 Swagger（接口文档）：

- http://localhost:8081/swagger-ui/index.html

## 接口一览（后端B）

客户相关：
- `POST /customers`
- `GET /customers`
- `PUT /customers/{id}`
- `GET /customers/{id}`
- `POST /customers/{id}/contacts`
- `POST /customers/{id}/followups`

商品相关：
- `GET /products/spu`
- `POST /products/spu`
- `GET /products/sku`
- `POST /products/sku`
- `PUT /products/sku/{id}`
- `GET /products/categories`

提示：
- 默认用 H2 演示环境启动时，会自动初始化两条 `product_category` 数据（便于你直接创建 SPU/SKU）。

## 审计字段与 traceId

- 审计字段：`created_by/created_at/updated_by/updated_at/deleted_flag`
- traceId：接口返回结构里会带上 `traceId`；如请求头提供 `X-Trace-Id` 会原样透传。

