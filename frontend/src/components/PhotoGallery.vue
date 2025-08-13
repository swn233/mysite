<template>
  <div class="gallery-container">
    <div class="gallery-header">
      <div class="category-tabs">
        <div class="tab active">精选</div>
        <div class="tab">风景</div>
        <div class="tab">人像</div>
        <div class="tab">建筑</div>
        <div class="tab">街拍</div>
        <div class="tab">自然</div>
        <div class="tab">旅行</div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, Collection, CollectionTag, View } from '@element-plus/icons-vue'
import PromotionCard from './PromotionCard.vue'

const router = useRouter()
const loading = ref(false)
const hasMorePhotos = ref(true)

// 模拟数据 - 实际项目中应该从API获取
const photos = ref([
  {
    id: 1,
    title: '山间日出',
    author: '摄影师A',
    imageUrl: 'https://picsum.photos/id/10/800/600',
    likes: 120,
    favorites: 45,
    comments: 23,
    liked: false,
    favorited: false
  },
  {
    id: 2,
    title: '城市夜景',
    author: '摄影师B',
    imageUrl: 'https://picsum.photos/id/20/800/600',
    likes: 89,
    favorites: 34,
    comments: 12,
    liked: true,
    favorited: false
  },
  {
    id: 3,
    title: '海边落日',
    author: '摄影师C',
    imageUrl: 'https://picsum.photos/id/30/800/600',
    likes: 156,
    favorites: 67,
    comments: 31,
    liked: false,
    favorited: true
  },
  {
    id: 4,
    title: '花卉特写',
    author: '摄影师D',
    imageUrl: 'https://picsum.photos/id/40/800/600',
    likes: 78,
    favorites: 23,
    comments: 9,
    liked: false,
    favorited: false
  }
])

// 加载更多照片
const loadMorePhotos = async () => {
  loading.value = true
  
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟加载更多数据
    const morePhotos = [
      {
        id: 5,
        title: '雪山风光',
        author: '摄影师E',
        imageUrl: 'https://picsum.photos/id/50/800/600',
        likes: 92,
        favorites: 41,
        comments: 15,
        liked: false,
        favorited: false
      },
      {
        id: 6,
        title: '森林小径',
        author: '摄影师F',
        imageUrl: 'https://picsum.photos/id/60/800/600',
        likes: 64,
        favorites: 28,
        comments: 7,
        liked: false,
        favorited: false
      }
    ]
    
    photos.value = [...photos.value, ...morePhotos]
    
    // 模拟没有更多数据了
    if (photos.value.length >= 6) {
      hasMorePhotos.value = false
    }
  } catch (error) {
    console.error('加载更多照片失败:', error)
    ElMessage.error('加载更多照片失败，请重试')
  } finally {
    loading.value = false
  }
}

// 查看照片详情
const viewPhotoDetail = (photoId) => {
  router.push(`/photo/${photoId}`)
}

// 点赞/取消点赞
const toggleLike = (photo) => {
  photo.liked = !photo.liked
  photo.likes += photo.liked ? 1 : -1
  
  // 实际项目中应该调用API
  ElMessage.success(photo.liked ? '点赞成功' : '已取消点赞')
}

// 收藏/取消收藏
const toggleFavorite = (photo) => {
  photo.favorited = !photo.favorited
  photo.favorites += photo.favorited ? 1 : -1
  
  // 实际项目中应该调用API
  ElMessage.success(photo.favorited ? '收藏成功' : '已取消收藏')
}

// 显示评论
const showComments = (photoId) => {
  // 实际项目中可以打开评论对话框或跳转到评论页面
  ElMessage.info('评论功能开发中...')
}

// 组件挂载时加载数据
onMounted(() => {
  // 实际项目中应该从API获取数据
  console.log('PhotoGallery组件已挂载')
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
  padding: 16px;
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