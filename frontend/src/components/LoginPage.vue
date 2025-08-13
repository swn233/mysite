<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <img src="../assets/logo.svg" alt="Logo" class="logo" />
        <h2>摄影作品展示平台</h2>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名/邮箱" 
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <div class="remember-forgot">
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码?</el-link>
        </div>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-link">
        <span>还没有账号?</span>
        <router-link to="/register">
          <el-link type="primary" :underline="false">立即注册</el-link>
        </router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '../api/auth'

// 获取路由器实例和当前路由
const router = useRouter()
const route = useRoute()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, message: '用户名长度不能小于3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ]
}

// 登录处理函数
const handleLogin = () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 使用auth API服务发送登录请求
        const response = await login(loginForm.username, loginForm.password)
        // console.log('Login response:', response)
        // console.log('Response headers:', response.headers)
        //
        // 获取JWT令牌 - HTTP头字段名称不区分大小写
        // 尝试从响应头中获取token
        let authHeader = response.headers['authorization'] || response.headers['Authorization']
        
        // 如果响应头中没有token，尝试从响应体中获取
        if (!authHeader && response.data && response.data.token) {
          authHeader = `Bearer ${response.data.token}`
        }
        
        // console.log('Received auth header:', authHeader)

        if (authHeader) {
          // 从授权头中提取token，格式为"Bearer {token}"
           const token = authHeader.startsWith('Bearer ') ? authHeader : `Bearer ${authHeader}`
          
          // 存储令牌 - 保持Bearer前缀，以便请求拦截器使用
          localStorage.setItem('token', token)
          
          // 如果需要记住登录状态，可以设置更长的过期时间
          if (loginForm.remember) {
            // 这里可以添加记住登录的逻辑
          }
          
          // 登录成功提示
          ElMessage.success('登录成功')
          
          // 如果有重定向路径，则跳转到该路径，否则跳转到首页
          const redirectPath = route.query.redirect || '/'
          router.push(redirectPath)
        } else {
          ElMessage.error('登录失败：未获取到授权信息')
        }
      } catch (error) {
        console.error('登录错误:', error)
        console.log('错误响应:', error.response)
        if (error.response) {
          console.log('错误状态:', error.response.status)
          console.log('错误数据:', error.response.data)
          console.log('错误头信息:', error.response.headers)
          // 根据后端返回的错误状态码显示不同的错误信息
          switch (error.response.status) {
            case 401:
              ElMessage.error('用户名或密码错误')
              break
            case 403:
              ElMessage.error('账号已被禁用')
              break
            case 404:
              ElMessage.error('登录服务未找到，请检查API路径')
              break
            case 429:
              ElMessage.error('登录尝试次数过多，请稍后再试')
              break
            case 500:
              ElMessage.error('服务器内部错误')
              break
            default:
              ElMessage.error('登录失败，请稍后重试')
          }
        } else {
          ElMessage.error('网络错误，请检查您的网络连接')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.register-link span {
  margin-right: 5px;
  color: #606266;
}
</style>