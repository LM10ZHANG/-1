<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useQuoteStore } from '@/stores/quoteStore'
import { QUOTE_STATUS, getEnumLabel, getEnumTagType } from '@/utils/enums'
import { formatDate, formatDateTime, formatMoney } from '@/utils/id'
import QuoteItemTable from '@/components/QuoteItemTable.vue'

const route = useRoute()
const router = useRouter()
const store = useQuoteStore()

const quote = computed(() => store.findById(route.params.id))

function goEdit() {
  if (quote.value.status === 'VOID') {
    ElMessage.warning('已作废的报价不能编辑')
    return
  }
  router.push(`/quotes/${quote.value.id}/edit`)
}

function submitApproval() {
  store.submitApproval(quote.value.id)
  ElMessage.success('已提交审批')
}

function goApprove() {
  router.push(`/quotes/${quote.value.id}/approve`)
}
</script>

<template>
  <div v-if="quote" class="page-container">
    <div class="page-header">
      <div style="display: flex; align-items: center; gap: 12px">
        <el-button link :icon="ArrowLeft" @click="router.push('/quotes')">返回列表</el-button>
        <h2>{{ quote.quote_no }}</h2>
        <el-tag :type="getEnumTagType(QUOTE_STATUS, quote.status)">
          {{ getEnumLabel(QUOTE_STATUS, quote.status) }}
        </el-tag>
      </div>
      <div style="display: flex; gap: 8px">
        <el-button v-if="quote.status === 'DRAFT'" @click="submitApproval">提交审批</el-button>
        <el-button v-if="quote.status === 'WAIT_APPROVAL'" type="warning" @click="goApprove">
          前往审批
        </el-button>
        <el-button type="primary" :disabled="quote.status === 'VOID'" @click="goEdit">
          编辑
        </el-button>
      </div>
    </div>

    <div class="card">
      <el-descriptions title="报价单信息" :column="3" border>
        <el-descriptions-item label="客户">
          <el-link
            type="primary"
            underline="never"
            @click="router.push(`/customers/${quote.customer_id}`)"
          >
            {{ quote.customer_name_snapshot }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ quote.contact_name_snapshot || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="负责人">{{ quote.owner_user_name }}</el-descriptions-item>
        <el-descriptions-item label="报价日期">{{ formatDate(quote.quote_date) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">{{ formatDate(quote.expire_date) }}</el-descriptions-item>
        <el-descriptions-item label="是否含税">
          {{ quote.tax_included_flag ? '含税' : '未税' }}
        </el-descriptions-item>
        <el-descriptions-item label="付款条件">{{ quote.payment_term || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交付方式">{{ quote.delivery_method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(quote.created_at) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ quote.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="card">
      <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px">报价明细</div>
      <QuoteItemTable
        :items="quote.items"
        readonly
        :tax-included="quote.tax_included_flag"
      />
    </div>

    <div class="card">
      <div style="font-size: 16px; font-weight: 600; margin-bottom: 12px">审批轨迹</div>
      <el-timeline v-if="quote.approvals && quote.approvals.length">
        <el-timeline-item
          v-for="a in quote.approvals"
          :key="a.id"
          :type="a.action === 'APPROVE' ? 'success' : a.action === 'REJECT' ? 'danger' : 'info'"
          :timestamp="formatDateTime(a.action_time)"
        >
          <div style="font-weight: 500">
            {{ a.approver_user_name }} ·
            <el-tag
              :type="a.action === 'APPROVE' ? 'success' : a.action === 'REJECT' ? 'danger' : 'info'"
              size="small"
            >
              {{ a.action === 'APPROVE' ? '通过' : a.action === 'REJECT' ? '驳回' : '系统' }}
            </el-tag>
          </div>
          <div style="margin-top: 4px; color: #606266">{{ a.comment }}</div>
        </el-timeline-item>
      </el-timeline>
      <div v-else class="empty-tip">暂无审批记录</div>
    </div>
  </div>

  <div v-else class="page-container empty-tip">报价单不存在</div>
</template>
