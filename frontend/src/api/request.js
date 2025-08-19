import axios from 'axios'

// 创建axios实例
const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    },
    // 确保axios能够接收到自定义响应头，但不发送凭证
    withCredentials: false
})

// 请求拦截器 - 添加token到请求头
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            // 确保token格式正确（以Bearer开头）
            config.headers['Authorization'] = token.startsWith('Bearer ') ? token : `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器 - 处理错误和token刷新
api.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.response && error.response.status === 401) {
            // 未授权，清除token并重定向到登录页
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)
