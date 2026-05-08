import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { uid } from '@/utils/id'

function seedCustomers() {
  const now = Date.now()
  const mk = (i, o) => ({
    id: `c_${i}`,
    customer_code: `C2026${String(i).padStart(4, '0')}`,
    customer_name: o.name,
    customer_level: o.level,
    customer_type: o.type,
    industry: o.industry,
    source: o.source,
    province: o.province,
    city: o.city,
    address: o.address,
    owner_user_name: '张三',
    credit_limit: o.credit,
    current_ar_amount: o.ar,
    follow_status: o.status,
    status: 1,
    remark: o.remark || '',
    created_at: now - i * 86400000,
    updated_at: now - i * 86400000,
  })
  return [
    mk(1, {
      name: '上海云帆信息技术有限公司',
      level: 'A',
      type: 'ENTERPRISE',
      industry: '互联网',
      source: 'INTRODUCE',
      province: '上海',
      city: '上海市',
      address: '浦东新区张江高科技园区祖冲之路 899 号',
      credit: 500000,
      ar: 128000,
      status: '跟进中',
      remark: '重点客户，季度大单',
    }),
    mk(2, {
      name: '深圳创智电子有限公司',
      level: 'A',
      type: 'ENTERPRISE',
      industry: '电子制造',
      source: 'EXHIBITION',
      province: '广东',
      city: '深圳市',
      address: '南山区科技园南区深南大道 10000 号',
      credit: 300000,
      ar: 46000,
      status: '已签约',
    }),
    mk(3, {
      name: '北京恒泰贸易有限公司',
      level: 'B',
      type: 'ENTERPRISE',
      industry: '贸易',
      source: 'WEBSITE',
      province: '北京',
      city: '北京市',
      address: '朝阳区建国路 88 号',
      credit: 100000,
      ar: 0,
      status: '初步接触',
    }),
    mk(4, {
      name: '李四',
      level: 'C',
      type: 'INDIVIDUAL',
      industry: '个人消费',
      source: 'AD',
      province: '浙江',
      city: '杭州市',
      address: '西湖区文三路 478 号',
      credit: 20000,
      ar: 3200,
      status: '初步接触',
    }),
    mk(5, {
      name: '广州万联渠道科技',
      level: 'B',
      type: 'CHANNEL',
      industry: '渠道分销',
      source: 'INTRODUCE',
      province: '广东',
      city: '广州市',
      address: '天河区珠江新城华夏路 10 号',
      credit: 200000,
      ar: 12000,
      status: '跟进中',
    }),
  ]
}

function seedContacts() {
  return [
    {
      id: 'ct_1',
      customer_id: 'c_1',
      name: '王总',
      mobile: '13800138001',
      email: 'wang@yunfan.com',
      position: '采购总监',
      wechat: 'wang_boss',
      is_primary: 1,
      remark: '主要决策人',
      created_at: Date.now() - 30 * 86400000,
    },
    {
      id: 'ct_2',
      customer_id: 'c_1',
      name: '小刘',
      mobile: '13800138002',
      email: 'liu@yunfan.com',
      position: '采购专员',
      wechat: '',
      is_primary: 0,
      remark: '日常对接',
      created_at: Date.now() - 20 * 86400000,
    },
    {
      id: 'ct_3',
      customer_id: 'c_2',
      name: '陈工',
      mobile: '13900139003',
      email: 'chen@czdz.com',
      position: '技术经理',
      wechat: 'chen_engineer',
      is_primary: 1,
      remark: '',
      created_at: Date.now() - 40 * 86400000,
    },
  ]
}

function seedFollowups() {
  return [
    {
      id: 'f_1',
      customer_id: 'c_1',
      follow_user_name: '张三',
      follow_type: 'CALL',
      content: '电话沟通了 Q2 采购计划，客户表示本月底下单。',
      next_follow_time: Date.now() + 3 * 86400000,
      follow_result: '有意向',
      created_at: Date.now() - 2 * 86400000,
    },
    {
      id: 'f_2',
      customer_id: 'c_1',
      follow_user_name: '张三',
      follow_type: 'VISIT',
      content: '拜访客户公司，演示新品 SKU-X200 系列。',
      next_follow_time: Date.now() + 7 * 86400000,
      follow_result: '待定',
      created_at: Date.now() - 7 * 86400000,
    },
    {
      id: 'f_3',
      customer_id: 'c_2',
      follow_user_name: '张三',
      follow_type: 'EMAIL',
      content: '发送了最新报价方案。',
      next_follow_time: Date.now() + 2 * 86400000,
      follow_result: '已回复',
      created_at: Date.now() - 1 * 86400000,
    },
  ]
}

export const useCustomerStore = defineStore('customer', () => {
  const customers = ref(seedCustomers())
  const contacts = ref(seedContacts())
  const followups = ref(seedFollowups())
  const loading = ref(false)

  const customerOptions = computed(() =>
    customers.value
      .filter((c) => c.status === 1)
      .map((c) => ({ id: c.id, name: c.customer_name, code: c.customer_code })),
  )

  function findById(id) {
    return customers.value.find((c) => c.id === id) || null
  }

  function listContactsByCustomer(customerId) {
    return contacts.value.filter((c) => c.customer_id === customerId)
  }

  function listFollowupsByCustomer(customerId) {
    return followups.value
      .filter((f) => f.customer_id === customerId)
      .sort((a, b) => b.created_at - a.created_at)
  }

  function saveCustomer(payload) {
    const now = Date.now()
    if (payload.id) {
      const idx = customers.value.findIndex((c) => c.id === payload.id)
      if (idx >= 0) {
        customers.value[idx] = { ...customers.value[idx], ...payload, updated_at: now }
        return customers.value[idx]
      }
    }
    const newId = uid('c_')
    const created = {
      id: newId,
      customer_code:
        payload.customer_code ||
        `C${new Date().getFullYear()}${String(customers.value.length + 1).padStart(4, '0')}`,
      status: 1,
      owner_user_name: '张三',
      current_ar_amount: 0,
      ...payload,
      created_at: now,
      updated_at: now,
    }
    customers.value.unshift(created)
    return created
  }

  function setCustomerStatus(id, status) {
    const c = findById(id)
    if (c) c.status = status
  }

  function saveContact(payload) {
    const now = Date.now()
    if (payload.is_primary) {
      contacts.value
        .filter((c) => c.customer_id === payload.customer_id && c.id !== payload.id)
        .forEach((c) => (c.is_primary = 0))
    }
    if (payload.id) {
      const idx = contacts.value.findIndex((c) => c.id === payload.id)
      if (idx >= 0) {
        contacts.value[idx] = { ...contacts.value[idx], ...payload }
        return contacts.value[idx]
      }
    }
    const created = {
      id: uid('ct_'),
      is_primary: 0,
      remark: '',
      created_at: now,
      ...payload,
    }
    contacts.value.push(created)
    return created
  }

  function removeContact(id) {
    const idx = contacts.value.findIndex((c) => c.id === id)
    if (idx >= 0) contacts.value.splice(idx, 1)
  }

  function addFollowup(payload) {
    const created = {
      id: uid('f_'),
      follow_user_name: '张三',
      created_at: Date.now(),
      ...payload,
    }
    followups.value.push(created)
    return created
  }

  return {
    customers,
    contacts,
    followups,
    loading,
    customerOptions,
    findById,
    listContactsByCustomer,
    listFollowupsByCustomer,
    saveCustomer,
    setCustomerStatus,
    saveContact,
    removeContact,
    addFollowup,
  }
})
