<script setup>
import { computed } from 'vue'
import { formatMoney } from '@/utils/id'

const props = defineProps({
  items: { type: Array, required: true },
  readonly: { type: Boolean, default: false },
  taxIncluded: { type: Number, default: 1 },
})
const emit = defineEmits(['remove'])

function recalc(item) {
  const origin = Number(item.origin_unit_price) || 0
  const rate = Number(item.discount_rate) || 0
  item.deal_unit_price = Number((origin * (1 - rate / 100)).toFixed(2))
}

function lineAmount(item) {
  return (Number(item.deal_unit_price) || 0) * (Number(item.qty) || 0)
}

const totals = computed(() => {
  let subtotal = 0
  let totalTax = 0
  for (const item of props.items) {
    const amount = lineAmount(item)
    subtotal += amount
    const taxRate = Number(item.tax_rate) || 0
    if (props.taxIncluded) {
      totalTax += (amount * taxRate) / (100 + taxRate)
    } else {
      totalTax += (amount * taxRate) / 100
    }
  }
  const untaxed = props.taxIncluded ? subtotal - totalTax : subtotal
  const total = props.taxIncluded ? subtotal : subtotal + totalTax
  return {
    subtotal,
    untaxed: Number(untaxed.toFixed(2)),
    totalTax: Number(totalTax.toFixed(2)),
    total: Number(total.toFixed(2)),
  }
})

defineExpose({ totals })
</script>

<template>
  <div class="quote-items">
    <el-table :data="items" border stripe>
      <el-table-column label="#" type="index" width="50" />
      <el-table-column label="商品 SKU" min-width="240">
        <template #default="{ row }">
          <div style="font-weight: 500">{{ row.sku_name_snapshot }}</div>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="120">
        <template #default="{ row }">
          <el-input-number
            v-if="!readonly"
            v-model="row.qty"
            :min="1"
            :step="1"
            size="small"
            style="width: 100%"
          />
          <span v-else>{{ row.qty }}</span>
        </template>
      </el-table-column>
      <el-table-column label="原单价" width="140">
        <template #default="{ row }">
          <el-input-number
            v-if="!readonly"
            v-model="row.origin_unit_price"
            :min="0"
            :precision="2"
            size="small"
            style="width: 100%"
            @change="recalc(row)"
          />
          <span v-else>¥ {{ formatMoney(row.origin_unit_price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="折扣率(%)" width="120">
        <template #default="{ row }">
          <el-input-number
            v-if="!readonly"
            v-model="row.discount_rate"
            :min="0"
            :max="100"
            :precision="2"
            size="small"
            style="width: 100%"
            @change="recalc(row)"
          />
          <span v-else>{{ row.discount_rate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="成交单价" width="140" align="right">
        <template #default="{ row }">
          <span style="color: #f56c6c">¥ {{ formatMoney(row.deal_unit_price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="税率(%)" width="100">
        <template #default="{ row }">
          <el-input-number
            v-if="!readonly"
            v-model="row.tax_rate"
            :min="0"
            :max="100"
            :precision="2"
            size="small"
            style="width: 100%"
          />
          <span v-else>{{ row.tax_rate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="行金额" width="140" align="right">
        <template #default="{ row }">
          ¥ {{ formatMoney(row.deal_unit_price * row.qty) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="140">
        <template #default="{ row }">
          <el-input v-if="!readonly" v-model="row.remark" size="small" placeholder="可选" />
          <span v-else>{{ row.remark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!readonly" label="操作" width="80" fixed="right">
        <template #default="{ $index }">
          <el-button link type="danger" size="small" @click="emit('remove', $index)">移除</el-button>
        </template>
      </el-table-column>
      <template #empty>
        <div class="empty-tip">暂无明细行，请点击"选择商品"添加</div>
      </template>
    </el-table>

    <div class="total-panel">
      <div class="total-row">
        <span class="label">明细行金额合计：</span>
        <span class="value">¥ {{ formatMoney(totals.subtotal) }}</span>
      </div>
      <div class="total-row">
        <span class="label">其中：{{ taxIncluded ? '含税总额' : '未税总额' }}：</span>
        <span class="value">¥ {{ formatMoney(taxIncluded ? totals.subtotal : totals.untaxed) }}</span>
      </div>
      <div class="total-row">
        <span class="label">税额合计：</span>
        <span class="value">¥ {{ formatMoney(totals.totalTax) }}</span>
      </div>
      <div class="total-row total">
        <span class="label">报价总额：</span>
        <span class="value">¥ {{ formatMoney(totals.total) }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.quote-items {
  position: relative;
}
.total-panel {
  margin-top: 12px;
  padding: 12px 20px;
  background: #fafbfc;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}
.total-row {
  display: flex;
  gap: 12px;
  align-items: baseline;
  min-width: 340px;
  justify-content: space-between;
}
.total-row .label {
  color: var(--text-regular);
}
.total-row .value {
  font-variant-numeric: tabular-nums;
}
.total-row.total .value {
  color: #165dff;
  font-size: 18px;
  font-weight: 600;
}
</style>
