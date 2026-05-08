<script setup>
import { computed, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowLeft, ChatLineRound } from '@element-plus/icons-vue'
import { useCustomerStore } from '@/stores/customerStore'
import {
  CUSTOMER_LEVELS,
  CUSTOMER_TYPES,
  CUSTOMER_SOURCES,
  FOLLOW_TYPES,
  getEnumLabel,
} from '@/utils/enums'
import { formatDate, formatDateTime } from '@/utils/id'

const route = useRoute()
const router = useRouter()
const store = useCustomerStore()

const customer = computed(() => store.findById(route.params.id))
const contacts = computed(() => store.listContactsByCustomer(route.params.id))
const followups = computed(() => store.listFollowupsByCustomer(route.params.id))

const contactDialog = ref(false)
const contactForm = reactive({
  id: '',
  customer_id: '',
  name: '',
  mobile: '',
  email: '',
  position: '',
  wechat: '',
  is_primary: 0,
  remark: '',
})
const contactFormRef = ref(null)
const contactRules = {
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  mobile: [{ required: true, message: '请填写手机号', trigger: 'blur' }],
}

function openContact(c) {
  Object.assign(contactForm, {
    id: '',
    customer_id: route.params.id,
    name: '',
    mobile: '',
    email: '',
    position: '',
    wechat: '',
    is_primary: 0,
    remark: '',
  })
  if (c) Object.assign(contactForm, c)
  contactDialog.value = true
}

async function saveContact() {
  await contactFormRef.value?.validate()
  store.saveContact({ ...contactForm })
  ElMessage.success('保存成功')
  contactDialog.value = false
}

function removeContact(c) {
  ElMessageBox.confirm(`确定删除联系人「${c.name}」吗？`, '提示', {
    type: 'warning',
  })
    .then(() => {
      store.removeContact(c.id)
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

const followDialog = ref(false)
const followForm = reactive({
  customer_id: '',
  follow_type: 'CALL',
  content: '',
  next_follow_time: null,
  follow_result: '',
})
const followFormRef = ref(null)
const followRules = {
  content: [{ required: true, message: '请填写跟进内容', trigger: 'blur' }],
  follow_type: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
}

function openFollow() {
  Object.assign(followForm, {
    customer_id: route.params.id,
    follow_type: 'CALL',
    content: '',
    next_follow_time: null,
    follow_result: '',
  })
  followDialog.value = true
}

async function saveFollow() {
  await followFormRef.value?.validate()
  store.addFollowup({
    ...followForm,
    next_follow_time: followForm.next_follow_time
      ? new Date(followForm.next_follow_time).getTime()
      : null,
  })
  ElMessage.success('跟进已登记')
  followDialog.value = false
}

function goEdit() {
  router.push(`/customers/${route.params.id}/edit`)
}
function goBack() {
  router.push('/customers')
}
</script>

<template>
  <div v-if="customer" class="page-container">
    <div class="page-header">
      <div style="display: flex; align-items: center; gap: 12px">
        <el-button link @click="goBack" :icon="ArrowLeft">返回列表</el-button>
        <h2>{{ customer.customer_name }}</h2>
        <el-tag
          :type="customer.customer_level === 'A' ? 'danger' : customer.customer_level === 'B' ? 'warning' : 'info'"
          size="small"
        >
          {{ customer.customer_level }} 级
        </el-tag>
        <el-tag :type="customer.status === 1 ? 'success' : 'info'" size="small">
          {{ customer.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </div>
      <el-button type="primary" @click="goEdit">编辑资料</el-button>
    </div>

    <div class="card">
      <el-descriptions title="基础信息" :column="3" border>
        <el-descriptions-item label="客户编码">{{ customer.customer_code }}</el-descriptions-item>
        <el-descriptions-item label="客户类型">
          {{ getEnumLabel(CUSTOMER_TYPES, customer.customer_type) }}
        </el-descriptions-item>
        <el-descriptions-item label="所属行业">{{ customer.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户来源">
          {{ getEnumLabel(CUSTOMER_SOURCES, customer.source) }}
        </el-descriptions-item>
        <el-descriptions-item label="所属地区">
          {{ customer.province }} / {{ customer.city }}
        </el-descriptions-item>
        <el-descriptions-item label="跟进状态">{{ customer.follow_status }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ customer.owner_user_name }}</el-descriptions-item>
        <el-descriptions-item label="信用额度">
          ¥ {{ customer.credit_limit.toLocaleString() }}
        </el-descriptions-item>
        <el-descriptions-item label="当前应收">
          <span style="color: #f56c6c">¥ {{ customer.current_ar_amount.toLocaleString() }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">
          {{ customer.address || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(customer.created_at) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ customer.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <el-tabs class="card" style="padding: 8px 20px 16px" type="border-card">
      <el-tab-pane label="联系人管理">
        <div style="display: flex; justify-content: flex-end; margin-bottom: 12px">
          <el-button type="primary" size="small" :icon="Plus" @click="openContact(null)">
            新增联系人
          </el-button>
        </div>
        <el-table :data="contacts" border>
          <el-table-column prop="name" label="姓名" width="120">
            <template #default="{ row }">
              {{ row.name }}
              <el-tag
                v-if="row.is_primary"
                type="warning"
                size="small"
                style="margin-left: 6px"
              >
                主联系人
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="mobile" label="手机号" width="140" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="position" label="职位" width="140" />
          <el-table-column prop="wechat" label="微信" width="140" />
          <el-table-column prop="remark" label="备注" min-width="160" />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openContact(row)">编辑</el-button>
              <el-button size="small" link type="danger" @click="removeContact(row)">删除</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <div class="empty-tip">暂无联系人，点击右上角按钮新增</div>
          </template>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="跟进记录">
        <div style="display: flex; justify-content: flex-end; margin-bottom: 12px">
          <el-button type="primary" size="small" :icon="ChatLineRound" @click="openFollow">
            登记跟进
          </el-button>
        </div>
        <el-timeline v-if="followups.length">
          <el-timeline-item
            v-for="f in followups"
            :key="f.id"
            :timestamp="formatDateTime(f.created_at)"
            placement="top"
          >
            <el-card shadow="never">
              <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px">
                <el-tag size="small">{{ getEnumLabel(FOLLOW_TYPES, f.follow_type) }}</el-tag>
                <span class="detail-label">{{ f.follow_user_name }}</span>
                <span v-if="f.follow_result" class="detail-label">
                  · 结果：{{ f.follow_result }}
                </span>
              </div>
              <div style="margin-bottom: 6px">{{ f.content }}</div>
              <div v-if="f.next_follow_time" class="detail-label">
                下次跟进：{{ formatDateTime(f.next_follow_time) }}
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        <div v-else class="empty-tip">暂无跟进记录</div>
      </el-tab-pane>

      <el-tab-pane label="相关报价">
        <div class="empty-tip">
          相关报价模块跳转由「报价中心」承载，可在<el-link type="primary" @click="$router.push('/quotes')">报价列表</el-link>中按客户筛选查看。
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="contactDialog" :title="contactForm.id ? '编辑联系人' : '新增联系人'" width="540px">
      <el-form
        ref="contactFormRef"
        :model="contactForm"
        :rules="contactRules"
        label-width="90px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="contactForm.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="contactForm.mobile" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="contactForm.email" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="contactForm.position" />
        </el-form-item>
        <el-form-item label="微信">
          <el-input v-model="contactForm.wechat" />
        </el-form-item>
        <el-form-item label="主联系人">
          <el-switch
            v-model="contactForm.is_primary"
            :active-value="1"
            :inactive-value="0"
          />
          <span class="detail-label" style="margin-left: 8px">一个客户只允许一位主联系人</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="contactForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialog = false">取消</el-button>
        <el-button type="primary" @click="saveContact">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialog" title="登记跟进" width="540px">
      <el-form
        ref="followFormRef"
        :model="followForm"
        :rules="followRules"
        label-width="90px"
      >
        <el-form-item label="跟进方式" prop="follow_type">
          <el-select v-model="followForm.follow_type" style="width: 100%">
            <el-option
              v-for="f in FOLLOW_TYPES"
              :key="f.value"
              :label="f.label"
              :value="f.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="followForm.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="跟进结果">
          <el-input v-model="followForm.follow_result" placeholder="如：有意向 / 已回复 / 待定" />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="followForm.next_follow_time"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialog = false">取消</el-button>
        <el-button type="primary" @click="saveFollow">保存</el-button>
      </template>
    </el-dialog>
  </div>

  <div v-else class="page-container empty-tip">客户不存在或已被删除</div>
</template>
