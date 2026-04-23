import { defineStore } from 'pinia'
import { ref } from 'vue'
import { uid, genNo } from '@/utils/id'

function seedQuotes() {
  const now = Date.now()
  return [
    {
      id: 'q_1',
      quote_no: 'Q202604010001',
      customer_id: 'c_1',
      customer_name_snapshot: '上海云帆信息技术有限公司',
      contact_id: 'ct_1',
      contact_name_snapshot: '王总',
      quote_date: now - 15 * 86400000,
      expire_date: now + 15 * 86400000,
      payment_term: '月结 30 天',
      delivery_method: '顺丰快递',
      tax_included_flag: 1,
      status: 'APPROVED',
      owner_user_name: '张三',
      remark: '配套笔记本采购',
      items: [
        {
          id: 'qi_1',
          sku_id: 'sku_1',
          sku_name_snapshot: 'ThinkPad X1 Carbon i7/32G/1TB',
          qty: 5,
          origin_unit_price: 14999,
          discount_rate: 5,
          deal_unit_price: 14249.05,
          tax_rate: 13,
          remark: '',
        },
        {
          id: 'qi_2',
          sku_id: 'sku_4',
          sku_name_snapshot: '罗技 MX Keys 机械键盘 黑色',
          qty: 5,
          origin_unit_price: 899,
          discount_rate: 0,
          deal_unit_price: 899,
          tax_rate: 13,
          remark: '搭售',
        },
      ],
      approvals: [
        {
          id: 'ap_1',
          action: 'APPROVE',
          approver_user_name: '李经理',
          comment: '折扣符合政策，同意',
          action_time: now - 14 * 86400000,
        },
      ],
      created_at: now - 15 * 86400000,
      updated_at: now - 14 * 86400000,
    },
    {
      id: 'q_2',
      quote_no: 'Q202604050002',
      customer_id: 'c_2',
      customer_name_snapshot: '深圳创智电子有限公司',
      contact_id: 'ct_3',
      contact_name_snapshot: '陈工',
      quote_date: now - 5 * 86400000,
      expire_date: now + 25 * 86400000,
      payment_term: '预付 50% 尾款货到付',
      delivery_method: '自提',
      tax_included_flag: 1,
      status: 'WAIT_APPROVAL',
      owner_user_name: '张三',
      remark: '测试样机批次',
      items: [
        {
          id: 'qi_3',
          sku_id: 'sku_3',
          sku_name_snapshot: 'MacBook Pro 14 M3 Pro/18G/512G 深空黑',
          qty: 10,
          origin_unit_price: 19999,
          discount_rate: 18,
          deal_unit_price: 16399.18,
          tax_rate: 13,
          remark: '大批量折扣',
        },
      ],
      approvals: [],
      created_at: now - 5 * 86400000,
      updated_at: now - 1 * 86400000,
    },
    {
      id: 'q_3',
      quote_no: 'Q202604200003',
      customer_id: 'c_5',
      customer_name_snapshot: '广州万联渠道科技',
      contact_id: null,
      contact_name_snapshot: '',
      quote_date: now - 2 * 86400000,
      expire_date: now + 30 * 86400000,
      payment_term: '货到付款',
      delivery_method: '德邦物流',
      tax_included_flag: 1,
      status: 'DRAFT',
      owner_user_name: '张三',
      remark: '',
      items: [
        {
          id: 'qi_4',
          sku_id: 'sku_5',
          sku_name_snapshot: 'Dell U2723QE 27" 4K',
          qty: 20,
          origin_unit_price: 3299,
          discount_rate: 8,
          deal_unit_price: 3035.08,
          tax_rate: 13,
          remark: '',
        },
      ],
      approvals: [],
      created_at: now - 2 * 86400000,
      updated_at: now - 2 * 86400000,
    },
  ]
}

export const useQuoteStore = defineStore('quote', () => {
  const quotes = ref(seedQuotes())
  const loading = ref(false)

  function findById(id) {
    return quotes.value.find((q) => q.id === id) || null
  }

  function saveQuote(payload) {
    const now = Date.now()
    if (payload.id) {
      const idx = quotes.value.findIndex((q) => q.id === payload.id)
      if (idx >= 0) {
        const prev = quotes.value[idx]
        const next = {
          ...prev,
          ...payload,
          updated_at: now,
        }
        if (prev.status === 'APPROVED') {
          next.status = 'WAIT_APPROVAL'
          next.approvals = [
            ...(prev.approvals || []),
            {
              id: uid('ap_'),
              action: 'SYSTEM',
              approver_user_name: '系统',
              comment: '已审批通过的报价被修改，自动回退到待审批',
              action_time: now,
            },
          ]
        }
        quotes.value[idx] = next
        return next
      }
    }
    const seq = quotes.value.length + 1
    const created = {
      id: uid('q_'),
      quote_no: payload.quote_no || genNo('Q', seq),
      status: 'DRAFT',
      owner_user_name: '张三',
      approvals: [],
      items: [],
      created_at: now,
      updated_at: now,
      ...payload,
    }
    quotes.value.unshift(created)
    return created
  }

  function submitApproval(id) {
    const q = findById(id)
    if (!q) return
    q.status = 'WAIT_APPROVAL'
    q.updated_at = Date.now()
  }

  function approve(id, comment) {
    const q = findById(id)
    if (!q) return
    q.status = 'APPROVED'
    q.updated_at = Date.now()
    q.approvals = [
      ...(q.approvals || []),
      {
        id: uid('ap_'),
        action: 'APPROVE',
        approver_user_name: '李经理',
        comment: comment || '同意',
        action_time: Date.now(),
      },
    ]
  }

  function reject(id, comment) {
    const q = findById(id)
    if (!q) return
    q.status = 'REJECTED'
    q.updated_at = Date.now()
    q.approvals = [
      ...(q.approvals || []),
      {
        id: uid('ap_'),
        action: 'REJECT',
        approver_user_name: '李经理',
        comment: comment || '驳回',
        action_time: Date.now(),
      },
    ]
  }

  function voidQuote(id) {
    const q = findById(id)
    if (!q) return
    q.status = 'VOID'
    q.updated_at = Date.now()
  }

  return {
    quotes,
    loading,
    findById,
    saveQuote,
    submitApproval,
    approve,
    reject,
    voidQuote,
  }
})
