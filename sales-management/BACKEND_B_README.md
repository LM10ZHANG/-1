# 销售管理系统 - 后端 B 主数据模块

> 贡献者：后端 B
> 模块范围：客户管理 / 商品主数据（SPU、SKU、分类）/ 数据字典
> 配合依据：《销售管理系统需求文档》 + 《分工文档 4.2》

---

## 一、本次提交内容

### 1. 新增 Java 源代码（零修改后端 C 现有文件）

所有新增代码严格沿用后端 C 已搭好的包路径 `com.yourcompany.sales.*`，直接放进 C 的目录 `sales-management/src/Main/java/`，不修改任何现有文件。

```
sales-management/src/Main/java/com/yourcompany/sales/modules/
├── customer/                     # 客户中心
│   ├── entity/
│   │   ├── Customer.java
│   │   ├── CustomerContact.java
│   │   └── CustomerFollowup.java
│   ├── dto/
│   │   ├── CustomerCreateRequest.java
│   │   ├── CustomerUpdateRequest.java
│   │   ├── CustomerQueryRequest.java
│   │   ├── CustomerResponse.java
│   │   ├── ContactRequest.java
│   │   ├── ContactResponse.java
│   │   ├── FollowupRequest.java
│   │   └── FollowupResponse.java
│   ├── repository/
│   │   ├── CustomerRepository.java
│   │   ├── CustomerContactRepository.java
│   │   └── CustomerFollowupRepository.java
│   ├── service/
│   │   ├── CustomerService.java
│   │   ├── CustomerContactService.java
│   │   ├── CustomerFollowupService.java
│   │   └── CustomerQueryService.java   # ⭐ 给后端 C 用
│   └── controller/
│       └── CustomerController.java
│
├── product/                      # 商品主数据
│   ├── entity/           (ProductSpu / ProductSku / ProductCategory)
│   ├── dto/              (SpuRequest/Response/Query, SkuRequest/Response/Query, SkuPricingVo, CategoryRequest, CategoryNode)
│   ├── repository/       (ProductSpuRepository / ProductSkuRepository / ProductCategoryRepository)
│   ├── service/          (SpuService / SkuService / CategoryService / SkuQueryService ⭐)
│   └── controller/       (SpuController / SkuController / CategoryController)
│
└── dict/                         # 数据字典
    ├── entity/           (SysDict / SysDictItem)
    ├── dto/              (DictRequest / DictItemRequest / DictItemVo)
    ├── repository/       (SysDictRepository / SysDictItemRepository)
    ├── service/          (DictService)
    └── controller/       (DictController)
```

### 2. 新增数据库建表脚本

```
sales-management/docs/sql/schema_backend_b.sql
```

内含 8 张表的完整 DDL + 初始字典数据：
`customer`、`customer_contact`、`customer_followup`、`product_spu`、`product_sku`、`product_category`、`sys_dict`、`sys_dict_item`。

---

## 二、对外 API 一览

| 模块    | 方法   | 路径                                           | 说明                        |
| ------- | ------ | ---------------------------------------------- | --------------------------- |
| 客户    | POST   | `/api/customers`                               | 新增客户                    |
| 客户    | PUT    | `/api/customers/{id}`                          | 更新客户                    |
| 客户    | GET    | `/api/customers/{id}`                          | 查询详情（含联系人）        |
| 客户    | GET    | `/api/customers`                               | 分页查询                    |
| 客户    | PUT    | `/api/customers/{id}/status`                   | 启用 / 禁用                 |
| 客户    | DELETE | `/api/customers/{id}`                          | 逻辑删除                    |
| 联系人  | GET    | `/api/customers/{id}/contacts`                 | 联系人列表                  |
| 联系人  | POST   | `/api/customers/{id}/contacts`                 | 新增联系人（主联系人唯一）  |
| 联系人  | PUT    | `/api/customers/{id}/contacts/{cid}`           | 更新联系人                  |
| 联系人  | DELETE | `/api/customers/{id}/contacts/{cid}`           | 删除联系人                  |
| 跟进    | GET    | `/api/customers/{id}/followups`                | 跟进记录分页                |
| 跟进    | POST   | `/api/customers/{id}/followups`                | 新增跟进（自动同步客户状态）|
| SPU     | GET/POST/PUT | `/api/products/spu[/{id}]`               | SPU 管理                    |
| SKU     | GET/POST/PUT | `/api/products/sku[/{id}]`               | SKU 管理                    |
| 分类    | GET    | `/api/products/categories`                     | 完整分类树                  |
| 字典    | GET    | `/api/dicts/{code}/items`                      | **前端下拉专用接口**        |
| 字典    | GET/POST/PUT/DELETE | `/api/dicts/**`                   | 字典管理                    |

所有响应统一走 `com.yourcompany.sales.common.dto.ApiResponse`；所有异常统一走 C 已有的 `GlobalExceptionHandler`。

---

## 三、与后端 C 的集成说明（关键！）

后端 C 在 `QuoteService` 与 `OrderService` 里有两处 TODO 需要调用后端 B 的服务：

### 3.1 客户有效性校验 (替换 `QuoteService#createQuote` 中 "1. 校验客户是否存在且状态正常" 的 TODO)

```java
// 在 QuoteService / OrderService 里注入
private final CustomerQueryService customerQueryService;

// 创建报价/订单时调用
customerQueryService.requireActive(request.getCustomerId());
```

- 客户不存在 / 已逻辑删除 → `BusinessException.notFound`
- 客户已禁用 → `BusinessException.operationForbidden("客户已被禁用...")`

### 3.2 SKU 定价快照 (替换 `QuoteService#createQuote` 里 `skuNameSnapshot / originUnitPrice / taxRate` 的硬编码)

```java
// 注入
private final SkuQueryService skuQueryService;

// 创建明细时调用
SkuPricingVo pricing = skuQueryService.getForPricing(itemRequest.getSkuId());
item.setSkuNameSnapshot(pricing.getSkuName());
item.setOriginUnitPrice(pricing.getSalePrice());
item.setTaxRate(pricing.getTaxRate());
```

- SKU 不存在 / 已逻辑删除 → `BusinessException.notFound`
- SKU 已停用 → `BusinessException.operationForbidden`

### 3.3 历史单据回显（订单/报价查询时可能遇到已停用的 SKU）

```java
skuQueryService.getSnapshot(skuId); // Optional，允许返回已停用/已删除
```

**说明：后端 B 没有修改 C 的任何源文件**，只需要在 C 的业务里把上面两个 Bean 注入进去即可。

---

## 四、业务规则一览（已在代码中实现）

1. **客户编码唯一**（`customer_code` 数据库层加了 `UNIQUE KEY`）。
2. **客户名称不可重复**（Service 层校验 `existsByCustomerNameAndDeletedFlag`）。
3. **主联系人最多只能有一个**（保存主联系人时 `clearPrimaryExcept` 会把其它主联系人置为非主）。
4. **SKU 停用后不能再被新的报价/订单引用**（`SkuQueryService#getForPricing` 拦截）。
5. **客户禁用后不能新建报价/订单**（`CustomerQueryService#requireActive` 拦截）。
6. **字典下拉只返回启用项**（`DictService#listItemsByCode` 过滤 `status == 1`）。
7. **审计字段与逻辑删除统一由 `BaseEntity` 托管**（`created_by/at`、`updated_by/at`、`deleted_flag`）。

---

## 五、如何上传到 GitHub

> ⚠️ **前置条件**：已安装 Git，且已在 GitHub 上创建好仓库并加了你作为协作者。
> ⚠️ **声明**：后端 B 不负责 `pom.xml` / 启动类 / Spring Security 等基础设施，那些属于后端 A 的工作范围。

### 1. 切到项目根目录

```bash
cd c:\Users\MYT2020\Desktop\-1-main
```

### 2. 首次使用先初始化远端（如果还没 clone 仓库）

如果你的 `-1-main` 文件夹已经是后端 C 已有的仓库 clone，跳过此步。否则：

```bash
git init
git remote add origin <你的仓库地址>
git fetch origin
git checkout -b main origin/main    # 拉主分支
```

### 3. 创建自己的 feature 分支（防止污染别人代码）

```bash
git checkout -b feature/backend-b-master-data
```

### 4. 把新增文件加入暂存区

```bash
git add sales-management/src/Main/java/com/yourcompany/sales/modules/customer
git add sales-management/src/Main/java/com/yourcompany/sales/modules/product
git add sales-management/src/Main/java/com/yourcompany/sales/modules/dict
git add sales-management/docs/sql/schema_backend_b.sql
git add sales-management/BACKEND_B_README.md
```

### 5. 提交并推送

```bash
git commit -m "feat(backend-b): 客户/商品/字典三模块 CRUD + 对接后端 C 的查询服务"
git push -u origin feature/backend-b-master-data
```

### 6. 在 GitHub 上发起 Pull Request

- 源分支：`feature/backend-b-master-data`
- 目标分支：`main`（或团队约定的开发分支）
- PR 标题：`[后端B] 主数据模块：客户/商品/字典`
- 请 reviewer 里勾选 **后端 A（负责合入基础设施）** 和 **后端 C（验证集成点）**

---

## 六、需要其他人配合的事

| 角色    | 需要做的事                                                                                             |
| ------- | ------------------------------------------------------------------------------------------------------ |
| 后端 A  | 搭 `pom.xml`、`application.yml`、`SalesApplication.java`，把 `src/Main/java` 规范化为 `src/main/java` |
| 后端 A  | 实现 `SecurityUtils.getCurrentUserId()` 的真实 Spring Security 取值                                   |
| 后端 C  | 在 `QuoteService / OrderService` 注入 `CustomerQueryService` 和 `SkuQueryService`（见第三节）         |
| 后端 D  | 库存/收款表不由我负责，另行对接                                                                        |
| 前端    | 调 `/api/dicts/{code}/items` 拉下拉数据；客户/商品/分类页面按上面 API 对接                              |

---

## 七、本地验证（等后端 A 把工程搭起来后）

```sql
# 1. 在 MySQL 8 中执行建表：
source sales-management/docs/sql/schema_backend_b.sql;
```

然后让后端 A 跑起来 Spring Boot 应用，用 Postman 试一下：

```http
POST /api/customers
{
  "customerCode": "C0001",
  "customerName": "测试客户 A",
  "customerLevel": "A",
  "creditLimit": 100000
}

GET /api/dicts/CUSTOMER_LEVEL/items
```

看到统一 `ApiResponse` 结构返回即视为通过。
