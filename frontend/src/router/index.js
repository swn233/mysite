import { createRouter, createWebHistory } from 'vue-router'
// 导入页面组件
import Login from '../components/LoginPage.vue'
import Home from '../components/HomePage.vue'
import Register from '../components/RegisterPage.vue'
import PhotoDetail from '../components/PhotoDetail.vue'

// 路由规则配置
const routes = [
    {
        path: '/login',     // 登录页路径
        name: 'Login',      // 路由名称
        component: Login    // 对应组件
    },
    {
        path: '/register',  // 注册页路径
        name: 'Register',    // 路由名称
        component: Register  // 对应组件
    },
    {
        path: '/',         // 首页路径
        name: 'Home',       // 路由名称
        component: Home,    // 对应组件
        meta: { requiresAuth: true } // 需要认证
    },
    {
        path: '/photo/:id', // 照片详情页路径，使用动态路由参数
        name: 'PhotoDetail', // 路由名称
        component: PhotoDetail, // 对应组件
        meta: { requiresAuth: true } // 需要认证
    },
    {
        path: '/profile',   // 用户个人中心页路径
        name: 'UserProfile', // 路由名称
        component: () => import('../components/UserProfile.vue'), // 对应组件
        meta: { requiresAuth: true } // 需要认证
    },
    {
        path: '/:pathMatch(.*)*', // 匹配所有未定义路由
        redirect: '/login'         // 重定向到登录页
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),  // 历史模式（无 # 号）
    routes  // 导入路由规则
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
    // 检查路由是否需要认证
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 检查用户是否已登录
        const token = localStorage.getItem('token')
        if (!token) {
            // 未登录，重定向到登录页
            next({ 
                path: '/login',
                query: { redirect: to.fullPath } // 保存原目标路径
            })
        } else {
            // 已登录，允许访问
            next()
        }
    } else {
        // 不需要认证的路由，直接访问
        next()
    }
})

export default router
