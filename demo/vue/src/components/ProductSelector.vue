<script setup>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useProductStore } from '@/stores/productStore'
import { formatMoney } from '@/utils/id'

const emit = defineEmits(['select'])

defineProps({
  visible: { type: Boolean, default: false },
})

const dialogVisible = defineModel('visible', { default: false })

const store = useProductStore()
const keyword = ref('')
const multiSelected = ref([])

const options = computed(() => {
  return store.skuOptions.filter((o) => {
    if (!keyword.value) return true
    const k = keyword.value.toLowerCase()
    return (
      o.sku_name.toLowerCase().includes(k) ||
      o.sku_code.toLowerCase().includes(k) ||
      o.spu_name.toLowerCase().includes(k)
    )
  })
})

function confirmSelection() {
  emit('select', [...multiSelected.value])
  multiSelected.value = []
  keyword.value = ''
  dialogVisible.value = false
}
function cancel() {
  multiSelected.value = []
  keyword.value = ''
  dialogVisible.value = false
}
function onRowClick(row) {
  const idx = multiSelected.value.findIndex((x) => x.id === row.id)
  if (idx >= 0) multiSelected.value.splice(idx, 1)
  else multiSelected.value.push(row)
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择商品 SKU"
    width="860px"
    :close-on-click-modal="false"
    @close="cancel"
  >
    <div style="margin-bottom: 12px">
      <el-input
        v-model="keyword"
        placeholder="搜索 SKU 名称 / 编码 / 所属 SPU"
        clearable
        style="width: 320px"
        :prefix-icon="Search"
      />
      <span style="margin-left: 12px; color: #909399">
        已选 {{ multiSelected.length }} 项，只显示启用中的 SKU
      </span>
    </div>
    <el-table
      :data="options"
      @row-click="onRowClick"
      height="420"
      border
      :row-class-name="({ row }) => (multiSelected.some((x) => x.id === row.id) ? 'selected-row' : '')"
    >
      <el-table-column width="50" align="center">
        <template #default="{ row }">
          <el-checkbox :model-value="multiSelected.some((x) => x.id === row.id)" />
        </template>
      </el-table-column>
      <el-table-column prop="sku_code" label="SKU 编码" width="180" />
      <el-table-column prop="sku_name" label="SKU 名称" min-width="220" />
      <el-table-column prop="spu_name" label="所属 SPU" min-width="180" />
      <el-table-column label="销售价" width="120" align="right">
        <template #default="{ row }">¥ {{ formatMoney(row.sale_price) }}</template>
      </el-table-column>
      <el-table-column label="税率" width="80" align="right">
        <template #default="{ row }">{{ row.tax_rate }}%</template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" :disabled="multiSelected.length === 0" @click="confirmSelection">
        确定选择 ({{ multiSelected.length }})
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
:deep(.selected-row) {
  background-color: #ecf5ff;
}
:deep(.el-table__row) {
  cursor: pointer;
}
</style>
