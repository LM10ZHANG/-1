export const CUSTOMER_LEVELS = [
  { value: 'A', label: 'A 级（重点）' },
  { value: 'B', label: 'B 级（普通）' },
  { value: 'C', label: 'C 级（潜在）' },
]

export const CUSTOMER_TYPES = [
  { value: 'ENTERPRISE', label: '企业' },
  { value: 'INDIVIDUAL', label: '个人' },
  { value: 'CHANNEL', label: '渠道' },
]

export const CUSTOMER_SOURCES = [
  { value: 'INTRODUCE', label: '朋友介绍' },
  { value: 'WEBSITE', label: '官网注册' },
  { value: 'EXHIBITION', label: '展会' },
  { value: 'AD', label: '广告投放' },
  { value: 'OTHER', label: '其他' },
]

export const FOLLOW_TYPES = [
  { value: 'CALL', label: '电话' },
  { value: 'VISIT', label: '拜访' },
  { value: 'WECHAT', label: '微信' },
  { value: 'EMAIL', label: '邮件' },
]

export const PRODUCT_STATUS = [
  { value: 1, label: '启用', type: 'success' },
  { value: 0, label: '停用', type: 'info' },
]

export const QUOTE_STATUS = [
  { value: 'DRAFT', label: '草稿', type: 'info' },
  { value: 'WAIT_APPROVAL', label: '待审批', type: 'warning' },
  { value: 'APPROVED', label: '已通过', type: 'success' },
  { value: 'REJECTED', label: '已驳回', type: 'danger' },
  { value: 'VOID', label: '已作废', type: 'info' },
]

export function getEnumLabel(list, value) {
  return list.find((item) => item.value === value)?.label ?? value
}

export function getEnumTagType(list, value) {
  return list.find((item) => item.value === value)?.type ?? 'info'
}

export const DISCOUNT_APPROVAL_THRESHOLD = 0.15
