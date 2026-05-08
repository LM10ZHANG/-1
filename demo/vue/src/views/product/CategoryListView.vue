<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useProductStore } from '@/stores/productStore'

const router = useRouter()
const store = useProductStore()

const activeTab = ref('category')
function onTabChange(tab) {
  if (tab === 'spu') router.push('/products/spu')
  else if (tab === 'sku') router.push('/products/sku')
}

const tree = computed(() => {
  const list = store.categories
  const map = new Map()
  list.forEach((c) => map.set(c.id, { ...c, children: [] }))
  const roots = []
  list.forEach((c) => {
    const node = map.get(c.id)
    if (c.parent_id && map.has(c.parent_id)) {
      map.get(c.parent_id).children.push(node)
    } else {
      roots.push(node)
    }
  })
  return roots
})

const dialog = ref(false)
const form = reactive({ id: '', name: '', parent_id: null, sort: 0 })
const formRef = ref(null)
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

function openNew(parent = null) {
  Object.assign(form, { id: '', name: '', parent_id: parent?.id || null, sort: 0 })
  dialog.value = true
}
function openEdit(c) {
  Object.assign(form, { ...c })
  dialog.value = true
}
async function save() {
  await formRef.value?.validate()
  store.saveCategory({ ...form })
  ElMessage.success('保存成功')
  dialog.value = false
}
function remove(c) {
  ElMessageBox.confirm(`确定删除分类「${c.name}」吗？（存在子分类或关联商品时无法删除）`, '提示', {
    type: 'warning',
  })
    .then(() => {
      const ok = store.removeCategory(c.id)
      if (ok) ElMessage.success('删除成功')
      else ElMessage.warning('该分类下存在子分类或商品，无法删除')
    })
    .catch(() => {})
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品中心</h2>
      <el-button type="primary" :icon="Plus" @click="openNew(null)">新增一级分类</el-button>
    </div>

    <el-tabs v-model="activeTab" class="card" style="padding: 8px 20px" @tab-change="onTabChange">
      <el-tab-pane label="SPU 列表" name="spu" />
      <el-tab-pane label="SKU 列表" name="sku" />
      <el-tab-pane label="商品分类" name="category" />
    </el-tabs>

    <div class="card">
      <el-table
        :data="tree"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
        border
      >
        <el-table-column prop="name" label="分类名称" min-width="320" />
        <el-table-column prop="sort" label="排序" width="120" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.parent_id"
              size="small"
              link
              type="primary"
              @click="openNew(row)"
            >
              新增子分类
            </el-button>
            <el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialog" :title="form.id ? '编辑分类' : '新增分类'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-select
            v-model="form.parent_id"
            clearable
            placeholder="无则为一级分类"
            style="width: 100%"
          >
            <el-option
              v-for="c in store.categories.filter((x) => !x.parent_id && x.id !== form.id)"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
