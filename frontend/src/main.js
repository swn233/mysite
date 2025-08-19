
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'  // 导入路由实例
// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios";
// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080'
// 创建应用实例
const app = createApp(App)
app.use(router)
// 使用Element Plus
app.use(ElementPlus)

// 挂载应用
app.mount('#app')
