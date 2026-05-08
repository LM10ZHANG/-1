<script setup>
import { reactive, computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCustomerStore } from '@/stores/customerStore'
import {
  CUSTOMER_LEVELS,
  CUSTOMER_TYPES,
  CUSTOMER_SOURCES,
} from '@/utils/enums'

const route = useRoute()
const router = useRouter()
const store = useCustomerStore()

const isEdit = computed(() => Boolean(route.params.id))
const formRef = ref(null)

const form = reactive({
  id: '',
  customer_code: '',
  customer_name: '',
  customer_level: 'B',
  customer_type: 'ENTERPRISE',
  industry: '',
  source: 'INTRODUCE',
  province: '',
  city: '',
  address: '',
  credit_limit: 0,
  follow_status: '初步接触',
  remark: '',
})

const rules = {
  customer_name: [{ required: true, message: '请填写客户名称', trigger: 'blur' }],
  customer_level: [{ required: true, message: '请选择客户级别', trigger: 'change' }],
  customer_type: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  credit_limit: [
    { required: true, message: '请填写信用额度', trigger: 'blur' },
    { type: 'number', min: 0, message: '信用额度不能为负', trigger: 'blur' },
  ],
}

onMounted(() => {
  if (isEdit.value) {
    const c = store.findById(route.params.id)
    if (!c) {
      ElMessage.error('客户不存在')
      router.replace('/customers')
      return
    }
    Object.assign(form, c)
  }
})

async function onSubmit() {
  await formRef.value?.validate()
  const saved = store.saveCustomer({ ...form })
  ElMessage.success(isEdit.value ? '保存成功' : '新增成功')
  router.replace(`/customers/${saved.id}`)
}

function goBack() {
  router.back()
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑客户' : '新增客户' }}</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <div class="card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        label-position="right"
        style="max-width: 900px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户编码">
              <el-input
                v-model="form.customer_code"
                placeholder="留空由系统自动生成"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customer_name">
              <el-input v-model="form.customer_name" placeholder="请输入客户全称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户级别" prop="customer_level">
              <el-select v-model="form.customer_level" style="width: 100%">
                <el-option
                  v-for="lv in CUSTOMER_LEVELS"
                  :key="lv.value"
                  :label="lv.label"
                  :value="lv.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customer_type">
              <el-select v-model="form.customer_type" style="width: 100%">
                <el-option
                  v-for="t in CUSTOMER_TYPES"
                  :key="t.value"
                  :label="t.label"
                  :value="t.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业">
              <el-input v-model="form.industry" placeholder="如：互联网 / 制造业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户来源">
              <el-select v-model="form.source" style="width: 100%">
                <el-option
                  v-for="s in CUSTOMER_SOURCES"
                  :key="s.value"
                  :label="s.label"
                  :value="s.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="省份">
              <el-input v-model="form.province" placeholder="如：上海" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市">
              <el-input v-model="form.city" placeholder="如：上海市" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="详细地址">
              <el-input v-model="form.address" placeholder="详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用额度" prop="credit_limit">
              <el-input-number
                v-model="form.credit_limit"
                :min="0"
                :step="10000"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跟进状态">
              <el-input v-model="form.follow_status" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="form.remark"
                type="textarea"
                :rows="3"
                placeholder="补充信息"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
