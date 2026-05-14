<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useProductStore } from '@/stores/productStore'
import { formatMoney } from '@/utils/id'

const route = useRoute()
const router = useRouter()
const store = useProductStore()

const keyword = ref('')
const spuFilter = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const spuFilterList = ref([])

const activeTab = ref('sku')
function onTabChange(tab) {
  if (tab === 'spu') router.push('/products/spu')
  else if (tab === 'category') router.push('/products/categories')
}

async function load() {
  await store.fetchSkuPage({
    pageNum: page.value,
    pageSize: pageSize.value,
    skuName: keyword.value || undefined,
    spuId: spuFilter.value ? Number(spuFilter.value) : undefined,
    status: statusFilter.value === '' ? undefined : Number(statusFilter.value),
  })
}

onMounted(async () => {
  await store.fetchSpuPage({ pageNum: 1, pageSize: 500 })
  spuFilterList.value = [...store.spuList]
  if (route.query.spu) {
    spuFilter.value = String(route.query.spu)
  }
  await load()
})

watch([page, pageSize], () => {
  load()
})

watch(
  () => route.query.spu,
  (v) => {
    if (v) {
      spuFilter.value = String(v)
      page.value = 1
      load()
    }
  },
)

function spuName(id) {
  return spuFilterList.value.find((s) => s.id === String(id))?.spu_name ?? store.findSpuById(id)?.spu_name ?? '-'
}

function goNew() {
  router.push('/products/sku/new')
}
function goEdit(row) {
  router.push(`/products/sku/${row.id}/edit`)
}
function toggleStatus(row) {
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确定${action} SKU「${row.sku_name}」吗？`, '操作确认', {
    type: 'warning',
  })
    .then(async () => {
      await store.setSkuStatus(row.id, row.status === 1 ? 0 : 1)
      ElMessage.success(`${action}成功`)
      await load()
    })
    .catch(() => {})
}
function resetFilter() {
  keyword.value = ''
  spuFilter.value = ''
  statusFilter.value = ''
  page.value = 1
  load()
}
function search() {
  page.value = 1
  load()
}
function formatSpec(spec) {
  if (!spec) return '-'
  return Object.entries(spec)
    .map(([k, v]) => `${k}:${v}`)
    .join(' / ')
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品中心</h2>
      <el-button type="primary" :icon="Plus" @click="goNew">新增 SKU</el-button>
    </div>

    <el-tabs v-model="activeTab" class="card" style="padding: 8px 20px" @tab-change="onTabChange">
      <el-tab-pane label="SPU 列表" name="spu" />
      <el-tab-pane label="SKU 列表" name="sku" />
      <el-tab-pane label="商品分类" name="category" />
    </el-tabs>

    <div class="card">
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="SKU 名称（服务端）"
          clearable
          style="width: 240px"
          :prefix-icon="Search"
          @keyup.enter="search"
        />
        <el-select v-model="spuFilter" placeholder="所属 SPU" clearable style="width: 220px">
          <el-option
            v-for="s in spuFilterList"
            :key="s.id"
            :label="s.spu_name"
            :value="s.id"
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <div class="spacer" />
        <span class="detail-label">共 {{ store.skuTotal }} 条</span>
      </div>

      <el-table
        :data="store.skuList"
        v-loading="store.loading"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="sku_code" label="SKU 编码" width="200" />
        <el-table-column prop="sku_name" label="SKU 名称" min-width="240" />
        <el-table-column label="所属 SPU" min-width="200">
          <template #default="{ row }">{{ spuName(row.spu_id) }}</template>
        </el-table-column>
        <el-table-column label="规格" width="200">
          <template #default="{ row }">
            <span class="detail-label">{{ formatSpec(row.spec_json) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="销售价" width="120" align="right">
          <template #default="{ row }">¥ {{ formatMoney(row.sale_price) }}</template>
        </el-table-column>
        <el-table-column label="成本价" width="120" align="right">
          <template #default="{ row }">¥ {{ formatMoney(row.cost_price) }}</template>
        </el-table-column>
        <el-table-column label="税率" width="80" align="right">
          <template #default="{ row }">{{ row.tax_rate }}%</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="goEdit(row)">编辑</el-button>
            <el-button
              size="small"
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="store.skuTotal"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>
