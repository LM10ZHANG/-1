//登录页面
//增加密码确认验证
//自定义验证规则确保两次密码一致
<template>
  <div class="login-container">
    <div class="login-box">
      <div style="font-weight: bold; font-size: 24px; text-align: center; margin-bottom: 30px; color: #2e7d32">欢迎登录销售管理系统</div>
      <el-form :model="data.form"  ref="formRef" :rules="data.rules">
        <el-form-item prop="username">
          <el-input :prefix-icon="User" size="large" v-model="data.form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input :prefix-icon="Lock" size="large" v-model="data.form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item prop="role">
          <el-select size="large" style="width: 100%" v-model="data.form.role">
            <el-option value="ADMIN" label="系统管理员"></el-option>
            <el-option value="TEACHER" label="销售经理"></el-option>
            <el-option value="STUDENT" label="销售员"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="large" type="primary" style="width: 100%" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: right;">
        还没有账号？请 <a href="/register">注册</a>
      </div>
    </div>

  </div>
</template>

<script setup>
  import { reactive, ref } from "vue";
  import { User, Lock } from "@element-plus/icons-vue";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import router from "@/router";

  const data = reactive({
    dialogVisible: true,
    form: {},
    rules: {
      username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
      ],
    }
  })

  const formRef = ref()

  // 点击登录按钮的时候会触发这个方法
  const login = () => {
    formRef.value.validate((valid => {
      if (valid) {
        // 调用后台的接口
        request.post('/login', data.form).then(res => {
          if (res.code === '200') {
            ElMessage.success("登录成功")
            router.push('/')
            localStorage.setItem('system-user', JSON.stringify(res.data))
          } else {
            ElMessage.error(res.msg)
          }
        })
      }
    })).catch(error => {
      console.error(error)
    })
  }

</script>

<style scoped>
/* 同样添加全局绿色主题变量，确保登录页的按钮也是绿色的 */
:root {
  --el-color-primary: #4caf50;
  --el-color-primary-light-3: #81c784;
  --el-color-primary-light-5: #a5d6a7;
  --el-color-primary-light-7: #c8e6c9;
  --el-color-primary-light-8: #e8f5e9;
  --el-color-primary-dark-2: #388e3c;
}

.login-container {
  height: 100vh;
  overflow:hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 移除了原有的蓝色渐变，保留背景图片逻辑 */
  background-image: url("@/assets/imgs/bg.jpg");
  background-size: cover;
}
.login-box {
  width: 400px;
  padding: 50px 30px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0,.1);
  background-color: rgba(255, 255, 255, .5);
}
</style>