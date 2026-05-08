<script setup>
import { computed } from 'vue'
import { useCustomerStore } from '@/stores/customerStore'

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '请选择客户' },
})
const emit = defineEmits(['update:modelValue', 'change'])

const store = useCustomerStore()
const options = computed(() => store.customerOptions)

const value = computed({
  get: () => props.modelValue || undefined,
  set: (v) => {
    emit('update:modelValue', v || '')
    const found = store.findById(v)
    emit('change', found)
  },
})
</script>

<template>
  <el-select
    v-model="value"
    filterable
    clearable
    :disabled="disabled"
    :placeholder="placeholder"
    style="width: 100%"
  >
    <el-option
      v-for="opt in options"
      :key="opt.id"
      :label="opt.name"
      :value="opt.id"
    >
      <div style="display: flex; justify-content: space-between; gap: 16px">
        <span>{{ opt.name }}</span>
        <span style="color: #909399; font-size: 12px">{{ opt.code }}</span>
      </div>
    </el-option>
  </el-select>
</template>
