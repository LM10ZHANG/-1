import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import salesHttp, { unwrapSalesResponse } from '@/api/biz/salesHttp'

function sid(v) {
  return v === null || v === undefined ? '' : String(v)
}

function mapCustomerFromApi(row) {
  if (!row) return null
  return {
    id: sid(row.id),
    customer_code: row.customerCode ?? '',
    customer_name: row.customerName ?? '',
    customer_level: row.customerLevel ?? '',
    customer_type: row.customerType ?? '',
    industry: row.industry ?? '',
    source: row.source ?? '',
    province: row.province ?? '',
    city: row.city ?? '',
    address: row.address ?? '',
    owner_user_id: row.ownerUserId != null ? sid(row.ownerUserId) : '',
    owner_user_name: row.ownerUserName ?? '-',
    credit_limit: Number(row.creditLimit ?? 0),
    current_ar_amount: Number(row.currentArAmount ?? 0),
    follow_status: row.followStatus ?? '',
    status: row.status != null ? Number(row.status) : 1,
    remark: row.remark ?? '',
    created_at: row.createdAt ? new Date(row.createdAt).getTime() : Date.now(),
    updated_at: row.updatedAt ? new Date(row.updatedAt).getTime() : Date.now(),
  }
}

function mapContactFromApi(row, customerId) {
  return {
    id: sid(row.id),
    customer_id: sid(row.customerId ?? customerId),
    name: row.name ?? '',
    mobile: row.mobile ?? '',
    email: row.email ?? '',
    position: row.position ?? '',
    wechat: row.wechat ?? '',
    is_primary: row.isPrimary != null ? Number(row.isPrimary) : 0,
    remark: row.remark ?? '',
    created_at: row.createdAt ? new Date(row.createdAt).getTime() : Date.now(),
  }
}

function mapFollowupFromApi(row) {
  return {
    id: sid(row.id),
    customer_id: sid(row.customerId),
    follow_user_id: row.followUserId != null ? sid(row.followUserId) : '',
    follow_user_name: row.followUserName ?? '-',
    follow_type: mapFollowTypeFromApi(row.followType),
    content: row.content ?? '',
    next_follow_time: row.nextFollowTime ? new Date(row.nextFollowTime).getTime() : null,
    follow_result: row.followResult ?? '',
    created_at: row.createdAt ? new Date(row.createdAt).getTime() : Date.now(),
  }
}

function mapFollowTypeToApi(v) {
  if (v === 'CALL') return 'PHONE'
  return v
}

function mapFollowTypeFromApi(v) {
  if (v === 'PHONE') return 'CALL'
  return v || 'CALL'
}

function toIsoDateTime(ts) {
  if (!ts) return null
  const d = new Date(ts)
  if (Number.isNaN(d.getTime())) return null
  return d.toISOString().slice(0, 19).replace('T', ' ')
}

export const useCustomerStore = defineStore('customer', () => {
  const customers = ref([])
  const contacts = ref([])
  const followups = ref([])
  const loading = ref(false)
  const listTotal = ref(0)

  const customerOptions = computed(() =>
    customers.value
      .filter((c) => c.status === 1)
      .map((c) => ({ id: c.id, name: c.customer_name, code: c.customer_code })),
  )

  function findById(id) {
    const key = sid(id)
    return customers.value.find((c) => c.id === key) || null
  }

  function replaceContactsForCustomer(customerId, list) {
    const cid = sid(customerId)
    const mapped = (list || []).map((r) => mapContactFromApi(r, cid))
    contacts.value = contacts.value.filter((c) => c.customer_id !== cid).concat(mapped)
  }

  function replaceFollowupsForCustomer(customerId, list) {
    const cid = sid(customerId)
    const mapped = (list || []).map(mapFollowupFromApi)
    followups.value = followups.value.filter((f) => f.customer_id !== cid).concat(mapped)
  }

  function listContactsByCustomer(customerId) {
    const cid = sid(customerId)
    return contacts.value.filter((c) => c.customer_id === cid)
  }

  function listFollowupsByCustomer(customerId) {
    const cid = sid(customerId)
    return followups.value
      .filter((f) => f.customer_id === cid)
      .sort((a, b) => b.created_at - a.created_at)
  }

  async function fetchCustomerPage(params) {
    loading.value = true
    try {
      const res = await salesHttp.get('/api/customers', { params })
      const data = unwrapSalesResponse(res)
      if (!data) {
        customers.value = []
        listTotal.value = 0
        return
      }
      customers.value = (data.list || []).map(mapCustomerFromApi)
      listTotal.value = Number(data.total ?? 0)
    } finally {
      loading.value = false
    }
  }

  async function loadCustomerDetail(id) {
    const cid = sid(id)
    loading.value = true
    try {
      const res = await salesHttp.get(`/api/customers/${cid}`)
      const row = unwrapSalesResponse(res)
      if (!row) return null
      const mapped = mapCustomerFromApi(row)
      const idx = customers.value.findIndex((c) => c.id === mapped.id)
      if (idx >= 0) customers.value[idx] = mapped
      else customers.value.unshift(mapped)
      if (row.contacts && Array.isArray(row.contacts)) {
        replaceContactsForCustomer(cid, row.contacts)
      } else {
        await fetchContactsForCustomer(cid)
      }
      await fetchFollowupsForCustomer(cid, 1, 200)
      return mapped
    } finally {
      loading.value = false
    }
  }

  async function fetchContactsForCustomer(customerId) {
    const cid = sid(customerId)
    const res = await salesHttp.get(`/api/customers/${cid}/contacts`)
    const list = unwrapSalesResponse(res)
    replaceContactsForCustomer(cid, list || [])
  }

  async function fetchFollowupsForCustomer(customerId, pageNum = 1, pageSize = 50) {
    const cid = sid(customerId)
    const res = await salesHttp.get(`/api/customers/${cid}/followups`, {
      params: { pageNum, pageSize },
    })
    const page = unwrapSalesResponse(res)
    const list = page?.list || []
    replaceFollowupsForCustomer(cid, list)
  }

  async function saveCustomer(payload) {
    const isEdit = Boolean(payload.id)
    if (isEdit) {
      const body = {
        customerName: payload.customer_name,
        customerLevel: payload.customer_level || undefined,
        customerType: payload.customer_type || undefined,
        industry: payload.industry || undefined,
        source: payload.source || undefined,
        province: payload.province || undefined,
        city: payload.city || undefined,
        address: payload.address || undefined,
        ownerUserId: payload.owner_user_id ? Number(payload.owner_user_id) : undefined,
        creditLimit: payload.credit_limit != null ? Number(payload.credit_limit) : undefined,
        followStatus: payload.follow_status || undefined,
        remark: payload.remark || undefined,
      }
      const res = await salesHttp.put(`/api/customers/${sid(payload.id)}`, body)
      const row = unwrapSalesResponse(res)
      return mapCustomerFromApi(row)
    }
    const body = {
      customerCode: payload.customer_code,
      customerName: payload.customer_name,
      customerLevel: payload.customer_level || undefined,
      customerType: payload.customer_type || undefined,
      industry: payload.industry || undefined,
      source: payload.source || undefined,
      province: payload.province || undefined,
      city: payload.city || undefined,
      address: payload.address || undefined,
      ownerUserId: payload.owner_user_id ? Number(payload.owner_user_id) : undefined,
      creditLimit: payload.credit_limit != null ? Number(payload.credit_limit) : undefined,
      followStatus: payload.follow_status || undefined,
      remark: payload.remark || undefined,
    }
    const res = await salesHttp.post('/api/customers', body)
    const row = unwrapSalesResponse(res)
    return mapCustomerFromApi(row)
  }

  async function setCustomerStatus(id, status) {
    const res = await salesHttp.put(`/api/customers/${sid(id)}/status`, {}, {
      params: { status },
    })
    unwrapSalesResponse(res)
    const c = findById(id)
    if (c) c.status = Number(status)
  }

  async function saveContact(payload) {
    const cid = sid(payload.customer_id)
    const body = {
      name: payload.name,
      mobile: payload.mobile || undefined,
      email: payload.email || undefined,
      position: payload.position || undefined,
      wechat: payload.wechat || undefined,
      isPrimary: payload.is_primary != null ? Number(payload.is_primary) : 0,
      remark: payload.remark || undefined,
    }
    if (payload.id) {
      const res = await salesHttp.put(`/api/customers/${cid}/contacts/${sid(payload.id)}`, body)
      const row = unwrapSalesResponse(res)
      const mapped = mapContactFromApi(row, cid)
      const idx = contacts.value.findIndex((x) => x.id === mapped.id)
      if (idx >= 0) contacts.value[idx] = mapped
      return mapped
    }
    const res = await salesHttp.post(`/api/customers/${cid}/contacts`, body)
    const row = unwrapSalesResponse(res)
    const mapped = mapContactFromApi(row, cid)
    contacts.value.push(mapped)
    return mapped
  }

  async function removeContact(id) {
    const row = contacts.value.find((c) => c.id === sid(id))
    if (!row) return
    const res = await salesHttp.delete(`/api/customers/${row.customer_id}/contacts/${sid(id)}`)
    unwrapSalesResponse(res)
    contacts.value = contacts.value.filter((c) => c.id !== sid(id))
  }

  async function addFollowup(payload) {
    const cid = sid(payload.customer_id)
    const body = {
      followType: mapFollowTypeToApi(payload.follow_type),
      content: payload.content,
      nextFollowTime: toIsoDateTime(payload.next_follow_time),
      followResult: payload.follow_result || undefined,
    }
    const res = await salesHttp.post(`/api/customers/${cid}/followups`, body)
    const row = unwrapSalesResponse(res)
    const mapped = mapFollowupFromApi(row)
    followups.value.push(mapped)
    return mapped
  }

  return {
    customers,
    contacts,
    followups,
    loading,
    listTotal,
    customerOptions,
    findById,
    listContactsByCustomer,
    listFollowupsByCustomer,
    fetchCustomerPage,
    loadCustomerDetail,
    fetchContactsForCustomer,
    fetchFollowupsForCustomer,
    saveCustomer,
    setCustomerStatus,
    saveContact,
    removeContact,
    addFollowup,
  }
})
