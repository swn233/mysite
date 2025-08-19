<template>
  <div class="photo-detail-container">
    <div class="back-button">
      <el-button @click="goBack" icon="ArrowLeft" size="small">返回</el-button>
    </div>
    
    <el-row :gutter="20" v-if="photo">
      <!-- 照片展示区 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="16">
        <div class="photo-showcase">
          <img :src="photo.imageUrl" class="main-photo" />
          
          <div class="photo-metadata">
            <div class="photo-title">{{ photo.title }}</div>
            <div class="photo-info">
              <span>{{ photo.createTime }}</span>
              <span>{{ photo.camera }}</span>
              <span>{{ photo.location }}</span>
            </div>
            
            <div class="photo-description">
              {{ photo.description }}
            </div>
            
            <div class="photo-tags">
              <el-tag 
                v-for="tag in photo.tags" 
                :key="tag"
                size="small"
                effect="plain"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-col>
      
      <!-- 作者信息和互动区 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
        <div class="author-section">
          <el-card>
            <div class="author-info">
              <el-avatar :size="50" :src="photo.authorAvatar" />
              <div class="author-details">
                <div class="author-name">{{ photo.author }}</div>
                <div class="author-bio">{{ photo.authorBio }}</div>
              </div>
            </div>
            
            <el-button 
              type="primary" 
              :plain="!photo.isFollowing" 
              style="width: 100%; margin-top: 15px;"
              @click="toggleFollow"
            >
              {{ photo.isFollowing ? '已关注' : '关注作者' }}
            </el-button>
          </el-card>
          
          <el-card class="interaction-card">
            <div class="interaction-buttons">
              <div class="interaction-button">
                <el-button 
                  :type="photo.liked ? 'danger' : 'default'" 
                  :icon="photo.liked ? 'Star' : 'StarFilled'" 
                  circle
                  @click="toggleLike"
                ></el-button>
                <span>{{ photo.likes }} 赞</span>
              </div>
              
              <div class="interaction-button">
                <el-button 
                  :type="photo.collected ? 'warning' : 'default'" 
                  :icon="photo.collected ? 'Collection' : 'CollectionTag'" 
                  circle
                  @click="toggleCollect"
                ></el-button>
                <span>{{ photo.collections }} 收藏</span>
              </div>
              
              <div class="interaction-button">
                <el-button 
                  type="info" 
                  icon="Share" 
                  circle
                  @click="sharePhoto"
                ></el-button>
                <span>分享</span>
              </div>
            </div>
          </el-card>
          
          <!-- 评论区 -->
          <el-card class="comments-card">
            <template #header>
              <div class="comments-header">
                <span>评论 ({{ photo.comments.length }})</span>
              </div>
            </template>
            
            <div class="comment-input">
              <el-input
                v-model="newComment"
                placeholder="写下你的评论..."
                :rows="2"
                type="textarea"
              />
              <el-button 
                type="primary" 
                @click="addComment"
                :disabled="!newComment.trim()"
                style="margin-top: 10px;"
              >
                发表评论
              </el-button>
            </div>
            
            <div class="comments-list" v-if="photo.comments.length > 0">
              <div 
                v-for="comment in photo.comments" 
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-author">
                  <el-avatar :size="32" :src="comment.avatar" />
                  <div class="comment-info">
                    <div class="comment-name">{{ comment.username }}</div>
                    <div class="comment-time">{{ comment.time }}</div>
                  </div>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button 
                    text 
                    size="small"
                    @click="likeComment(comment.id)"
                  >
                    {{ comment.liked ? '已赞' : '赞' }} ({{ comment.likes }})
                  </el-button>
                  <el-button 
                    text 
                    size="small"
                    @click="replyComment(comment.id)"
                  >
                    回复
                  </el-button>
                </div>
              </div>
            </div>
            
            <el-empty 
              v-else 
              description="暂无评论，快来发表第一条评论吧！"
              :image-size="100"
            ></el-empty>
          </el-card>
        </div>
      </el-col>
    </el-row>
    
    <!-- 加载中状态 -->
    <div v-else-if="loading" class="loading-container">
      <el-skeleton style="width: 100%" animated>
        <template #template>
          <div style="display: flex; flex-direction: column; gap: 20px;">
            <el-skeleton-item variant="image" style="width: 100%; height: 400px" />
            <el-skeleton-item variant="p" style="width: 60%" />
            <el-skeleton-item variant="text" style="width: 30%" />
            <el-skeleton-item variant="p" style="width: 100%" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 照片不存在 -->
    <el-result 
      v-else 
      icon="error"
      title="照片不存在"
      sub-title="您查看的照片可能已被删除或不存在"
    >
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star, StarFilled, Collection, CollectionTag, Share } from '@element-plus/icons-vue'
import { getPhotoDetail, togglePhotoLike, togglePhotoFavorite, addPhotoComment, toggleCommentLike, toggleFollowUser } from '../../api/photo'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const photo = ref(null)
const newComment = ref('')

// 获取照片详情
const fetchPhotoDetail = async () => {
  const photoId = route.params.id
  
  try {
    loading.value = true
    
    // 调用API获取照片详情
    const response = await getPhotoDetail(photoId)
    
    if (response && response.data && response.data.success) {
      // 从API响应中获取照片数据
      photo.value = response.data.photo
      
      // 如果后端没有提供评论数据，初始化为空数组
      if (!photo.value.comments) {
        photo.value.comments = []
      }
      
      // 如果后端没有提供标签数据，初始化为空数组
      if (!photo.value.tags) {
        photo.value.tags = []
      }
    } else {
      photo.value = null
      ElMessage.error('照片不存在或已被删除')
    }
  } catch (error) {
    console.error('获取照片详情失败:', error)
    ElMessage.error('获取照片详情失败，请重试')
    photo.value = null
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 点赞/取消点赞
const toggleLike = async () => {
  if (!photo.value) return
  
  try {
    // 调用API进行点赞/取消点赞操作
    const response = await togglePhotoLike(photo.value.id)
    
    if (response && response.data && response.data.success) {
      // 更新本地状态
      photo.value.liked = !photo.value.liked
      photo.value.likes += photo.value.liked ? 1 : -1
      
      ElMessage.success(photo.value.liked ? '点赞成功' : '已取消点赞')
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 收藏/取消收藏
const toggleCollect = async () => {
  if (!photo.value) return
  
  try {
    // 调用API进行收藏/取消收藏操作
    const response = await togglePhotoFavorite(photo.value.id)
    
    if (response && response.data && response.data.success) {
      // 更新本地状态
      photo.value.collected = !photo.value.collected
      photo.value.collections += photo.value.collected ? 1 : -1
      
      ElMessage.success(photo.value.collected ? '收藏成功' : '已取消收藏')
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 关注/取消关注作者
const toggleFollow = async () => {
  if (!photo.value || !photo.value.authorId) return
  
  try {
    // 调用API关注/取消关注作者
    const response = await toggleFollowUser(photo.value.authorId)
    
    if (response && response.data && response.data.success) {
      // 更新本地状态
      photo.value.isFollowing = !photo.value.isFollowing
      
      ElMessage.success(photo.value.isFollowing ? '已关注作者' : '已取消关注')
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 分享照片
const sharePhoto = () => {
  // 实际项目中可以实现分享功能
  ElMessage.info('分享功能开发中...')
}

// 添加评论
const addComment = async () => {
  if (!photo.value || !newComment.value.trim()) return
  
  try {
    // 调用API添加评论
    const response = await addPhotoComment(photo.value.id, newComment.value)
    
    if (response && response.data && response.data.success) {
      // 如果后端返回了新评论数据，使用后端返回的数据
      if (response.data.comment) {
        photo.value.comments.unshift(response.data.comment)
      } else {
        // 否则使用本地创建的评论数据
        const comment = {
          id: Date.now(), // 临时ID
          username: '当前用户', // 实际项目中应该使用当前登录用户信息
          avatar: 'https://picsum.photos/id/1004/100/100',
          content: newComment.value,
          time: new Date().toLocaleString(),
          likes: 0,
          liked: false
        }
        
        // 添加到评论列表
        photo.value.comments.unshift(comment)
      }
      
      // 清空输入框
      newComment.value = ''
      
      ElMessage.success('评论发表成功')
    } else {
      ElMessage.error('评论发表失败，请稍后重试')
    }
  } catch (error) {
    console.error('添加评论失败:', error)
    ElMessage.error('评论发表失败，请稍后重试')
  }
}

// 点赞评论
const likeComment = async (commentId) => {
  if (!photo.value) return
  
  const comment = photo.value.comments.find(c => c.id === commentId)
  if (!comment) return
  
  try {
    // 调用API点赞/取消点赞评论
    const response = await toggleCommentLike(commentId)
    
    if (response && response.data && response.data.success) {
      // 更新本地状态
      comment.liked = !comment.liked
      comment.likes += comment.liked ? 1 : -1
      
      ElMessage.success(comment.liked ? '评论点赞成功' : '已取消评论点赞')
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } catch (error) {
    console.error('评论点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 回复评论
const replyComment = (commentId) => {
  if (!photo.value) return
  
  const comment = photo.value.comments.find(c => c.id === commentId)
  if (comment) {
    // 在评论框中添加回复前缀
    newComment.value = `@${comment.username} `
    
    // 聚焦评论框
    document.querySelector('.comment-input textarea').focus()
  }
}

// 组件挂载时获取照片详情
onMounted(() => {
  fetchPhotoDetail()
})
</script>

<style scoped>
.photo-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-button {
  margin-bottom: 20px;
}

.photo-showcase {
  margin-bottom: 20px;
}

.main-photo {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.photo-metadata {
  margin-top: 20px;
}

.photo-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.photo-info {
  display: flex;
  gap: 15px;
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.photo-description {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #333;
}

.photo-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag-item {
  margin-right: 0;
}

.author-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-details {
  flex: 1;
}

.author-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
}

.author-bio {
  font-size: 14px;
  color: #666;
}

.interaction-card {
  margin-top: 0;
}

.interaction-buttons {
  display: flex;
  justify-content: space-between;
}

.interaction-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.interaction-button span {
  font-size: 14px;
  color: #666;
}

.comments-card {
  margin-top: 0;
}

.comments-header {
  font-weight: bold;
}

.comment-input {
  margin-bottom: 20px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.comment-info {
  flex: 1;
}

.comment-name {
  font-weight: bold;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin-bottom: 8px;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.loading-container {
  padding: 20px;
}

@media (max-width: 768px) {
  .interaction-buttons {
    flex-direction: column;
    gap: 15px;
  }
  
  .interaction-button {
    flex-direction: row;
    justify-content: center;
  }
}
</style>