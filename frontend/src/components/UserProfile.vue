<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧用户信息卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="6">
        <el-card class="user-card">
          <div class="user-avatar-container">
            <el-avatar :size="100" :src="userInfo.avatar" />
            <el-button 
              size="small" 
              class="edit-avatar-btn"
              @click="handleEditAvatar"
            >
              修改头像
            </el-button>
          </div>
          
          <div class="user-info">
            <h2 class="username">{{ userInfo.username }}</h2>
            <p class="user-bio">{{ userInfo.bio || '这个人很懒，什么都没写~' }}</p>
          </div>
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.photoCount }}</div>
              <div class="stat-label">作品</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.followingCount }}</div>
              <div class="stat-label">关注</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.followerCount }}</div>
              <div class="stat-label">粉丝</div>
            </div>
          </div>
          
          <el-button 
            type="primary" 
            style="width: 100%; margin-top: 20px;"
            @click="handleEditProfile"
          >
            编辑资料
          </el-button>
        </el-card>
        
        <!-- 用户信息详情卡片 -->
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          
          <div class="info-item">
            <el-icon><UserFilled /></el-icon>
            <span class="info-label">用户名：</span>
            <span>{{ userInfo.username }}</span>
          </div>
          
          <div class="info-item">
            <el-icon><Message /></el-icon>
            <span class="info-label">邮箱：</span>
            <span>{{ userInfo.email }}</span>
          </div>
          
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span class="info-label">注册时间：</span>
            <span>{{ userInfo.registerTime }}</span>
          </div>
          
          <div class="info-item">
            <el-icon><Location /></el-icon>
            <span class="info-label">所在地：</span>
            <span>{{ userInfo.location || '未设置' }}</span>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧内容区 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="18">
        <el-card>
          <template #header>
            <div class="content-header">
              <el-tabs v-model="activeTab">
                <el-tab-pane label="我的作品" name="photos">
                  <template #label>
                    <span class="tab-label">
                      <el-icon><Picture /></el-icon>
                      我的作品
                    </span>
                  </template>
                </el-tab-pane>
                
                <el-tab-pane label="我的收藏" name="collections">
                  <template #label>
                    <span class="tab-label">
                      <el-icon><Star /></el-icon>
                      我的收藏
                    </span>
                  </template>
                </el-tab-pane>
                
                <el-tab-pane label="账号设置" name="settings">
                  <template #label>
                    <span class="tab-label">
                      <el-icon><Setting /></el-icon>
                      账号设置
                    </span>
                  </template>
                </el-tab-pane>
              </el-tabs>
              
              <div v-if="activeTab === 'photos'">
                <el-button type="primary" @click="handleUploadPhoto">
                  <el-icon><Upload /></el-icon>
                  上传作品
                </el-button>
              </div>
            </div>
          </template>
          
          <!-- 我的作品 -->
          <div v-if="activeTab === 'photos'">
            <el-empty 
              v-if="userPhotos.length === 0" 
              description="暂无作品，快来上传你的第一张照片吧！"
            >
              <el-button type="primary" @click="handleUploadPhoto">上传作品</el-button>
            </el-empty>
            
            <div v-else>
              <el-row :gutter="20">
                <el-col 
                  v-for="photo in userPhotos" 
                  :key="photo.id" 
                  :xs="24" 
                  :sm="12" 
                  :md="8" 
                  :lg="6" 
                  :xl="4"
                >
                  <el-card class="photo-card" :body-style="{ padding: '0px' }">
                    <div class="photo-container">
                      <img :src="photo.imageUrl" class="photo-image" />
                      <div class="photo-overlay">
                        <div class="overlay-buttons">
                          <el-button 
                            type="primary" 
                            circle 
                            size="small"
                            @click="viewPhotoDetails(photo.id)"
                          >
                            <el-icon><ZoomIn /></el-icon>
                          </el-button>
                          <el-button 
                            type="danger" 
                            circle 
                            size="small"
                            @click="handleDeletePhoto(photo.id)"
                          >
                            <el-icon><Delete /></el-icon>
                          </el-button>
                        </div>
                      </div>
                    </div>
                    
                    <div class="photo-info">
                      <div class="photo-title">{{ photo.title }}</div>
                      <div class="photo-stats">
                        <span><el-icon><View /></el-icon> {{ photo.views }}</span>
                        <span><el-icon><Star /></el-icon> {{ photo.likes }}</span>
                        <span><el-icon><ChatDotRound /></el-icon> {{ photo.comments }}</span>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              
              <div class="load-more-container" v-if="userPhotos.length > 0">
                <el-button 
                  v-if="userPhotos.length < userInfo.photoCount" 
                  type="primary" 
                  plain 
                  :loading="photosLoading" 
                  @click="loadMorePhotos"
                >
                  加载更多
                </el-button>
                <el-divider v-else>已显示全部</el-divider>
              </div>
            </div>
          </div>
          
          <!-- 我的收藏 -->
          <div v-else-if="activeTab === 'collections'">
            <el-empty 
              v-if="userCollections.length === 0" 
              description="暂无收藏，去发现更多精彩作品吧！"
            >
              <el-button type="primary" @click="router.push('/')">浏览作品</el-button>
            </el-empty>
            
            <div v-else>
              <el-row :gutter="20">
                <el-col 
                  v-for="photo in userCollections" 
                  :key="photo.id" 
                  :xs="24" 
                  :sm="12" 
                  :md="8" 
                  :lg="6" 
                  :xl="4"
                >
                  <el-card class="photo-card" :body-style="{ padding: '0px' }">
                    <div class="photo-container">
                      <img :src="photo.imageUrl" class="photo-image" />
                      <div class="photo-overlay">
                        <div class="overlay-buttons">
                          <el-button 
                            type="primary" 
                            circle 
                            size="small"
                            @click="viewPhotoDetails(photo.id)"
                          >
                            <el-icon><ZoomIn /></el-icon>
                          </el-button>
                          <el-button 
                            type="warning" 
                            circle 
                            size="small"
                            @click="handleRemoveCollection(photo.id)"
                          >
                            <el-icon><RemoveFilled /></el-icon>
                          </el-button>
                        </div>
                      </div>
                    </div>
                    
                    <div class="photo-info">
                      <div class="photo-title">{{ photo.title }}</div>
                      <div class="photo-author">by {{ photo.author }}</div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              
              <div class="load-more-container" v-if="userCollections.length > 0">
                <el-button 
                  v-if="userCollections.length < userInfo.collectionCount" 
                  type="primary" 
                  plain 
                  :loading="collectionsLoading" 
                  @click="loadMoreCollections"
                >
                  加载更多
                </el-button>
                <el-divider v-else>已显示全部</el-divider>
              </div>
            </div>
          </div>
          
          <!-- 账号设置 -->
          <div v-else-if="activeTab === 'settings'">
            <el-form 
              :model="settingsForm" 
              :rules="settingsRules" 
              ref="settingsFormRef" 
              label-width="100px"
            >
              <el-form-item label="修改密码" prop="password">
                <el-input 
                  v-model="settingsForm.password" 
                  type="password" 
                  placeholder="输入新密码" 
                  show-password
                ></el-input>
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="settingsForm.confirmPassword" 
                  type="password" 
                  placeholder="再次输入新密码" 
                  show-password
                ></el-input>
              </el-form-item>
              
              <el-form-item label="个人简介" prop="bio">
                <el-input 
                  v-model="settingsForm.bio" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="介绍一下自己吧"
                ></el-input>
              </el-form-item>
              
              <el-form-item label="所在地" prop="location">
                <el-input 
                  v-model="settingsForm.location" 
                  placeholder="填写你的所在地"
                ></el-input>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleSaveSettings">保存设置</el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 编辑个人资料对话框 -->
    <el-dialog
      v-model="editProfileDialogVisible"
      title="编辑个人资料"
      width="500px"
    >
      <el-form :model="editProfileForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editProfileForm.username" disabled></el-input>
          <div class="form-tip">用户名不可修改</div>
        </el-form-item>
        
        <el-form-item label="个人简介">
          <el-input 
            v-model="editProfileForm.bio" 
            type="textarea" 
            :rows="3" 
            placeholder="介绍一下自己吧"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="所在地">
          <el-input 
            v-model="editProfileForm.location" 
            placeholder="填写你的所在地"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editProfileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEditProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 上传照片对话框 -->
    <el-dialog
      v-model="uploadPhotoDialogVisible"
      title="上传摄影作品"
      width="600px"
    >
      <el-form :model="uploadPhotoForm" :rules="uploadPhotoRules" ref="uploadPhotoFormRef" label-width="80px">
        <el-form-item label="作品标题" prop="title">
          <el-input v-model="uploadPhotoForm.title" placeholder="给你的作品起个名字"></el-input>
        </el-form-item>
        
        <el-form-item label="作品描述" prop="description">
          <el-input 
            v-model="uploadPhotoForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="描述一下你的作品"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="拍摄地点" prop="location">
          <el-input v-model="uploadPhotoForm.location" placeholder="填写拍摄地点"></el-input>
        </el-form-item>
        
        <el-form-item label="拍摄设备" prop="camera">
          <el-input v-model="uploadPhotoForm.camera" placeholder="填写拍摄设备"></el-input>
        </el-form-item>
        
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="uploadPhotoForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或创建标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag"
              :label="tag"
              :value="tag"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="上传图片" prop="image">
          <el-upload
            class="photo-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handlePhotoChange"
            :limit="1"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">请上传高质量的图片，支持jpg、png、jpeg格式</div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadPhotoDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPhotoUpload">上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import {
  UserFilled,
  Message,
  Calendar,
  Location,
  Picture,
  Star,
  Setting,
  Upload,
  ZoomIn,
  Delete,
  View,
  ChatDotRound,
  RemoveFilled,
  Plus
} from '@element-plus/icons-vue'
import {
  getUserProfile,
  updateUserProfile,
  updatePassword,
  uploadAvatar,
  getUserPhotos,
  getUserCollections,
  uploadPhoto,
  deletePhoto,
  removeCollection
} from '../api/user'

const router = useRouter()
const activeTab = ref('photos')

// 用户信息
const userInfo = reactive({
  username: '',
  email: '',
  avatar: '',
  bio: '',
  registerTime: '',
  location: '',
  photoCount: 0,
  followingCount: 0,
  followerCount: 0
})

// 用户作品
const userPhotos = ref([])

// 用户收藏
const userCollections = ref([])

// 加载状态
const loading = ref(false)
const photosLoading = ref(false)
const collectionsLoading = ref(false)

// 分页参数
const photosParams = reactive({
  page: 1,
  size: 8
})

const collectionsParams = reactive({
  page: 1,
  size: 8
})

// 获取用户信息
const fetchUserProfile = async () => {
  loading.value = true
  try {
    const response = await getUserProfile()
    const data = response.data.data
    
    // 更新用户信息
    Object.assign(userInfo, {
      username: data.username,
      email: data.email,
      avatar: data.avatar || 'https://picsum.photos/id/1005/200/200', // 默认头像
      bio: data.bio || '',
      registerTime: new Date(data.createTime).toLocaleDateString(),
      location: data.location || '',
      photoCount: data.photoCount || 0,
      followingCount: data.followingCount || 0,
      followerCount: data.followerCount || 0
    })
    
    // 更新表单数据
    settingsForm.bio = userInfo.bio
    settingsForm.location = userInfo.location
    
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 获取用户作品
const fetchUserPhotos = async () => {
  photosLoading.value = true
  try {
    const response = await getUserPhotos(photosParams)
    const data = response.data.data
    
    if (photosParams.page === 1) {
      userPhotos.value = data.records || []
    } else {
      userPhotos.value = [...userPhotos.value, ...(data.records || [])]
    }
    
    // 更新总数
    userInfo.photoCount = data.total || 0
    
  } catch (error) {
    console.error('获取用户作品失败:', error)
    ElMessage.error('获取用户作品失败')
  } finally {
    photosLoading.value = false
  }
}

// 加载更多用户作品
const loadMorePhotos = async () => {
  if (photosLoading.value) return
  
  photoParams.page++
  await fetchUserPhotos()
}

// 获取用户收藏
const fetchUserCollections = async () => {
  collectionsLoading.value = true
  try {
    const response = await getUserCollections({
      page: collectionParams.page,
      size: collectionParams.size
    })
    const data = response.data.data
    
    if (collectionParams.page === 1) {
      userCollections.value = data.records || []
    } else {
      userCollections.value = [...userCollections.value, ...(data.records || [])]
    }
    
    // 更新总数
    userInfo.collectionCount = data.total || 0
    
  } catch (error) {
    console.error('获取用户收藏失败:', error)
    ElMessage.error('获取用户收藏失败')
  } finally {
    collectionsLoading.value = false
  }
}

// 加载更多用户收藏
const loadMoreCollections = async () => {
  if (collectionsLoading.value) return
  
  collectionParams.page++
  await fetchUserCollections()
}

// 账号设置表单
const settingsFormRef = ref(null)
const settingsForm = reactive({
  password: '',
  confirmPassword: '',
  bio: userInfo.bio,
  location: userInfo.location
})

// 账号设置表单验证规则
const settingsRules = {
  password: [
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (value !== settingsForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  bio: [
    { max: 200, message: '个人简介不能超过200个字符', trigger: 'blur' }
  ]
}

// 编辑个人资料对话框
const editProfileDialogVisible = ref(false)
const editProfileForm = reactive({
  username: userInfo.username,
  bio: userInfo.bio,
  location: userInfo.location
})

// 上传照片对话框
const uploadPhotoDialogVisible = ref(false)
const uploadPhotoFormRef = ref(null)
const uploadPhotoForm = reactive({
  title: '',
  description: '',
  location: '',
  camera: '',
  tags: [],
  image: null
})

// 上传照片表单验证规则
const uploadPhotoRules = {
  title: [
    { required: true, message: '请输入作品标题', trigger: 'blur' },
    { max: 50, message: '标题不能超过50个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
  ],
  image: [
    { required: true, message: '请上传图片', trigger: 'change' }
  ]
}

// 可用标签
const availableTags = [
  '风光', '人像', '街拍', '建筑', '自然', '黑白', '城市', '旅行', '动物', '植物', '微距', '夜景'
]

// 查看照片详情
const viewPhotoDetails = (photoId) => {
  router.push(`/photo/${photoId}`)
}

// 处理编辑头像
const handleEditAvatar = () => {
  ElMessage.info('头像编辑功能开发中...')
}

// 处理编辑个人资料
const handleEditProfile = () => {
  editProfileForm.username = userInfo.username
  editProfileForm.bio = userInfo.bio
  editProfileForm.location = userInfo.location
  editProfileDialogVisible.value = true
}

// 保存编辑的个人资料
const saveEditProfile = async () => {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '保存中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    // 准备更新数据
    const updateData = {
      bio: editProfileForm.bio,
      location: editProfileForm.location
    }
    
    // 调用API更新个人资料
    await updateUserProfile(updateData)
    
    // 更新本地用户信息
    userInfo.bio = editProfileForm.bio
    userInfo.location = editProfileForm.location
    
    // 更新设置表单中的值
    settingsForm.bio = editProfileForm.bio
    settingsForm.location = editProfileForm.location
    
    // 关闭对话框
    editProfileDialogVisible.value = false
    
    ElMessage.success('个人资料已更新')
  } catch (error) {
    console.error('更新个人资料失败:', error)
    ElMessage.error('更新个人资料失败')
  } finally {
    loadingInstance.close()
  }
}

// 处理保存账号设置
const handleSaveSettings = () => {
  settingsFormRef.value.validate(async (valid) => {
    if (valid) {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '保存中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      try {
        // 更新个人资料
        if (settingsForm.bio !== userInfo.bio || settingsForm.location !== userInfo.location) {
          await updateUserProfile({
            bio: settingsForm.bio,
            location: settingsForm.location
          })
          
          // 更新本地用户信息
          userInfo.bio = settingsForm.bio
          userInfo.location = settingsForm.location
          
          ElMessage.success('个人资料已更新')
        }
        
        // 如果有修改密码，则处理密码修改
        if (settingsForm.password) {
          await updatePassword({
            newPassword: settingsForm.password
          })
          
          ElMessage.success('密码已更新')
          
          // 清空密码字段
          settingsForm.password = ''
          settingsForm.confirmPassword = ''
        }
      } catch (error) {
        console.error('保存设置失败:', error)
        ElMessage.error('保存设置失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loadingInstance.close()
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  settingsFormRef.value.resetFields()
  settingsForm.bio = userInfo.bio
  settingsForm.location = userInfo.location
}

// 处理上传照片
const handleUploadPhoto = () => {
  // 重置表单
  if (uploadPhotoFormRef.value) {
    uploadPhotoFormRef.value.resetFields()
  }
  
  // 清空表单数据
  Object.assign(uploadPhotoForm, {
    title: '',
    description: '',
    location: '',
    camera: '',
    tags: [],
    image: null
  })
  
  // 显示对话框
  uploadPhotoDialogVisible.value = true
}

// 处理照片选择变化
const handlePhotoChange = (file) => {
  uploadPhotoForm.image = file.raw
}

// 提交照片上传
const submitPhotoUpload = () => {
  uploadPhotoFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!uploadPhotoForm.image) {
        ElMessage.warning('请选择要上传的图片')
        return
      }
      
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '上传中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      try {
        // 创建FormData对象
        const formData = new FormData()
        formData.append('file', uploadPhotoForm.image)
        formData.append('title', uploadPhotoForm.title)
        formData.append('description', uploadPhotoForm.description || '')
        formData.append('location', uploadPhotoForm.location || '')
        formData.append('camera', uploadPhotoForm.camera || '')
        
        // 添加标签
        if (uploadPhotoForm.tags && uploadPhotoForm.tags.length > 0) {
          formData.append('tags', JSON.stringify(uploadPhotoForm.tags))
        }
        
        // 调用API上传照片
        const response = await uploadPhoto(formData)
        const data = response.data.data
        
        ElMessage.success('照片上传成功')
        
        // 重新获取用户作品列表
        photosParams.page = 1
        await fetchUserPhotos()
        
        // 关闭对话框
        uploadPhotoDialogVisible.value = false
      } catch (error) {
        console.error('上传照片失败:', error)
        ElMessage.error('上传照片失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loadingInstance.close()
      }
    }
  })
}

// 处理删除照片
const handleDeletePhoto = (photoId) => {
  ElMessageBox.confirm(
    '确定要删除这张照片吗？此操作不可恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '删除中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 调用API删除照片
      await deletePhoto(photoId)
      
      // 从列表中移除
      userPhotos.value = userPhotos.value.filter(photo => photo.id !== photoId)
      userInfo.photoCount--
      
      ElMessage.success('照片已删除')
    } catch (error) {
      console.error('删除照片失败:', error)
      ElMessage.error('删除照片失败: ' + (error.response?.data?.message || error.message))
    } finally {
      loadingInstance.close()
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理移除收藏
const handleRemoveCollection = (photoId) => {
  ElMessageBox.confirm(
    '确定要取消收藏这张照片吗？',
    '取消收藏',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '取消收藏中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 调用API取消收藏
      await removeCollection(photoId)
      
      // 从列表中移除
      userCollections.value = userCollections.value.filter(photo => photo.id !== photoId)
      
      ElMessage.success('已取消收藏')
    } catch (error) {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败: ' + (error.response?.data?.message || error.message))
    } finally {
      loadingInstance.close()
    }
  }).catch(() => {
    // 取消操作
  })
}

// 组件挂载时获取用户数据
onMounted(async () => {
  // 显示全局加载状态
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    // 获取用户信息
    await fetchUserProfile()
    
    // 获取用户作品
    await fetchUserPhotos()
    
    // 如果当前标签是收藏，则获取用户收藏
    if (activeTab.value === 'collections') {
      await fetchUserCollections()
    }
  } catch (error) {
    console.error('初始化数据失败:', error)
    ElMessage.error('加载数据失败，请刷新页面重试')
  } finally {
    loadingInstance.close()
  }
})

// 监听标签页变化
watch(activeTab, async (newValue) => {
  if (newValue === 'collections' && userCollections.value.length === 0) {
    await fetchUserCollections()
  }
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.user-card {
  margin-bottom: 20px;
  text-align: center;
}

.user-avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  opacity: 0.8;
}

.username {
  margin: 10px 0 5px;
  font-size: 20px;
}

.user-bio {
  color: #666;
  margin-bottom: 15px;
  font-size: 14px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin: 15px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.info-item .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.info-label {
  font-weight: bold;
  margin-right: 5px;
  width: 80px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 5px;
}

.photo-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  overflow: hidden;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.photo-container {
  position: relative;
  overflow: hidden;
  height: 200px;
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
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.overlay-buttons {
  display: flex;
  gap: 10px;
}

.photo-info {
  padding: 12px;
}

.photo-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.photo-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.load-more-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 20px;
}

.photo-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.photo-stats span {
  display: flex;
  align-items: center;
  gap: 3px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.photo-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.photo-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

@media (max-width: 768px) {
  .content-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .user-stats {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .stat-item {
    flex: 1;
    min-width: 80px;
  }
}
</style>