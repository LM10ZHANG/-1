<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useQuoteStore } from '@/stores/quoteStore'
import { useCustomerStore } from '@/stores/customerStore'
import { QUOTE_STATUS, getEnumLabel, getEnumTagType } from '@/utils/enums'
import { formatDate, formatMoney } from '@/utils/id'

const router = useRouter()
const store = useQuoteStore()
const customerStore = useCustomerStore()

const keyword = ref('')
const statusFilter = ref('')
const customerFilter = ref('')
const page = ref(1)
const pageSize = ref(10)

const filtered = computed(() => {
  return store.quotes.filter((q) => {
    if (keyword.value) {
      const k = keyword.value.toLowerCase()
      if (
        !q.quote_no.toLowerCase().includes(k) &&
        !(q.customer_name_snapshot || '').toLowerCase().includes(k)
      ) {
        return false
      }
    }
    if (statusFilter.value && q.status !== statusFilter.value) return false
    if (customerFilter.value && q.customer_id !== customerFilter.value) return false
    return true
  })
})

function quoteTotal(q) {
  return (q.items || []).reduce((sum, it) => sum + it.deal_unit_price * it.qty, 0)
}

const paged = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

function goNew() {
  router.push('/quotes/new')
}
function goDetail(row) {
  router.push(`/quotes/${row.id}`)
}
function goEdit(row) {
  if (['APPROVED', 'VOID'].includes(row.status)) {
    ElMessageBox.confirm(
      '该报价已审批通过或作废，编辑后会重新回到"待审批"状态，是否继续？',
      '提示',
      { type: 'warning' },
    )
      .then(() => router.push(`/quotes/${row.id}/edit`))
      .catch(() => {})
    return
  }
  router.push(`/quotes/${row.id}/edit`)
}
function goApprove(row) {
  router.push(`/quotes/${row.id}/approve`)
}
function voidQuote(row) {
  ElMessageBox.confirm(`确定将报价「${row.quote_no}」作废吗？`, '操作确认', {
    type: 'warning',
  })
    .then(() => {
      store.voidQuote(row.id)
      ElMessage.success('已作废')
    })
    .catch(() => {})
}
function resetFilter() {
  keyword.value = ''
  statusFilter.value = ''
  customerFilter.value = ''
  page.value = 1
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>报价中心</h2>
      <el-button type="primary" :icon="Plus" @click="goNew">新建报价</el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索报价单号 / 客户名称"
          clearable
          style="width: 260px"
          :prefix-icon="Search"
        />
        <el-select v-model="statusFilter" placeholder="报价状态" clearable style="width: 150px">
          <el-option
            v-for="s in QUOTE_STATUS"
            :key="s.value"
            :label="s.label"
            :value="s.value"
          />
        </el-select>
        <el-select
          v-model="customerFilter"
          placeholder="客户"
          clearable
          filterable
          style="width: 220px"
        >
          <el-option
            v-for="c in customerStore.customers"
            :key="c.id"
            :label="c.customer_name"
            :value="c.id"
          />
        </el-select>
        <el-button @click="resetFilter">重置</el-button>
        <div class="spacer" />
        <span class="detail-label">共 {{ filtered.length }} 条</span>
      </div>

      <el-table :data="paged" stripe border style="width: 100%">
        <el-table-column prop="quote_no" label="报价单号" width="160" />
        <el-table-column label="客户" min-width="220">
          <template #default="{ row }">
            {{ row.customer_name_snapshot }}
          </template>
        </el-table-column>
        <el-table-column label="明细行数" width="100" align="right">
          <template #default="{ row }">{{ (row.items || []).length }}</template>
        </el-table-column>
        <el-table-column label="报价总额" width="150" align="right">
          <template #default="{ row }">
            <span style="font-weight: 500">¥ {{ formatMoney(quoteTotal(row)) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getEnumTagType(QUOTE_STATUS, row.status)" size="small">
              {{ getEnumLabel(QUOTE_STATUS, row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报价日期" width="120">
          <template #default="{ row }">{{ formatDate(row.quote_date) }}</template>
        </el-table-column>
        <el-table-column label="有效期至" width="120">
          <template #default="{ row }">{{ formatDate(row.expire_date) }}</template>
        </el-table-column>
        <el-table-column prop="owner_user_name" label="负责人" width="100" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="goDetail(row)">详情</el-button>
            <el-button
              size="small"
              link
              type="primary"
              :disabled="['VOID'].includes(row.status)"
              @click="goEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 'WAIT_APPROVAL'"
              size="small"
              link
              type="warning"
              @click="goApprove(row)"
            >
              去审批
            </el-button>
            <el-button
              v-if="!['VOID'].includes(row.status)"
              size="small"
              link
              type="danger"
              @click="voidQuote(row)"
            >
              作废
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
