<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useQuoteStore } from '@/stores/quoteStore'
import { useCustomerStore } from '@/stores/customerStore'
import { DISCOUNT_APPROVAL_THRESHOLD } from '@/utils/enums'
import { uid } from '@/utils/id'
import CustomerSelector from '@/components/CustomerSelector.vue'
import ProductSelector from '@/components/ProductSelector.vue'
import QuoteItemTable from '@/components/QuoteItemTable.vue'

const route = useRoute()
const router = useRouter()
const store = useQuoteStore()
const customerStore = useCustomerStore()

const isEdit = computed(() => Boolean(route.params.id))
const formRef = ref(null)
const productPickerVisible = ref(false)

const form = reactive({
  id: '',
  customer_id: '',
  customer_name_snapshot: '',
  contact_id: null,
  contact_name_snapshot: '',
  quote_date: Date.now(),
  expire_date: Date.now() + 30 * 86400000,
  payment_term: '',
  delivery_method: '',
  tax_included_flag: 1,
  remark: '',
  items: [],
})

const rules = {
  customer_id: [{ required: true, message: '请选择客户', trigger: 'change' }],
  quote_date: [{ required: true, message: '请选择报价日期', trigger: 'change' }],
  expire_date: [{ required: true, message: '请选择有效期', trigger: 'change' }],
}

const contactOptions = computed(() =>
  form.customer_id ? customerStore.listContactsByCustomer(form.customer_id) : [],
)

onMounted(() => {
  if (isEdit.value) {
    const q = store.findById(route.params.id)
    if (!q) {
      ElMessage.error('报价单不存在')
      router.replace('/quotes')
      return
    }
    Object.assign(form, JSON.parse(JSON.stringify(q)))
  }
})

function onCustomerChange(customer) {
  if (customer) {
    form.customer_name_snapshot = customer.customer_name
    const primary = customerStore.listContactsByCustomer(customer.id).find((c) => c.is_primary)
    if (primary) {
      form.contact_id = primary.id
      form.contact_name_snapshot = primary.name
    } else {
      form.contact_id = null
      form.contact_name_snapshot = ''
    }
  } else {
    form.customer_name_snapshot = ''
    form.contact_id = null
    form.contact_name_snapshot = ''
  }
}

function onContactChange(val) {
  const found = contactOptions.value.find((c) => c.id === val)
  form.contact_name_snapshot = found?.name || ''
}

function onSkuSelected(skus) {
  for (const sku of skus) {
    if (form.items.some((it) => it.sku_id === sku.id)) continue
    form.items.push({
      id: uid('qi_'),
      sku_id: sku.id,
      sku_name_snapshot: sku.sku_name,
      qty: 1,
      origin_unit_price: sku.sale_price,
      discount_rate: 0,
      deal_unit_price: sku.sale_price,
      tax_rate: sku.tax_rate,
      remark: '',
    })
  }
}

function removeItem(index) {
  form.items.splice(index, 1)
}

const hasOverDiscount = computed(() => {
  const threshold = DISCOUNT_APPROVAL_THRESHOLD * 100
  return form.items.some((it) => (Number(it.discount_rate) || 0) > threshold)
})

async function save(action = 'draft') {
  await formRef.value?.validate()
  if (form.items.length === 0) {
    ElMessage.warning('请至少添加一个明细行')
    return
  }

  if (action === 'submit' && hasOverDiscount.value) {
    await ElMessageBox.confirm(
      `存在折扣率超过 ${DISCOUNT_APPROVAL_THRESHOLD * 100}% 的明细行，提交后将进入经理审批流程，是否继续？`,
      '审批提示',
      { type: 'warning' },
    )
  }

  const saved = store.saveQuote({ ...form })
  if (action === 'submit') {
    store.submitApproval(saved.id)
    ElMessage.success('已提交审批')
  } else {
    ElMessage.success('保存为草稿')
  }
  router.replace(`/quotes/${saved.id}`)
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑报价单' : '新建报价单' }}</h2>
      <el-button @click="router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="客户" prop="customer_id">
              <CustomerSelector
                v-model="form.customer_id"
                :disabled="isEdit"
                @change="onCustomerChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系人">
              <el-select
                v-model="form.contact_id"
                placeholder="请选择联系人"
                clearable
                style="width: 100%"
                :disabled="!form.customer_id"
                @change="onContactChange"
              >
                <el-option
                  v-for="c in contactOptions"
                  :key="c.id"
                  :label="c.name + (c.is_primary ? '（主）' : '')"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="负责人">
              <el-input value="张三" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="报价日期" prop="quote_date">
              <el-date-picker
                v-model="form.quote_date"
                type="date"
                style="width: 100%"
                value-format="x"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期至" prop="expire_date">
              <el-date-picker
                v-model="form.expire_date"
                type="date"
                style="width: 100%"
                value-format="x"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否含税">
              <el-radio-group v-model="form.tax_included_flag">
                <el-radio :value="1">含税</el-radio>
                <el-radio :value="0">未税</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款条件">
              <el-input v-model="form.payment_term" placeholder="如：月结 30 天 / 货到付款" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交付方式">
              <el-input v-model="form.delivery_method" placeholder="如：顺丰快递 / 自提" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="card">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px">
        <div style="font-size: 16px; font-weight: 600">报价明细</div>
        <div style="display: flex; gap: 8px; align-items: center">
          <el-tag v-if="hasOverDiscount" type="warning" effect="plain">
            存在超阈值折扣，提交后将进入审批
          </el-tag>
          <el-button type="primary" :icon="Plus" @click="productPickerVisible = true">
            选择商品
          </el-button>
        </div>
      </div>

      <QuoteItemTable
        :items="form.items"
        :tax-included="form.tax_included_flag"
        @remove="removeItem"
      />
    </div>

    <div class="card" style="display: flex; justify-content: flex-end; gap: 12px">
      <el-button @click="router.back()">取消</el-button>
      <el-button @click="save('draft')">保存草稿</el-button>
      <el-button type="primary" @click="save('submit')">保存并提交审批</el-button>
    </div>

    <ProductSelector v-model:visible="productPickerVisible" @select="onSkuSelected" />
  </div>
</template>
