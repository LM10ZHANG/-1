<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useCustomerStore } from '@/stores/customerStore'
import { CUSTOMER_LEVELS, CUSTOMER_TYPES, getEnumLabel } from '@/utils/enums'
import { formatDate } from '@/utils/id'

const router = useRouter()
const store = useCustomerStore()

const keyword = ref('')
const levelFilter = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)

const filtered = computed(() => {
  return store.customers.filter((c) => {
    if (keyword.value) {
      const k = keyword.value.toLowerCase()
      if (
        !c.customer_name.toLowerCase().includes(k) &&
        !c.customer_code.toLowerCase().includes(k)
      ) {
        return false
      }
    }
    if (levelFilter.value && c.customer_level !== levelFilter.value) return false
    if (statusFilter.value !== '' && c.status !== Number(statusFilter.value)) return false
    return true
  })
})

const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

function goDetail(row) {
  router.push(`/customers/${row.id}`)
}

function goEdit(row) {
  router.push(`/customers/${row.id}/edit`)
}

function goNew() {
  router.push('/customers/new')
}

function toggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定${action}客户「${row.customer_name}」吗？`, '操作确认', {
    type: 'warning',
  })
    .then(() => {
      store.setCustomerStatus(row.id, row.status === 1 ? 0 : 1)
      ElMessage.success(`${action}成功`)
    })
    .catch(() => {})
}

function resetFilter() {
  keyword.value = ''
  levelFilter.value = ''
  statusFilter.value = ''
  page.value = 1
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>客户中心</h2>
      <el-button type="primary" :icon="Plus" @click="goNew">新增客户</el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索客户名称 / 编码"
          clearable
          style="width: 240px"
          :prefix-icon="Search"
        />
        <el-select
          v-model="levelFilter"
          placeholder="客户级别"
          clearable
          style="width: 160px"
        >
          <el-option
            v-for="lv in CUSTOMER_LEVELS"
            :key="lv.value"
            :label="lv.label"
            :value="lv.value"
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button @click="resetFilter">重置</el-button>
        <div class="spacer" />
        <span class="detail-label">共 {{ filtered.length }} 条</span>
      </div>

      <el-table :data="paged" stripe border style="width: 100%" row-key="id">
        <el-table-column prop="customer_code" label="客户编码" width="140" />
        <el-table-column label="客户名称" min-width="220">
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="goDetail(row)">
              {{ row.customer_name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="级别" width="110">
          <template #default="{ row }">
            <el-tag
              :type="row.customer_level === 'A' ? 'danger' : row.customer_level === 'B' ? 'warning' : 'info'"
              size="small"
            >
              {{ row.customer_level }} 级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            {{ getEnumLabel(CUSTOMER_TYPES, row.customer_type) }}
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column label="所属地区" width="140">
          <template #default="{ row }">
            {{ row.province }} / {{ row.city }}
          </template>
        </el-table-column>
        <el-table-column prop="follow_status" label="跟进状态" width="120" />
        <el-table-column label="信用额度 / 应收" width="180" align="right">
          <template #default="{ row }">
            <div>{{ row.credit_limit.toLocaleString() }}</div>
            <div style="color: #f56c6c; font-size: 12px">
              应收 {{ row.current_ar_amount.toLocaleString() }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="goDetail(row)">详情</el-button>
            <el-button size="small" link type="primary" @click="goEdit(row)">编辑</el-button>
            <el-button
              size="small"
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
