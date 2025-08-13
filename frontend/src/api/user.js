import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 将token添加到请求头
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 如果响应状态码是401（未授权），清除token并跳转到登录页
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

/**
 * 获取用户个人信息
 * @returns {Promise}
 */
export function getUserProfile() {
  return api.get('/user/profile')
}

/**
 * 更新用户个人信息
 * @param {Object} data - 用户信息对象
 * @returns {Promise}
 */
export function updateUserProfile(data) {
  return api.put('/user/profile', data)
}

/**
 * 更新用户密码
 * @param {Object} data - 包含旧密码和新密码的对象
 * @returns {Promise}
 */
export function updatePassword(data) {
  return api.put('/user/password', data)
}

/**
 * 上传用户头像
 * @param {FormData} formData - 包含头像文件的FormData对象
 * @returns {Promise}
 */
export function uploadAvatar(formData) {
  return api.post('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取用户作品列表
 * @param {Object} params - 查询参数，如页码、每页数量等
 * @returns {Promise}
 */
export function getUserPhotos(params) {
  return api.get('/user/photos', { params })
}

/**
 * 获取用户收藏列表
 * @param {Object} params - 查询参数，如页码、每页数量等
 * @returns {Promise}
 */
export function getUserCollections(params) {
  return api.get('/user/collections', { params })
}

/**
 * 上传照片
 * @param {FormData} formData - 包含照片文件和元数据的FormData对象
 * @returns {Promise}
 */
export function uploadPhoto(formData) {
  return api.post('/photos', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除照片
 * @param {Number} photoId - 照片ID
 * @returns {Promise}
 */
export function deletePhoto(photoId) {
  return api.delete(`/photos/${photoId}`)
}

/**
 * 取消收藏照片
 * @param {Number} photoId - 照片ID
 * @returns {Promise}
 */
export function removeCollection(photoId) {
  return api.delete(`/collections/${photoId}`)
}

export default {
  getUserProfile,
  updateUserProfile,
  updatePassword,
  uploadAvatar,
  getUserPhotos,
  getUserCollections,
  uploadPhoto,
  deletePhoto,
  removeCollection
}