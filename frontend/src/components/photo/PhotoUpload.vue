<template>
  <div class="upload-container">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <h3>上传照片</h3>
        </div>
      </template>
      
      <el-form :model="photoForm" label-position="top">
        <el-form-item label="照片标题" required>
          <el-input v-model="photoForm.title" placeholder="请输入照片标题"></el-input>
        </el-form-item>
        
        <el-form-item label="作者" required>
          <el-input v-model="photoForm.author" placeholder="请输入作者名"></el-input>
        </el-form-item>
        
        <el-form-item label="照片" required>
          <el-upload
            class="photo-uploader"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :file-list="fileList"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 jpg/png/jpeg 格式的图片文件，且不超过 10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <div class="preview-container" v-if="imageUrl">
          <h4>预览</h4>
          <img alt="photo" :src="imageUrl" class="preview-image" />
        </div>
        
        <el-form-item>
          <el-button type="primary" @click="submitUpload" :loading="uploading" :disabled="!canSubmit">
            上传照片
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import {uploadPhoto} from "@/api/user.js";

export default {
  name: 'PhotoUpload',
  emits: ['upload-success'],
  components: {
    UploadFilled
  },
  setup(props,context) {
    const photoForm = ref({
      title: '',
      author: ''
    })
    const { emit } = context
    const fileList = ref([])
    const imageUrl = ref('')
    const uploading = ref(false)
    const photoFile = ref(null)
    
    // 处理文件变化
    const handleFileChange = (file) => {
      const isImage = file.raw.type.startsWith('image/')
      const isLt10M = file.raw.size / 1024 / 1024 < 10
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        fileList.value = []
        return false
      }
      
      if (!isLt10M) {
        ElMessage.error('图片大小不能超过 10MB!')
        fileList.value = []
        return false
      }
      
      photoFile.value = file.raw
      fileList.value = [file]
      
      // 创建预览
      const reader = new FileReader()
      reader.onload = (e) => {
        imageUrl.value = e.target.result
      }
      reader.readAsDataURL(file.raw)
    }
    
    // 是否可以提交
    const canSubmit = computed(() => {
      return photoForm.value.title && 
             photoForm.value.author && 
             photoFile.value
    })
    
    // 提交上传
    const submitUpload = async () => {
      if (!canSubmit.value) {
        ElMessage.warning('请填写所有必填字段并选择照片')
        return
      }
      
      uploading.value = true
      
      try {
        const formData = new FormData()
        formData.append('file', photoFile.value)
        formData.append('title', photoForm.value.title)
        formData.append('author', photoForm.value.author)


        const response = await uploadPhoto(formData)
        
        if (response.data.success) {
          ElMessage.success('照片上传成功')
          // 重置表单
          photoForm.value.title = ''
          photoForm.value.author = ''
          fileList.value = []
          imageUrl.value = ''
          photoFile.value = null
          
          // 触发刷新事件
          emit('upload-success')
        } else {
          ElMessage.error(response.data.message || '上传失败')
        }
      } catch (error) {
        console.error('上传错误:', error)
        ElMessage.error('上传失败: ' + (error.response?.data?.message || error.message || '未知错误'))
      } finally {
        uploading.value = false
      }
    }
    
    return {
      photoForm,
      fileList,
      imageUrl,
      uploading,
      handleFileChange,
      submitUpload,
      canSubmit
    }
  }
}
</script>

<style scoped>
.upload-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.upload-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.photo-uploader {
  width: 100%;
}

.preview-container {
  margin-top: 20px;
  margin-bottom: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>