import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import salesHttp, { unwrapSalesResponse } from '@/api/biz/salesHttp'

function sid(v) {
  return v === null || v === undefined ? '' : String(v)
}

function toTs(iso) {
  if (!iso) return Date.now()
  const d = new Date(iso)
  return Number.isNaN(d.getTime()) ? Date.now() : d.getTime()
}

function toLocalDateStr(ts) {
  if (!ts) return null
  const d = new Date(ts)
  if (Number.isNaN(d.getTime())) return null
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function percentOffToMultiplier(pct) {
  const p = Number(pct) || 0
  return Number(Math.max(0, Math.min(1, 1 - p / 100)).toFixed(4))
}

function multiplierToPercentOff(m) {
  if (m == null) return 0
  const x = Number(m)
  if (Number.isNaN(x)) return 0
  return Number(((1 - x) * 100).toFixed(2))
}

function mapQuoteItemFromApi(it) {
  const m = it.discountRate != null ? Number(it.discountRate) : 1
  return {
    id: sid(it.id),
    sku_id: sid(it.skuId),
    sku_code: it.skuCode ?? '',
    sku_name_snapshot: it.skuNameSnapshot || it.skuName || '',
    qty: it.qty != null ? Number(it.qty) : 1,
    origin_unit_price: Number(it.originUnitPrice ?? 0),
    discount_rate: multiplierToPercentOff(m),
    deal_unit_price: Number(it.dealUnitPrice ?? 0),
    tax_rate: Number(it.taxRate ?? 0),
    remark: it.remark ?? '',
  }
}

function mapApprovalFromApi(a) {
  const act = a.action || ''
  return {
    id: sid(a.id),
    action: act,
    approver_user_name: a.approverUserId != null ? `用户#${a.approverUserId}` : '-',
    comment: a.comment ?? '',
    action_time: toTs(a.actionTime),
  }
}

function mapQuoteFromApi(q, approvals) {
  const taxFlag = q.taxIncludedFlag === true || q.taxIncludedFlag === 1 ? 1 : 0
  return {
    id: sid(q.id),
    quote_no: q.quoteNo ?? '',
    customer_id: sid(q.customerId),
    customer_name_snapshot: q.customerName ?? '',
    contact_id: q.contactId != null ? sid(q.contactId) : null,
    contact_name_snapshot: q.contactName ?? '',
    quote_date: toTs(q.quoteDate),
    expire_date: toTs(q.expireDate),
    payment_term: q.paymentTerm ?? '',
    delivery_method: q.deliveryMethod ?? '',
    tax_included_flag: taxFlag,
    status: q.status ?? 'DRAFT',
    approval_status: q.approvalStatus,
    owner_user_name: q.ownerUserName ?? '-',
    remark: q.remark ?? '',
    items: (q.items || []).map(mapQuoteItemFromApi),
    approvals: approvals || [],
    created_at: toTs(q.createdAt),
    updated_at: toTs(q.updatedAt),
  }
}

function upsert(quotesRef, row) {
  const idx = quotesRef.value.findIndex((x) => x.id === row.id)
  if (idx >= 0) quotesRef.value[idx] = row
  else quotesRef.value.unshift(row)
}

export const useQuoteStore = defineStore('quote', () => {
  const quotes = ref([])
  const loading = ref(false)
  const listUnavailable = ref(false)

  function findById(id) {
    const key = sid(id)
    return quotes.value.find((q) => q.id === key) || null
  }

  async function fetchApprovalHistory(quoteId) {
    const res = await salesHttp.get('/api/approvals/history', {
      params: { bizType: 'QUOTE', bizId: Number(quoteId) },
    })
    const list = unwrapSalesResponse(res, { silent: true })
    return Array.isArray(list) ? list.map(mapApprovalFromApi) : []
  }

  async function loadQuoteById(id) {
    loading.value = true
    try {
      const res = await salesHttp.get(`/api/quotes/${sid(id)}`)
      const row = unwrapSalesResponse(res)
      if (!row) return null
      let approvals = []
      try {
        approvals = await fetchApprovalHistory(id)
      } catch {
        approvals = []
      }
      const mapped = mapQuoteFromApi(row, approvals)
      upsert(quotes, mapped)
      return mapped
    } finally {
      loading.value = false
    }
  }

  async function fetchQuotePage(params) {
    loading.value = true
    listUnavailable.value = false
    try {
      const res = await salesHttp.get('/api/quotes', { params })
      const data = unwrapSalesResponse(res, { silent: true })
      if (!data) {
        quotes.value = []
        listUnavailable.value = true
        return
      }
      const rows = (data.list || []).map((q) => mapQuoteFromApi(q, []))
      quotes.value = rows
    } catch {
      quotes.value = []
      listUnavailable.value = true
    } finally {
      loading.value = false
    }
  }

  function buildItemsPayload(items) {
    return (items || []).map((it) => ({
      skuId: Number(it.sku_id),
      qty: Number(it.qty) || 1,
      discountRate: percentOffToMultiplier(it.discount_rate),
      remark: it.remark || undefined,
    }))
  }

  async function saveQuote(payload) {
    const expire = toLocalDateStr(payload.expire_date)
    if (!expire) {
      ElMessage.error('请选择有效期')
      throw new Error('expire')
    }
    const base = {
      customerId: Number(payload.customer_id),
      contactId: payload.contact_id ? Number(payload.contact_id) : undefined,
      expireDate: expire,
      paymentTerm: payload.payment_term || undefined,
      deliveryMethod: payload.delivery_method || undefined,
      taxIncludedFlag: Boolean(Number(payload.tax_included_flag)),
      discountAmount: 0,
      remark: payload.remark || undefined,
      items: buildItemsPayload(payload.items),
    }
    if (payload.id) {
      const res = await salesHttp.put(`/api/quotes/${sid(payload.id)}`, base)
      const row = unwrapSalesResponse(res)
      const approvals = await fetchApprovalHistory(payload.id).catch(() => [])
      const mapped = mapQuoteFromApi(row, approvals)
      upsert(quotes, mapped)
      return mapped
    }
    const res = await salesHttp.post('/api/quotes', base)
    const row = unwrapSalesResponse(res)
    const mapped = mapQuoteFromApi(row, [])
    upsert(quotes, mapped)
    return mapped
  }

  async function submitApproval(id) {
    const res = await salesHttp.post(`/api/quotes/${sid(id)}/submit-approval`, {})
    unwrapSalesResponse(res)
    await loadQuoteById(id)
  }

  async function approve(id, comment) {
    const text = (comment && String(comment).trim()) || '同意'
    const res = await salesHttp.post(`/api/quotes/${sid(id)}/approve`, { comment: text })
    unwrapSalesResponse(res)
    await loadQuoteById(id)
  }

  async function reject(id, comment) {
    const res = await salesHttp.post(`/api/quotes/${sid(id)}/reject`, {
      comment: String(comment || '').trim() || '驳回',
    })
    unwrapSalesResponse(res)
    await loadQuoteById(id)
  }

  function voidQuote(_id) {
    ElMessage.warning('当前后端未提供报价作废接口，无法作废')
  }

  return {
    quotes,
    loading,
    listUnavailable,
    findById,
    fetchQuotePage,
    loadQuoteById,
    saveQuote,
    submitApproval,
    approve,
    reject,
    voidQuote,
  }
})
