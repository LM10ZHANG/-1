import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import salesHttp, { unwrapSalesResponse } from '@/api/biz/salesHttp'

function sid(v) {
  return v === null || v === undefined ? '' : String(v)
}

function flattenCategoryTree(nodes, out = []) {
  if (!nodes) return out
  for (const n of nodes) {
    out.push({
      id: sid(n.id),
      name: n.categoryName ?? '',
      category_code: n.categoryCode ?? '',
      parent_id: n.parentId != null && Number(n.parentId) !== 0 ? sid(n.parentId) : null,
      sort: n.sortNo != null ? Number(n.sortNo) : 0,
      status: n.status != null ? Number(n.status) : 1,
      remark: n.remark ?? '',
    })
    if (n.children && n.children.length) flattenCategoryTree(n.children, out)
  }
  return out
}

function mapSpu(row) {
  return {
    id: sid(row.id),
    spu_code: row.spuCode ?? '',
    spu_name: row.spuName ?? '',
    category_id: row.categoryId != null ? sid(row.categoryId) : '',
    category_name: row.categoryName ?? '',
    brand_name: row.brandName ?? '',
    unit_name: row.unitName ?? '',
    description: row.description ?? '',
    status: row.status != null ? Number(row.status) : 1,
    created_at: row.createdAt ? new Date(row.createdAt).getTime() : Date.now(),
    updated_at: row.updatedAt ? new Date(row.updatedAt).getTime() : Date.now(),
  }
}

function mapSku(row) {
  let spec_json = {}
  if (row.specJson) {
    try {
      spec_json = typeof row.specJson === 'string' ? JSON.parse(row.specJson) : row.specJson
    } catch {
      spec_json = {}
    }
  }
  return {
    id: sid(row.id),
    spu_id: row.spuId != null ? sid(row.spuId) : '',
    spu_name: row.spuName ?? '',
    sku_code: row.skuCode ?? '',
    sku_name: row.skuName ?? '',
    spec_json,
    barcode: row.barcode ?? '',
    sale_price: Number(row.salePrice ?? 0),
    cost_price: Number(row.costPrice ?? 0),
    tax_rate: Number(row.taxRate ?? 0),
    stock_warn_qty: row.stockWarnQty != null ? Number(row.stockWarnQty) : 0,
    status: row.status != null ? Number(row.status) : 1,
    created_at: row.createdAt ? new Date(row.createdAt).getTime() : Date.now(),
    updated_at: row.updatedAt ? new Date(row.updatedAt).getTime() : Date.now(),
  }
}

export const useProductStore = defineStore('product', () => {
  const categories = ref([])
  const spuList = ref([])
  const skuList = ref([])
  const loading = ref(false)
  const spuTotal = ref(0)
  const skuTotal = ref(0)

  const skuOptions = computed(() =>
    skuList.value
      .filter((s) => s.status === 1)
      .map((s) => ({
        id: s.id,
        sku_code: s.sku_code,
        sku_name: s.sku_name,
        sale_price: s.sale_price,
        tax_rate: s.tax_rate,
        spu_name: s.spu_name || spuList.value.find((p) => p.id === s.spu_id)?.spu_name || '',
      })),
  )

  function categoryName(id) {
    return categories.value.find((c) => c.id === sid(id))?.name ?? '-'
  }

  function findSpuById(id) {
    return spuList.value.find((s) => s.id === sid(id)) || null
  }

  function findSkuById(id) {
    return skuList.value.find((s) => s.id === sid(id)) || null
  }

  async function fetchCategoryTree() {
    loading.value = true
    try {
      const res = await salesHttp.get('/api/products/categories')
      const tree = unwrapSalesResponse(res)
      categories.value = flattenCategoryTree(tree || [])
    } finally {
      loading.value = false
    }
  }

  async function fetchSpuPage(params) {
    loading.value = true
    try {
      const res = await salesHttp.get('/api/products/spu', { params })
      const data = unwrapSalesResponse(res)
      if (!data) {
        spuList.value = []
        spuTotal.value = 0
        return
      }
      spuList.value = (data.list || []).map(mapSpu)
      spuTotal.value = Number(data.total ?? 0)
    } finally {
      loading.value = false
    }
  }

  async function fetchSkuPage(params) {
    loading.value = true
    try {
      const res = await salesHttp.get('/api/products/sku', { params })
      const data = unwrapSalesResponse(res)
      if (!data) {
        skuList.value = []
        skuTotal.value = 0
        return
      }
      skuList.value = (data.list || []).map(mapSku)
      skuTotal.value = Number(data.total ?? 0)
    } finally {
      loading.value = false
    }
  }

  async function loadSpuById(id) {
    const res = await salesHttp.get(`/api/products/spu/${sid(id)}`)
    const row = unwrapSalesResponse(res)
    return row ? mapSpu(row) : null
  }

  async function loadSkuById(id) {
    const res = await salesHttp.get(`/api/products/sku/${sid(id)}`)
    const row = unwrapSalesResponse(res)
    return row ? mapSku(row) : null
  }

  async function saveSpu(payload) {
    const body = {
      spuCode: payload.spu_code,
      spuName: payload.spu_name,
      categoryId: payload.category_id ? Number(payload.category_id) : undefined,
      brandName: payload.brand_name || undefined,
      unitName: payload.unit_name || undefined,
      description: payload.description || undefined,
      status: payload.status != null ? Number(payload.status) : 1,
    }
    if (payload.id) {
      const res = await salesHttp.put(`/api/products/spu/${sid(payload.id)}`, body)
      const row = unwrapSalesResponse(res)
      return mapSpu(row)
    }
    const res = await salesHttp.post('/api/products/spu', body)
    const row = unwrapSalesResponse(res)
    return mapSpu(row)
  }

  async function saveSku(payload) {
    const body = {
      spuId: Number(payload.spu_id),
      skuCode: payload.sku_code,
      skuName: payload.sku_name,
      specJson:
        payload.spec_json && Object.keys(payload.spec_json).length
          ? JSON.stringify(payload.spec_json)
          : undefined,
      barcode: payload.barcode || undefined,
      salePrice: payload.sale_price != null ? Number(payload.sale_price) : 0,
      costPrice: payload.cost_price != null ? Number(payload.cost_price) : 0,
      taxRate: payload.tax_rate != null ? Number(payload.tax_rate) : 0,
      stockWarnQty: payload.stock_warn_qty != null ? Number(payload.stock_warn_qty) : 0,
      status: payload.status != null ? Number(payload.status) : 1,
    }
    if (payload.id) {
      const res = await salesHttp.put(`/api/products/sku/${sid(payload.id)}`, body)
      const row = unwrapSalesResponse(res)
      return mapSku(row)
    }
    const res = await salesHttp.post('/api/products/sku', body)
    const row = unwrapSalesResponse(res)
    return mapSku(row)
  }

  async function setSpuStatus(id, status) {
    const res = await salesHttp.put(`/api/products/spu/${sid(id)}/status`, {}, {
      params: { status },
    })
    unwrapSalesResponse(res)
    const s = findSpuById(id)
    if (s) s.status = Number(status)
  }

  async function setSkuStatus(id, status) {
    const res = await salesHttp.put(`/api/products/sku/${sid(id)}/status`, {}, {
      params: { status },
    })
    unwrapSalesResponse(res)
    const s = findSkuById(id)
    if (s) s.status = Number(status)
  }

  async function saveCategory(payload) {
    const parentId = payload.parent_id ? Number(payload.parent_id) : 0
    const code =
      payload.category_code ||
      `CAT_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 6)}`
    const body = {
      parentId,
      categoryCode: code,
      categoryName: payload.name,
      sortNo: payload.sort != null ? Number(payload.sort) : 0,
      status: 1,
      remark: payload.remark || undefined,
    }
    if (payload.id) {
      const res = await salesHttp.put(`/api/products/categories/${sid(payload.id)}`, body)
      const row = unwrapSalesResponse(res)
      await fetchCategoryTree()
      return categories.value.find((c) => c.id === sid(row.id)) || {
        id: sid(row.id),
        name: row.categoryName,
        category_code: row.categoryCode,
        parent_id: row.parentId ? sid(row.parentId) : null,
        sort: row.sortNo,
      }
    }
    await salesHttp.post('/api/products/categories', body)
    await fetchCategoryTree()
    return categories.value.find((c) => c.category_code === code) || null
  }

  function removeCategory() {
    return false
  }

  return {
    categories,
    spuList,
    skuList,
    loading,
    spuTotal,
    skuTotal,
    skuOptions,
    categoryName,
    findSpuById,
    findSkuById,
    fetchCategoryTree,
    fetchSpuPage,
    fetchSkuPage,
    loadSpuById,
    loadSkuById,
    saveSpu,
    saveSku,
    setSpuStatus,
    setSkuStatus,
    saveCategory,
    removeCategory,
  }
})
