<script setup>
import { reactive, computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useProductStore } from '@/stores/productStore'

const route = useRoute()
const router = useRouter()
const store = useProductStore()

const isEdit = computed(() => Boolean(route.params.id))
const formRef = ref(null)

const form = reactive({
  id: '',
  spu_id: '',
  sku_code: '',
  sku_name: '',
  specs: [],
  barcode: '',
  sale_price: 0,
  cost_price: 0,
  tax_rate: 13,
  stock_warn_qty: 0,
  status: 1,
})

const rules = {
  spu_id: [{ required: true, message: '请选择所属 SPU', trigger: 'change' }],
  sku_code: [{ required: true, message: '请输入 SKU 编码', trigger: 'blur' }],
  sku_name: [{ required: true, message: '请输入 SKU 名称', trigger: 'blur' }],
  sale_price: [{ required: true, message: '请输入销售价', trigger: 'blur' }],
}

onMounted(() => {
  if (isEdit.value) {
    const s = store.findSkuById(route.params.id)
    if (!s) {
      ElMessage.error('SKU 不存在')
      router.replace('/products/sku')
      return
    }
    Object.assign(form, {
      ...s,
      specs: Object.entries(s.spec_json || {}).map(([key, value]) => ({ key, value })),
    })
  }
})

function addSpec() {
  form.specs.push({ key: '', value: '' })
}
function removeSpec(idx) {
  form.specs.splice(idx, 1)
}

async function onSubmit() {
  await formRef.value?.validate()
  const spec_json = form.specs
    .filter((s) => s.key)
    .reduce((acc, s) => ({ ...acc, [s.key]: s.value }), {})
  const payload = { ...form, spec_json }
  delete payload.specs
  store.saveSku(payload)
  ElMessage.success(isEdit.value ? '保存成功' : '新增成功')
  router.replace('/products/sku')
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑 SKU' : '新增 SKU' }}</h2>
      <el-button @click="router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        style="max-width: 860px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属 SPU" prop="spu_id">
              <el-select v-model="form.spu_id" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="s in store.spuList"
                  :key="s.id"
                  :label="s.spu_name"
                  :value="s.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU 编码" prop="sku_code">
              <el-input v-model="form.sku_code" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="SKU 名称" prop="sku_name">
              <el-input v-model="form.sku_name" placeholder="如：ThinkPad X1 i7/32G/1TB" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="条码">
              <el-input v-model="form.barcode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch
                v-model="form.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="停用"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="销售价" prop="sale_price">
              <el-input-number
                v-model="form.sale_price"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成本价">
              <el-input-number
                v-model="form.cost_price"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="税率(%)">
              <el-input-number
                v-model="form.tax_rate"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存预警">
              <el-input-number
                v-model="form.stock_warn_qty"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规格属性">
              <div v-for="(s, idx) in form.specs" :key="idx" style="display: flex; gap: 8px; margin-bottom: 8px">
                <el-input v-model="s.key" placeholder="属性名，如 颜色" style="width: 180px" />
                <el-input v-model="s.value" placeholder="属性值，如 黑色" style="width: 220px" />
                <el-button link type="danger" @click="removeSpec(idx)">删除</el-button>
              </div>
              <el-button link type="primary" :icon="Plus" @click="addSpec">新增属性</el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
