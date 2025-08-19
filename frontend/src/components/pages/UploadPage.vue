<template>
  <div class="upload-page">
    <div class="page-header">
      <h1>上传照片</h1>
      <p>分享你的摄影作品，展示你的创意和视角</p>
    </div>
    
    <photo-upload @upload-success="handleUploadSuccess" />
    
    <div class="upload-tips">
      <h3>上传提示</h3>
      <ul>
        <li>请确保您拥有上传照片的版权或使用权</li>
        <li>支持JPG、PNG、JPEG格式的图片</li>
        <li>图片大小不超过10MB</li>
        <li>请提供准确的照片标题和作者信息</li>
        <li>上传成功后，照片将在审核通过后显示在网站上</li>
      </ul>
    </div>
  </div>
</template>

<script>
import PhotoUpload from '../photo/PhotoUpload.vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'UploadPage',
  components: {
    PhotoUpload
  },
  setup() {
    const router = useRouter()
    
    const handleUploadSuccess = () => {
      ElMessage({
        message: '照片上传成功，即将跳转到首页',
        type: 'success',
        duration: 2000
      })



      // 延迟跳转到首页
      setTimeout(() => {
        router.push('/')
      }, 2000)
    }
    
    return {
      handleUploadSuccess
    }
  }
}
</script>

<style scoped>
.upload-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #333;
}

.page-header p {
  font-size: 1.1rem;
  color: #666;
}

.upload-tips {
  margin-top: 60px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.upload-tips h3 {
  margin-bottom: 15px;
  color: #333;
}

.upload-tips ul {
  padding-left: 20px;
}

.upload-tips li {
  margin-bottom: 10px;
  color: #555;
}
</style>