import Vue from 'vue';
import Router from 'vue-router';
import api from '../api/index'; // 引入API用于路由守卫中的用户信息校验

// 解决 ElementUI 导航栏中的 vue-router 在 3.0 版本以上重复点菜单报错问题
const originalReplace = Router.prototype.replace;
Router.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err);
};
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};

Vue.use(Router);

const router = new Router({
    routes: [
        {
            path: '/',
            redirect: '/index'
        },
        {
            path: '/index',
            component: () => import('../components/page/index.vue'),
            meta: { title: 'C2C数字交易平台' }
        },
        {
            path: '/search',
            component: () => import('../components/page/search.vue'),
            meta: { title: '商品搜索 | C2C数字交易平台' }
        },
        {
            path: '/me',
            component: () => import('../components/page/me.vue'),
            meta: { title: '个人中心 | C2C数字交易平台' }
        },
        {
            path: '/message',
            component: () => import('../components/page/message.vue'),
            meta: { title: '私信列表 | C2C数字交易平台' }
        },
        {
            path: '/chat',
            component: () => import('../components/page/chat.vue'),
            meta: { title: '私信聊天 | C2C数字交易平台' }
        },
        {
            path: '/release',
            component: () => import('../components/page/release.vue'),
            meta: { title: '发布商品 | C2C数字交易平台' }
        },
        {
            path: '/details',
            component: () => import('../components/page/idle-details.vue'),
            meta: { title: '商品详情 | C2C数字交易平台' }
        },
        {
            path: '/order',
            component: () => import('../components/page/order.vue'),
            meta: { title: '订单详情 | C2C数字交易平台' }
        },
        {
            path: '/alipay/pay',
            component: () => import('../components/page/alipay-success.vue'),
            meta: { title: '支付成功 | C2C数字交易平台' }
        },
        {
            path: '/login',
            component: () => import('../components/page/login.vue'),
            meta: { title: '登录 | C2C数字交易平台' }
        },
        {
            path: '/sign-in',
            component: () => import('../components/page/sign-in.vue'),
            meta: { title: '注册 | C2C数字交易平台' }
        },
        {
            path: '/login-admin',
            component: () => import('../components/page/login-admin.vue'),
            meta: { title: '管理员登录' }
        },
        {
            path: '/platform-admin',
            component: () => import('../components/page/platform-admin.vue'),
            meta: { title: '后台管理' }
        },
        {
            path: '/pay-result',
            component: () => import('../components/page/pay-result.vue'),
            meta: { title: '支付结果 | C2C数字交易平台' }
        },
        {
            path: '*',
            redirect: '/'
        }
    ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = `${to.meta.title}`;

    // 获取本地存储的 Token
    const token = localStorage.getItem('token');

    // 定义需要登录才能访问的路径白名单
    const authPaths = ['/me', '/message', '/release', '/order', '/chat'];

    const isAdminChat = to.path === '/chat' && to.query.isAdmin === 'true';

    // 1. 权限拦截逻辑
    // 如果要去需要权限的页面 且 没有Token 且 不是管理员聊天模式 -> 强制跳登录
    if (authPaths.includes(to.path) && !token && !isAdminChat) {
        next('/login');
    }
        // 2. 用户信息恢复逻辑 (仅针对普通用户)
    // 如果有 Token 但全局变量里没数据(比如刷新了页面)，且不是管理员模式 -> 重新拉取用户信息
    else if (token && !isAdminChat && (!Vue.prototype.$globalData.userInfo || !Vue.prototype.$globalData.userInfo.nickname)) {
        api.getUserInfo().then(res => {
            if (res.status_code === 1) {
                // 格式化时间并存入全局变量
                if(res.data.signInTime) {
                    res.data.signInTime = res.data.signInTime.substring(0, 10);
                }
                Vue.prototype.$globalData.userInfo = res.data;
                next(); // 获取成功，放行
            } else {
                // Token 无效或过期
                localStorage.removeItem('token');
                next('/login');
            }
        }).catch(e => {
            // 网络错误等异常
            localStorage.removeItem('token');
            next('/login');
        });
    }
    // 3. 其他情况直接放行 (包括管理员模式聊天、访问首页/搜索页等)
    else {
        next();
    }
});

export default router;