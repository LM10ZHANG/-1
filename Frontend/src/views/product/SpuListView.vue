<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useProductStore } from '@/stores/productStore'
import { formatDate } from '@/utils/id'

const router = useRouter()
const store = useProductStore()

const keyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)

const activeTab = ref('spu')
function onTabChange(tab) {
  if (tab === 'sku') router.push('/products/sku')
  else if (tab === 'category') router.push('/products/categories')
}

const filtered = computed(() => {
  return store.spuList.filter((s) => {
    if (keyword.value) {
      const k = keyword.value.toLowerCase()
      if (
        !s.spu_name.toLowerCase().includes(k) &&
        !s.spu_code.toLowerCase().includes(k)
      ) {
        return false
      }
    }
    if (categoryFilter.value && s.category_id !== categoryFilter.value) return false
    if (statusFilter.value !== '' && s.status !== Number(statusFilter.value)) return false
    return true
  })
})

const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

function goNew() {
  router.push('/products/spu/new')
}
function goEdit(row) {
  router.push(`/products/spu/${row.id}/edit`)
}
function toggleStatus(row) {
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确定${action} SPU「${row.spu_name}」吗？`, '操作确认', {
    type: 'warning',
  })
    .then(() => {
      store.setSpuStatus(row.id, row.status === 1 ? 0 : 1)
      ElMessage.success(`${action}成功`)
    })
    .catch(() => {})
}
function resetFilter() {
  keyword.value = ''
  categoryFilter.value = ''
  statusFilter.value = ''
  page.value = 1
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品中心</h2>
      <el-button type="primary" :icon="Plus" @click="goNew">新增 SPU</el-button>
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
          placeholder="搜索商品名称 / 编码"
          clearable
          style="width: 240px"
          :prefix-icon="Search"
        />
        <el-select
          v-model="categoryFilter"
          placeholder="商品分类"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="c in store.categories"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button @click="resetFilter">重置</el-button>
        <div class="spacer" />
        <span class="detail-label">共 {{ filtered.length }} 条</span>
      </div>

      <el-table :data="paged" stripe border style="width: 100%">
        <el-table-column prop="spu_code" label="SPU 编码" width="160" />
        <el-table-column prop="spu_name" label="商品名称" min-width="240" />
        <el-table-column label="分类" width="140">
          <template #default="{ row }">{{ store.categoryName(row.category_id) }}</template>
        </el-table-column>
        <el-table-column prop="brand_name" label="品牌" width="120" />
        <el-table-column prop="unit_name" label="单位" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="120">
          <template #default="{ row }">{{ formatDate(row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="goEdit(row)">编辑</el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="router.push(`/products/sku?spu=${row.id}`)"
            >
              查看 SKU
            </el-button>
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
          :total="filtered.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>
