<template>
  <div class="home-container">
    <el-header class="header">
      <div class="header-left">
        <div class="logo-container">
          <img src="../assets/logo.svg" alt="Logo" class="logo" />
          <h2 class="site-title">Photos</h2>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link active">精选</router-link>
          <router-link to="/wallpapers" class="nav-link">壁纸</router-link>
          <router-link to="/nature" class="nav-link">自然</router-link>
          <router-link to="/3d" class="nav-link">3D渲染</router-link>
          <router-link to="/textures" class="nav-link">纹理</router-link>
        </div>
      </div>
      
      <div class="header-right">
        <div class="user-actions">
          <el-dropdown>
            <el-avatar :size="32" icon="UserFilled" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="router.push('/profile')">我的作品</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <div class="search-container">
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索高质量的摄影作品和图片"
          prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        >
        </el-input>
      </div>
    </div>
    
    <el-main class="main-content">
      <photo-gallery />
    </el-main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { logout } from '../api/auth'
import { UserFilled, Search } from '@element-plus/icons-vue'
import PhotoGallery from './PhotoGallery.vue'

const router = useRouter()
const searchQuery = ref('')

// 处理搜索
const handleSearch = () => {
  if (!searchQuery.value.trim()) {
    return
  }
  
  // 实际项目中应该跳转到搜索结果页或调用搜索API
  ElMessage.info(`搜索: ${searchQuery.value}`)
  console.log('搜索关键词:', searchQuery.value)
  // router.push(`/search?q=${encodeURIComponent(searchQuery.value)}`)
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await logout()
    // 清除本地存储的token
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    ElMessage.error('退出登录失败')
    // 即使API调用失败，也清除本地token并跳转到登录页
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}


.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #fff;
  height: 62px;
  border-bottom: 1px solid #eee;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.site-title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: #767676;
  font-size: 14px;
  padding: 8px 0;
  position: relative;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #111;
}

.nav-link.active {
  color: #111;
  font-weight: 500;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #111;
}

.user-actions {
  display: flex;
  align-items: center;
}

.search-container {
  max-width: 800px;
  width: 100%;
}

.search-bar {
  margin: 15px 15px 10px 15px;
}

.search-bar :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding: 8px 8px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar :deep(.el-input__inner) {
  height: 20px;
  font-size: 15px;
}

.search-bar :deep(.el-input__prefix) {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
}

.main-content {
  padding-left: 0;
  padding-right: 0;
  padding-top: 0;
}
</style>