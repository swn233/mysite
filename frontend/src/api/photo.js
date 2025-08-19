import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: false
})

// 请求拦截器 - 添加token到请求头
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token.startsWith('Bearer ') ? token : `Bearer ${token}`
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
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

/**
 * 获取照片列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getPhotoList = (params) => {
  return api.get('/api/photos/list', { params })
}

/**
 * 获取照片详情
 * @param {Number} id 照片ID
 * @returns {Promise}
 */
export const getPhotoDetail = (id) => {
  return api.get(`/api/photos/${id}`)
}

/**
 * 点赞/取消点赞照片
 * @param {Number} id 照片ID
 * @param {Boolean} liked 是否点赞
 * @returns {Promise}
 */
export const togglePhotoLike = (id, liked) => {
  return api.post(`/api/photos/${id}/like`, { liked })
}

/**
 * 收藏/取消收藏照片
 * @param {Number} id 照片ID
 * @param {Boolean} favorited 是否收藏
 * @returns {Promise}
 */
export const togglePhotoFavorite = (id, favorited) => {
  return api.post(`/api/photos/${id}/favorite`, { favorited })
}

/**
 * 上传照片
 * @param {FormData} formData 表单数据
 * @returns {Promise}
 */
export const uploadPhoto = (formData) => {
  return api.post('/api/photos/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除照片
 * @param {Number} id 照片ID
 * @returns {Promise}
 */
export const deletePhoto = (id) => {
  return api.delete(`/api/photos/${id}`)
}

/**
 * 添加评论
 * @param {Number} photoId 照片ID
 * @param {String} content 评论内容
 * @returns {Promise}
 */
export const addPhotoComment = (photoId, content) => {
  return api.post(`/api/photos/${photoId}/comments`, { content })
}

/**
 * 点赞/取消点赞评论
 * @param {Number} commentId 评论ID
 * @returns {Promise}
 */
export const toggleCommentLike = (commentId) => {
  return api.post(`/api/comments/${commentId}/like`)
}

/**
 * 关注/取消关注用户
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export const toggleFollowUser = (userId) => {
  return api.post(`/api/users/${userId}/follow`)
}

export default {
  getPhotoList,
  getPhotoDetail,
  togglePhotoLike,
  togglePhotoFavorite,
  uploadPhoto,
  deletePhoto,
  addPhotoComment,
  toggleCommentLike,
  toggleFollowUser
}