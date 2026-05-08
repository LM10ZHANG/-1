<script setup>
import { reactive, computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useProductStore } from '@/stores/productStore'

const route = useRoute()
const router = useRouter()
const store = useProductStore()

const isEdit = computed(() => Boolean(route.params.id))
const formRef = ref(null)

const form = reactive({
  id: '',
  spu_code: '',
  spu_name: '',
  category_id: '',
  brand_name: '',
  unit_name: '件',
  status: 1,
  description: '',
})

const rules = {
  spu_code: [{ required: true, message: '请输入 SPU 编码', trigger: 'blur' }],
  spu_name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category_id: [{ required: true, message: '请选择分类', trigger: 'change' }],
  unit_name: [{ required: true, message: '请输入单位', trigger: 'blur' }],
}

onMounted(() => {
  if (isEdit.value) {
    const s = store.findSpuById(route.params.id)
    if (!s) {
      ElMessage.error('SPU 不存在')
      router.replace('/products/spu')
      return
    }
    Object.assign(form, s)
  }
})

async function onSubmit() {
  await formRef.value?.validate()
  store.saveSpu({ ...form })
  ElMessage.success(isEdit.value ? '保存成功' : '新增成功')
  router.replace('/products/spu')
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑 SPU' : '新增 SPU' }}</h2>
      <el-button @click="router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        style="max-width: 800px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="SPU 编码" prop="spu_code">
              <el-input v-model="form.spu_code" placeholder="如 SPU-NB-001" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="spu_name">
              <el-input v-model="form.spu_name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="category_id">
              <el-select v-model="form.category_id" style="width: 100%" placeholder="请选择">
                <el-option
                  v-for="c in store.categories"
                  :key="c.id"
                  :label="c.name"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input v-model="form.brand_name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit_name">
              <el-input v-model="form.unit_name" placeholder="如：台 / 件 / 套" />
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
          <el-col :span="24">
            <el-form-item label="商品描述">
              <el-input v-model="form.description" type="textarea" :rows="4" />
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
