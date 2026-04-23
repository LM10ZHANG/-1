<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="page-container" style="text-align: center; padding: 80px 0">
    <el-result icon="warning" title="404" sub-title="页面未找到或不在前端 B 职责范围内">
      <template #extra>
        <el-button type="primary" @click="router.push('/customers')">回到客户中心</el-button>
      </template>
    </el-result>
  </div>
</template>
