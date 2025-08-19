<template>
  <div class="gallery-container">
    <div class="gallery-header">
      <div class="category-tabs">
        <div 
          v-for="category in ['精选', '风景', '人像', '建筑', '街拍', '自然', '旅行']"
          :key="category"
          :class="['tab', { active: activeCategory === category }]"
          @click="switchCategory(category)"
        >
          {{ category }}
        </div>
      </div>
    </div>
    
    <el-row :gutter="24">
      <!-- 推广卡片 -->
      <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
        <promotion-card />
      </el-col>
      
      <!-- 照片卡片 -->
      <el-col 
        v-for="photo in photos" 
        :key="photo.id" 
        :xs="24" 
        :sm="12" 
        :md="8" 
        :lg="6" 
        :xl="4"
      >
        <div class="photo-card">
          <div class="photo-container">
            <img :src="photo.imageUrl" alt="photo.title" class="photo-image" />
            <div class="photo-overlay">
              <div class="overlay-buttons">
                <el-button 
                  :type="photo.favorited ? 'primary' : 'default'" 
                  circle 
                  size="small"
                  @click.stop="toggleFavorite(photo)"
                >
                  <el-icon><Collection v-if="!photo.favorited" /><CollectionTag v-else /></el-icon>
                </el-button>
                <el-button 
                  :type="photo.liked ? 'danger' : 'default'" 
                  circle 
                  size="small"
                  @click.stop="toggleLike(photo)"
                >
                  <el-icon><Star v-if="!photo.liked" /><StarFilled v-else /></el-icon>
                </el-button>
              </div>
              <el-button 
                type="primary" 
                size="small"
                class="download-button"
                @click="viewPhotoDetail(photo.id)"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </div>
          </div>
          
          <div class="photo-info">
            <div class="photo-author-info">
              <div class="author-avatar">
                <el-avatar :size="24" :src="`https://picsum.photos/id/${photo.id + 100}/100/100`" />
              </div>
              <div>
                <div class="photo-title">{{ photo.title }}</div>
                <div class="photo-author">by {{ photo.author }}</div>
              </div>
            </div>
            
            <div class="photo-stats">
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ photo.likes }}</span>
              </div>
              
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ Math.floor(Math.random() * 1000) + 100 }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 加载更多按钮 -->
    <div class="load-more" v-if="hasMorePhotos">
      <el-button 
        type="primary" 
        plain 
        :loading="loading"
        @click="loadMorePhotos"
      >
        加载更多
      </el-button>
    </div>
    
    <!-- 无数据提示 -->
    <el-empty 
      v-if="photos.length === 0" 
      description="暂无摄影作品"
    ></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { Star, StarFilled, Collection, CollectionTag, View } from '@element-plus/icons-vue'
import PromotionCard from '../common/PromotionCard.vue'
import { getPhotoList, togglePhotoLike as apiToggleLike, togglePhotoFavorite as apiToggleFavorite } from '../../api/photo'

const router = useRouter()
const loading = ref(false)
const hasMorePhotos = ref(true)
const currentPage = ref(1)
const pageSize = ref(8)
const totalPhotos = ref(0)
const activeCategory = ref('精选')

// 照片数据
const photos = ref([])

// 加载照片数据
const fetchPhotos = async (isLoadMore = false) => {
  if (!isLoadMore) {
    loading.value = true
  }
  
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      category: activeCategory.value !== '精选' ? activeCategory.value : undefined
    }
    
    const response = await getPhotoList(params)
    console.log(response)
    // 基础服务器地址（根据实际情况修改，如生产环境域名）
    const baseUrl = 'http://localhost:8080';

    if (response && response.data) {
      // 处理数据：给每个imageUrl添加基础服务器地址
      const processedData = response.data.map(photo => {
        // 避免重复添加基础地址（如果后端已返回完整URL可省略此判断）
        if (photo.imageUrl && !photo.imageUrl.startsWith('http')) {
          return {
            ...photo,
            imageUrl: baseUrl + photo.imageUrl
          };
        }
        return photo; // 已包含完整URL则直接返回
      });

      if (isLoadMore) {
        // 加载更多时，追加处理后的数据
        photos.value = [...photos.value, ...processedData];
      } else {
        // 首次加载或切换分类时，替换为处理后的数据
        photos.value = processedData;
      }

      // 判断是否还有更多数据
      hasMorePhotos.value = response.data.length >= pageSize.value;
    }
  } catch (error) {
    console.error('获取照片列表失败:', error)
    ElMessage.error('获取照片列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 加载更多照片
const loadMorePhotos = async () => {
  currentPage.value++
  await fetchPhotos(true)
}

// 查看照片详情
const viewPhotoDetail = (photoId) => {
  router.push(`/photo/${photoId}`)
}

// 点赞/取消点赞
const toggleLike = async (photo) => {
  try {
    // 乐观更新UI
    const originalLiked = photo.liked
    const originalLikes = photo.likes
    
    photo.liked = !photo.liked
    photo.likes += photo.liked ? 1 : -1
    
    // 调用API
    const response = await apiToggleLike(photo.id, photo.liked)
    
    // 成功提示
    ElMessage.success(photo.liked ? '点赞成功' : '已取消点赞')
  } catch (error) {
    // 发生错误时恢复原状态
    photo.liked = !photo.liked
    photo.likes += photo.liked ? 1 : -1
    
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 收藏/取消收藏
const toggleFavorite = async (photo) => {
  try {
    // 乐观更新UI
    const originalFavorited = photo.favorited
    const originalFavorites = photo.favorites
    
    photo.favorited = !photo.favorited
    photo.favorites += photo.favorited ? 1 : -1
    
    // 调用API
    const response = await apiToggleFavorite(photo.id, photo.favorited)
    
    // 成功提示
    ElMessage.success(photo.favorited ? '收藏成功' : '已取消收藏')
  } catch (error) {
    // 发生错误时恢复原状态
    photo.favorited = !photo.favorited
    photo.favorites += photo.favorited ? 1 : -1
    
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// 显示评论
const showComments = (photoId) => {
  // 实际项目中可以打开评论对话框或跳转到评论页面
  ElMessage.info('评论功能开发中...')
}

// 切换分类
const switchCategory = (category) => {
  if (activeCategory.value === category) return
  
  activeCategory.value = category
  currentPage.value = 1 // 重置页码
  fetchPhotos() // 重新获取数据
}

// 组件挂载时加载数据
onMounted(() => {
  fetchPhotos()
})
</script>

<style scoped>
.gallery-container {
  padding: 0 20px 20px;
  max-width: 1320px;
  margin: 0 auto;
}

.gallery-header {
  margin-bottom: 20px;
}

.category-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.tab {
  font-size: 14px;
  color: #767676;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
  transition: color 0.2s;
}

.tab:hover {
  color: #111;
}

.tab.active {
  color: #111;
  font-weight: 500;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #111;
}

.photo-card {
  margin-bottom: 24px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #fff;
  transition: transform 0.3s, box-shadow 0.3s;
}

.photo-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.photo-container {
  position: relative;
  overflow: hidden;
  height: 240px;
  border-radius: 4px;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.photo-card:hover .photo-image {
  transform: scale(1.05);
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.overlay-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.download-button {
  align-self: flex-end;
  border-radius: 4px;
  font-weight: 500;
}

.photo-info {
  padding: 12px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  flex-shrink: 0;
}

.photo-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.photo-author {
  font-size: 12px;
  color: #767676;
}

.photo-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #767676;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .gallery-container {
    padding: 0 15px 15px;
  }
  
  .photo-container {
    height: 200px;
  }
}
</style>