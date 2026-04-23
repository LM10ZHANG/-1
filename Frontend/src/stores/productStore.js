import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { uid } from '@/utils/id'

function seedCategories() {
  return [
    { id: 'cat_1', name: '笔记本电脑', parent_id: null, sort: 1 },
    { id: 'cat_2', name: '台式机', parent_id: null, sort: 2 },
    { id: 'cat_3', name: '外设配件', parent_id: null, sort: 3 },
    { id: 'cat_4', name: '键盘', parent_id: 'cat_3', sort: 1 },
    { id: 'cat_5', name: '鼠标', parent_id: 'cat_3', sort: 2 },
    { id: 'cat_6', name: '显示器', parent_id: null, sort: 4 },
  ]
}

function seedSpu() {
  const now = Date.now()
  return [
    {
      id: 'spu_1',
      spu_code: 'SPU-NB-001',
      spu_name: 'ThinkPad X1 Carbon 2025',
      category_id: 'cat_1',
      brand_name: '联想',
      unit_name: '台',
      status: 1,
      description: '14 英寸轻薄商务本，碳纤维机身',
      created_at: now - 90 * 86400000,
    },
    {
      id: 'spu_2',
      spu_code: 'SPU-NB-002',
      spu_name: 'MacBook Pro 14',
      category_id: 'cat_1',
      brand_name: 'Apple',
      unit_name: '台',
      status: 1,
      description: 'M3 芯片，专业级性能',
      created_at: now - 60 * 86400000,
    },
    {
      id: 'spu_3',
      spu_code: 'SPU-KB-001',
      spu_name: '罗技 MX Keys 机械键盘',
      category_id: 'cat_4',
      brand_name: '罗技',
      unit_name: '件',
      status: 1,
      description: '无线全尺寸，背光设计',
      created_at: now - 30 * 86400000,
    },
    {
      id: 'spu_4',
      spu_code: 'SPU-MN-001',
      spu_name: 'Dell U2723QE 27英寸 4K 显示器',
      category_id: 'cat_6',
      brand_name: 'Dell',
      unit_name: '台',
      status: 1,
      description: 'IPS 面板，USB-C 一线连',
      created_at: now - 15 * 86400000,
    },
    {
      id: 'spu_5',
      spu_code: 'SPU-MS-001',
      spu_name: '罗技 MX Master 3S 鼠标',
      category_id: 'cat_5',
      brand_name: '罗技',
      unit_name: '件',
      status: 0,
      description: '已停产，下一代型号上架中',
      created_at: now - 180 * 86400000,
    },
  ]
}

function seedSku() {
  const now = Date.now()
  return [
    {
      id: 'sku_1',
      spu_id: 'spu_1',
      sku_code: 'SKU-NB-001-I7-32G',
      sku_name: 'ThinkPad X1 Carbon i7/32G/1TB',
      spec_json: { cpu: 'i7-1360P', ram: '32G', disk: '1TB' },
      barcode: '6901234567001',
      sale_price: 14999,
      cost_price: 11800,
      tax_rate: 13,
      stock_warn_qty: 5,
      status: 1,
      created_at: now - 89 * 86400000,
    },
    {
      id: 'sku_2',
      spu_id: 'spu_1',
      sku_code: 'SKU-NB-001-I5-16G',
      sku_name: 'ThinkPad X1 Carbon i5/16G/512G',
      spec_json: { cpu: 'i5-1340P', ram: '16G', disk: '512G' },
      barcode: '6901234567002',
      sale_price: 11999,
      cost_price: 9500,
      tax_rate: 13,
      stock_warn_qty: 10,
      status: 1,
      created_at: now - 88 * 86400000,
    },
    {
      id: 'sku_3',
      spu_id: 'spu_2',
      sku_code: 'SKU-MBP14-M3P-18G',
      sku_name: 'MacBook Pro 14 M3 Pro/18G/512G 深空黑',
      spec_json: { cpu: 'M3 Pro', ram: '18G', disk: '512G', color: '深空黑' },
      barcode: '6901234567003',
      sale_price: 19999,
      cost_price: 16500,
      tax_rate: 13,
      stock_warn_qty: 3,
      status: 1,
      created_at: now - 59 * 86400000,
    },
    {
      id: 'sku_4',
      spu_id: 'spu_3',
      sku_code: 'SKU-KB-MXKEYS-BLK',
      sku_name: '罗技 MX Keys 机械键盘 黑色',
      spec_json: { color: '黑色' },
      barcode: '6901234567004',
      sale_price: 899,
      cost_price: 620,
      tax_rate: 13,
      stock_warn_qty: 20,
      status: 1,
      created_at: now - 29 * 86400000,
    },
    {
      id: 'sku_5',
      spu_id: 'spu_4',
      sku_code: 'SKU-DELL-U2723QE',
      sku_name: 'Dell U2723QE 27" 4K',
      spec_json: { size: '27寸', resolution: '3840x2160' },
      barcode: '6901234567005',
      sale_price: 3299,
      cost_price: 2700,
      tax_rate: 13,
      stock_warn_qty: 8,
      status: 1,
      created_at: now - 14 * 86400000,
    },
    {
      id: 'sku_6',
      spu_id: 'spu_5',
      sku_code: 'SKU-LOGI-MX3S',
      sku_name: '罗技 MX Master 3S 石墨黑',
      spec_json: { color: '石墨黑' },
      barcode: '6901234567006',
      sale_price: 699,
      cost_price: 480,
      tax_rate: 13,
      stock_warn_qty: 15,
      status: 0,
      created_at: now - 179 * 86400000,
    },
  ]
}

export const useProductStore = defineStore('product', () => {
  const categories = ref(seedCategories())
  const spuList = ref(seedSpu())
  const skuList = ref(seedSku())
  const loading = ref(false)

  const skuOptions = computed(() =>
    skuList.value
      .filter((s) => s.status === 1)
      .map((s) => ({
        id: s.id,
        sku_code: s.sku_code,
        sku_name: s.sku_name,
        sale_price: s.sale_price,
        tax_rate: s.tax_rate,
        spu_name: spuList.value.find((p) => p.id === s.spu_id)?.spu_name || '',
      })),
  )

  function categoryName(id) {
    return categories.value.find((c) => c.id === id)?.name ?? '-'
  }

  function findSpuById(id) {
    return spuList.value.find((s) => s.id === id) || null
  }

  function findSkuById(id) {
    return skuList.value.find((s) => s.id === id) || null
  }

  function saveSpu(payload) {
    const now = Date.now()
    if (payload.id) {
      const idx = spuList.value.findIndex((s) => s.id === payload.id)
      if (idx >= 0) {
        spuList.value[idx] = { ...spuList.value[idx], ...payload }
        return spuList.value[idx]
      }
    }
    const created = {
      id: uid('spu_'),
      status: 1,
      created_at: now,
      ...payload,
    }
    spuList.value.unshift(created)
    return created
  }

  function saveSku(payload) {
    const now = Date.now()
    if (payload.id) {
      const idx = skuList.value.findIndex((s) => s.id === payload.id)
      if (idx >= 0) {
        skuList.value[idx] = { ...skuList.value[idx], ...payload }
        return skuList.value[idx]
      }
    }
    const created = {
      id: uid('sku_'),
      status: 1,
      created_at: now,
      ...payload,
    }
    skuList.value.unshift(created)
    return created
  }

  function setSpuStatus(id, status) {
    const s = findSpuById(id)
    if (s) s.status = status
  }

  function setSkuStatus(id, status) {
    const s = findSkuById(id)
    if (s) s.status = status
  }

  function saveCategory(payload) {
    if (payload.id) {
      const idx = categories.value.findIndex((c) => c.id === payload.id)
      if (idx >= 0) categories.value[idx] = { ...categories.value[idx], ...payload }
      return categories.value[idx]
    }
    const created = { id: uid('cat_'), parent_id: null, sort: 0, ...payload }
    categories.value.push(created)
    return created
  }

  function removeCategory(id) {
    if (categories.value.some((c) => c.parent_id === id)) return false
    if (spuList.value.some((s) => s.category_id === id)) return false
    const idx = categories.value.findIndex((c) => c.id === id)
    if (idx >= 0) categories.value.splice(idx, 1)
    return true
  }

  return {
    categories,
    spuList,
    skuList,
    loading,
    skuOptions,
    categoryName,
    findSpuById,
    findSkuById,
    saveSpu,
    saveSku,
    setSpuStatus,
    setSkuStatus,
    saveCategory,
    removeCategory,
  }
})
