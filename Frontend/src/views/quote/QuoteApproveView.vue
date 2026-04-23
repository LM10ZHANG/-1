<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useQuoteStore } from '@/stores/quoteStore'
import { QUOTE_STATUS, getEnumLabel, getEnumTagType, DISCOUNT_APPROVAL_THRESHOLD } from '@/utils/enums'
import { formatDate, formatDateTime } from '@/utils/id'
import QuoteItemTable from '@/components/QuoteItemTable.vue'

const route = useRoute()
const router = useRouter()
const store = useQuoteStore()

const quote = computed(() => store.findById(route.params.id))
const comment = ref('')

const canApprove = computed(() => quote.value?.status === 'WAIT_APPROVAL')

const overDiscountItems = computed(() => {
  if (!quote.value) return []
  const threshold = DISCOUNT_APPROVAL_THRESHOLD * 100
  return quote.value.items.filter((it) => (Number(it.discount_rate) || 0) > threshold)
})

function approve() {
  store.approve(quote.value.id, comment.value || '同意')
  ElMessage.success('已通过')
  router.replace(`/quotes/${quote.value.id}`)
}

function reject() {
  if (!comment.value) {
    ElMessage.warning('驳回需要填写审批意见')
    return
  }
  store.reject(quote.value.id, comment.value)
  ElMessage.success('已驳回')
  router.replace(`/quotes/${quote.value.id}`)
}
</script>

<template>
  <div v-if="quote" class="page-container">
    <div class="page-header">
      <div style="display: flex; align-items: center; gap: 12px">
        <el-button link :icon="ArrowLeft" @click="router.push('/quotes')">返回列表</el-button>
        <h2>报价审批 · {{ quote.quote_no }}</h2>
        <el-tag :type="getEnumTagType(QUOTE_STATUS, quote.status)">
          {{ getEnumLabel(QUOTE_STATUS, quote.status) }}
        </el-tag>
      </div>
    </div>

    <el-alert
      v-if="overDiscountItems.length > 0"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template #title>
        本报价存在 {{ overDiscountItems.length }} 个明细行折扣超过 {{ DISCOUNT_APPROVAL_THRESHOLD * 100 }}%，请重点关注
      </template>
    </el-alert>

    <div class="card">
      <el-descriptions title="报价摘要" :column="3" border>
        <el-descriptions-item label="客户">{{ quote.customer_name_snapshot }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ quote.contact_name_snapshot || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ quote.owner_user_name }}</el-descriptions-item>
        <el-descriptions-item label="报价日期">{{ formatDate(quote.quote_date) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">{{ formatDate(quote.expire_date) }}</el-descriptions-item>
        <el-descriptions-item label="是否含税">
          {{ quote.tax_included_flag ? '含税' : '未税' }}
        </el-descriptions-item>
        <el-descriptions-item label="付款条件">{{ quote.payment_term || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交付方式">{{ quote.delivery_method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ quote.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="card">
      <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px">明细</div>
      <QuoteItemTable
        :items="quote.items"
        readonly
        :tax-included="quote.tax_included_flag"
      />
    </div>

    <div class="card">
      <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px">审批意见</div>
      <el-input
        v-model="comment"
        type="textarea"
        :rows="4"
        placeholder="请输入审批意见（驳回时必填）"
        :disabled="!canApprove"
      />
      <div style="margin-top: 16px; display: flex; justify-content: flex-end; gap: 12px">
        <el-button @click="router.push(`/quotes/${quote.id}`)">取消</el-button>
        <el-button type="danger" :disabled="!canApprove" @click="reject">驳回</el-button>
        <el-button type="success" :disabled="!canApprove" @click="approve">通过</el-button>
      </div>
    </div>

    <div class="card" v-if="quote.approvals && quote.approvals.length">
      <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px">历史审批</div>
      <el-timeline>
        <el-timeline-item
          v-for="a in quote.approvals"
          :key="a.id"
          :type="a.action === 'APPROVE' ? 'success' : a.action === 'REJECT' ? 'danger' : 'info'"
          :timestamp="formatDateTime(a.action_time)"
        >
          <div style="font-weight: 500">
            {{ a.approver_user_name }} ·
            {{ a.action === 'APPROVE' ? '通过' : a.action === 'REJECT' ? '驳回' : '系统' }}
          </div>
          <div style="margin-top: 4px; color: #606266">{{ a.comment }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>

  <div v-else class="page-container empty-tip">报价单不存在</div>
</template>
