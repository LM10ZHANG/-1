# Dashboard、Payment、Stock 接口文档

## 1. 说明

当前项目这三类接口都已经存在，但响应包装不完全统一：

- `dashboard`、`reports` 使用 `ApiResponse`
- `payment`、`stock`、`outbound` 使用 `Result`

示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

`ApiResponse` 额外可能带有 `timestamp`、`traceId`。

## 2. Dashboard 模块

### 2.1 GET `/api/dashboard/overview`

用途：经营概览。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `scope` | string | 否 | `TODAY`、`WEEK`、`MONTH`、`CUSTOM`，默认 `TODAY` |
| `startDate` | date | 否 | `yyyy-MM-dd`，`CUSTOM` 时使用 |
| `endDate` | date | 否 | `yyyy-MM-dd`，`CUSTOM` 时使用 |

返回 `data` 字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `pendingApprovalQuotes` | long | 待审批报价数 |
| `pendingOutboundOrders` | long | 待出库订单数 |
| `pendingReceivableOrders` | long | 待收款订单数 |
| `todayNewCustomers` | long | 今日新增客户数 |
| `monthOrderAmount` | decimal | 本月订单额 |
| `monthPaymentAmount` | decimal | 本月回款额 |
| `orderCompletionRate` | decimal | 订单完成率 |
| `roleView` | string | 当前写死为 `BOSS` |

业务说明：

- 本月订单额、本月回款额固定按“本月”统计。
- 订单完成率按选定时间范围内 `completedOrders / totalOrders` 计算。

### 2.2 GET `/api/dashboard/rankings`

用途：排行榜。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `scope` | string | 否 | 同上 |
| `startDate` | date | 否 | 同上 |
| `endDate` | date | 否 | 同上 |
| `type` | string | 是 | `SALES`、`PRODUCT`、`CUSTOMER` |
| `page` | int | 否 | 默认 `0`，这里是从 `0` 开始的页码 |
| `size` | int | 否 | 默认 `10` |

返回 `data` 为分页对象：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `total` | long | 总数 |
| `pageNum` | int | 返回时转换为从 `1` 开始 |
| `pageSize` | int | 每页条数 |
| `pages` | int | 总页数 |
| `list` | array | 排行数据 |

排行项字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `bizId` | long | 业务对象ID |
| `bizName` | string | 名称 |
| `amount` | decimal | 金额汇总 |
| `count` | int | 数量或单数 |
| `rankNo` | int | 排名 |

业务说明：

- `SALES` 按订单 `ownerUserId` 汇总。
- `PRODUCT` 按订单明细 SKU 汇总。
- `CUSTOMER` 按客户成交金额汇总。

### 2.3 GET `/api/dashboard/warnings`

用途：预警列表。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | int | 否 | 默认 `0` |
| `size` | int | 否 | 默认 `10` |

返回 `data` 字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `overdueFollowCustomers` | array | 超期未跟进客户 |
| `lowStockWarnings` | array | 低库存预警 |
| `overdueReceivables` | array | 逾期未回款预警 |

预警项字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `bizId` | long | 业务对象ID |
| `bizCode` | string | 编码 |
| `bizName` | string | 名称 |
| `warningType` | string | 预警类型 |
| `warningMessage` | string | 预警信息 |

### 2.4 GET `/api/reports/sales-trend`

用途：销售趋势图。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `scope` | string | 否 | 同上 |
| `startDate` | date | 否 | 同上 |
| `endDate` | date | 否 | 同上 |

返回 `data` 为数组：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `date` | date | 日期 |
| `orderAmount` | decimal | 当日订单金额 |
| `paymentAmount` | decimal | 当日有效收款金额 |

## 3. Payment 模块

### 3.1 GET `/api/payments`

用途：分页查询收款记录。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `orderId` | long | 否 | 订单ID |
| `customerId` | long | 否 | 客户ID |
| `paymentNo` | string | 否 | 收款单号，模糊匹配 |
| `payMethod` | string | 否 | 收款方式 |
| `status` | string | 否 | 当前实现主要是 `VALID` |
| `startTime` | datetime | 否 | 收款开始时间 |
| `endTime` | datetime | 否 | 收款结束时间 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `paymentNo` | string | 收款单号 |
| `orderId` | long | 订单ID |
| `orderNo` | string | 订单编号 |
| `customerId` | long | 客户ID |
| `customerName` | string | 客户名称 |
| `payAmount` | decimal | 收款金额 |
| `payMethod` | string | 收款方式 |
| `payTime` | datetime | 收款时间 |
| `voucherUrl` | string | 凭证地址 |
| `operatorUserId` | long | 经办人 |
| `status` | string | 状态 |
| `remark` | string | 备注 |

### 3.2 POST `/api/payments`

用途：登记收款。

请求体：

```json
{
  "orderId": 1,
  "customerId": 1,
  "payAmount": 500.00,
  "payMethod": "BANK",
  "payTime": "2026-05-06T10:00:00",
  "voucherUrl": "https://example.com/voucher.png",
  "operatorUserId": 1,
  "remark": "首付款"
}
```

关键规则：

- `orderId`、`customerId` 不能为空。
- `payAmount` 必须大于 `0`。
- 收款客户必须和订单客户一致。
- 累计净收款不得超过订单总金额。
- 成功后会回写订单 `paidAmount` 和 `paymentStatus`。

### 3.3 GET `/api/receivables`

用途：应收账款查询。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `customerId` | long | 否 | 客户ID |
| `orderId` | long | 否 | 订单ID |
| `paymentStatus` | string | 否 | `UNPAID`、`PARTIAL`、`PAID` 等 |
| `overdueOnly` | boolean | 否 | 是否仅看逾期 |
| `startDate` | date | 否 | 订单开始日期 |
| `endDate` | date | 否 | 订单结束日期 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `orderId` | long | 订单ID |
| `orderNo` | string | 订单编号 |
| `customerId` | long | 客户ID |
| `customerName` | string | 客户名称 |
| `orderDate` | date | 下单日期 |
| `deliveryDate` | date | 交付日期 |
| `totalAmount` | decimal | 订单总额 |
| `receivedAmount` | decimal | 已收净额 |
| `unreceivedAmount` | decimal | 未收金额 |
| `paymentStatus` | string | 付款状态 |
| `overdueDays` | long | 逾期天数 |
| `riskLevel` | string | `LOW`、`MEDIUM`、`HIGH` |

### 3.4 GET `/api/invoices`

用途：分页查询发票。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `orderId` | long | 否 | 订单ID |
| `invoiceNo` | string | 否 | 发票编号，模糊匹配 |
| `invoiceStatus` | string | 否 | 开票状态 |
| `startDate` | date | 否 | 开票开始日期 |
| `endDate` | date | 否 | 开票结束日期 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `invoiceNo` | string | 发票编号 |
| `orderId` | long | 订单ID |
| `invoiceTitle` | string | 发票抬头 |
| `taxNo` | string | 税号 |
| `invoiceAmount` | decimal | 开票金额 |
| `invoiceStatus` | string | 开票状态 |
| `invoiceDate` | date | 开票日期 |

### 3.5 POST `/api/invoices`

用途：新增发票。

请求体：

```json
{
  "orderId": 1,
  "invoiceTitle": "示例客户有限公司",
  "taxNo": "913xxxxxxxxxxxxx",
  "invoiceAmount": 500.00,
  "invoiceStatus": "ISSUED",
  "invoiceDate": "2026-05-06"
}
```

关键规则：

- `orderId` 不能为空。
- `invoiceAmount` 必须大于 `0`。
- 累计开票金额不得超过订单总金额。
- `invoiceStatus` 为空时默认写入 `ISSUED`。

### 3.6 GET `/api/refunds`

用途：分页查询退款单。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `orderId` | long | 否 | 订单ID |
| `paymentId` | long | 否 | 原收款ID |
| `status` | string | 否 | `WAIT`、`FINISHED`、`REJECTED` |
| `startTime` | datetime | 否 | 创建开始时间 |
| `endTime` | datetime | 否 | 创建结束时间 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `refundNo` | string | 退款单号 |
| `orderId` | long | 订单ID |
| `paymentId` | long | 原收款ID |
| `refundAmount` | decimal | 退款金额 |
| `refundReason` | string | 退款原因 |
| `refundTime` | datetime | 退款时间 |
| `status` | string | 状态 |
| `createdAt` | datetime | 创建时间 |

### 3.7 POST `/api/refunds`

用途：创建退款单。

请求体：

```json
{
  "orderId": 1,
  "paymentId": 1,
  "refundAmount": 100.00,
  "refundReason": "售后退款",
  "refundTime": "2026-05-06T12:00:00"
}
```

关键规则：

- `refundAmount` 必须大于 `0`。
- `paymentId` 必须存在且原收款状态必须是 `VALID`。
- 退款单中的 `orderId` 必须与原收款记录一致。
- 累计完成退款金额不得超过原收款金额。
- 新建后的初始状态为 `WAIT`。

### 3.8 POST `/api/refunds/{id}/finish`

用途：完成退款。

关键规则：

- 只有 `WAIT` 状态可以完成。
- 完成后状态变为 `FINISHED`。
- 完成后会重新回写订单净收款与付款状态。

### 3.9 POST `/api/refunds/{id}/reject`

用途：驳回退款。

关键规则：

- 只有 `WAIT` 状态可以驳回。
- 驳回后状态变为 `REJECTED`。

## 4. Stock 模块

### 4.1 GET `/api/stocks`

用途：分页查询库存台账。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `warehouseId` | long | 否 | 仓库ID |
| `skuId` | long | 否 | SKU ID |
| `skuCode` | string | 否 | SKU编码，模糊匹配 |
| `skuName` | string | 否 | SKU名称，模糊匹配 |
| `lowStockOnly` | boolean | 否 | 是否只看低库存 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 库存记录ID |
| `warehouseId` | long | 仓库ID |
| `warehouseName` | string | 当前实现为 `仓库-{warehouseId}` |
| `skuId` | long | SKU ID |
| `skuCode` | string | SKU编码 |
| `skuName` | string | SKU名称 |
| `totalQty` | int | 总库存 |
| `availableQty` | int | 可用库存 |
| `lockedQty` | int | 锁定库存 |
| `warnQty` | int | 预警值 |
| `lowStock` | boolean | 是否低库存 |
| `updatedAt` | datetime | 更新时间 |

### 4.2 GET `/api/stocks/{id}`

用途：库存详情。

返回 `data` 字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `stock` | object | 库存主信息 |
| `changeRecords` | array | 最近 20 条库存变更记录 |

库存变更记录字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `changeType` | string | `LOCK`、`RELEASE`、`OUTBOUND`、`RETURN_INBOUND` |
| `beforeTotalQty` | int | 变更前总库存 |
| `beforeAvailableQty` | int | 变更前可用库存 |
| `beforeLockedQty` | int | 变更前锁定库存 |
| `changeQty` | int | 变更数量 |
| `afterTotalQty` | int | 变更后总库存 |
| `afterAvailableQty` | int | 变更后可用库存 |
| `afterLockedQty` | int | 变更后锁定库存 |
| `bizType` | string | 业务类型 |
| `bizId` | long | 业务ID |
| `remark` | string | 备注 |
| `operatorUserId` | long | 操作人 |
| `createdAt` | datetime | 创建时间 |

### 4.3 POST `/api/stocks/lock`

用途：订单锁库存。

请求体：

```json
{
  "orderId": 1,
  "warehouseId": 1,
  "remark": "订单锁库",
  "items": [
    {
      "orderItemId": 11,
      "skuId": 101,
      "quantity": 10
    }
  ]
}
```

关键规则：

- `quantity` 必须大于 `0`。
- 锁定量不能超过库存可用量。
- 锁定量不能导致 `已锁 + 已出库 + 本次锁定 > 订单数量`。
- 成功后库存 `availableQty` 减少、`lockedQty` 增加。
- 成功后订单明细 `lockedQty` 回写，订单可能进入 `WAIT_OUTBOUND`。

### 4.4 POST `/api/stocks/release`

用途：释放已锁库存。

请求体：

```json
{
  "orderId": 1,
  "warehouseId": 1,
  "reason": "订单变更释放库存",
  "items": [
    {
      "orderItemId": 11,
      "skuId": 101,
      "quantity": 2
    }
  ]
}
```

关键规则：

- `quantity` 必须大于 `0`。
- 释放量不能大于当前已锁数量。
- 成功后库存 `lockedQty` 减少、`availableQty` 增加。

### 4.5 POST `/api/outbound-orders`

用途：执行出库。

请求体：

```json
{
  "orderId": 1,
  "warehouseId": 1,
  "outboundTime": "2026-05-06T14:00:00",
  "remark": "整单出库",
  "items": [
    {
      "orderItemId": 11,
      "skuId": 101,
      "quantity": 10
    }
  ]
}
```

关键规则：

- 订单状态必须允许出库，当前代码要求为 `WAIT_OUTBOUND`。
- `quantity` 必须大于 `0`。
- 出库量不能大于订单已锁数量。
- 出库量不能大于库存锁定数量。
- 成功后库存 `lockedQty` 减少、`totalQty` 减少。
- 成功后订单明细 `outboundQty` 增加。

### 4.6 GET `/api/outbound-orders`

用途：分页查询出库单。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | int | 否 | 默认 `1` |
| `pageSize` | int | 否 | 默认 `10` |
| `orderId` | long | 否 | 订单ID |
| `outboundNo` | string | 否 | 出库单号，模糊匹配 |
| `warehouseId` | long | 否 | 仓库ID |
| `status` | string | 否 | 当前实现主要为 `FINISHED` |
| `startTime` | datetime | 否 | 出库开始时间 |
| `endTime` | datetime | 否 | 出库结束时间 |

返回列表项：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `outboundNo` | string | 出库单号 |
| `orderId` | long | 订单ID |
| `warehouseId` | long | 仓库ID |
| `status` | string | 状态 |
| `remark` | string | 备注 |
| `outboundTime` | datetime | 出库时间 |

### 4.7 GET `/api/outbound-orders/{id}`

用途：出库单详情。

返回 `data` 字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `header` | object | 出库单头信息 |
| `items` | array | 出库明细 |

出库明细字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | long | 主键 |
| `orderItemId` | long | 订单明细ID |
| `skuId` | long | SKU ID |
| `outboundQty` | int | 出库数量 |

### 4.8 POST `/api/returns/inbound`

用途：退货入库。

请求体：

```json
{
  "orderId": 1,
  "warehouseId": 1,
  "inboundTime": "2026-05-06T16:00:00",
  "reason": "客户退货",
  "remark": "外包装破损",
  "items": [
    {
      "orderItemId": 11,
      "skuId": 101,
      "quantity": 1
    }
  ]
}
```

关键规则：

- `quantity` 必须大于 `0`。
- 成功后库存 `totalQty`、`availableQty` 增加。
- 会落一条 `RETURN_INBOUND` 库存变更记录。

## 5. 联调提醒

1. `dashboard` 页码参数用 `page`，且从 `0` 开始；`payment/stock` 用 `pageNum`，且从 `1` 开始。
2. 当前仓库并没有把所有需求文档中的接口都补全，比如订单分页列表仍是 TODO，但 `dashboard/payment/stock` 本身的主接口已具备。
3. 由于后端A未收口，测试报告里应把“权限缺失”和“业务错误”分开记录。
