<template>
  <div id="app">
    <router-view :key="routerKey"></router-view>

    <transition name="fade">
      <div v-if="showKefuBtn" class="kefu-btn" @click="contactSupport">
        <div class="kefu-icon">
          <i class="el-icon-headset"></i>
        </div>
        <div class="kefu-text">联系客服</div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      showKefuBtn: false,
      timer: null
    }
  },
  computed: {
    routerKey() {
      const path = this.$route.path;
      if (path.includes('/platform-admin')) {
        return path;
      }
      return this.$route.fullPath;
    }
  },
  watch: {
    $route: {
      handler(to, from) {
        const hideList = ['/login', '/sign-in', '/login-admin'];
        const isHiddenPage = hideList.includes(to.path);
        const isAdminPage = to.path.includes('/platform-admin');
        const shouldHide = isHiddenPage || isAdminPage;

        if (shouldHide) {
          this.showKefuBtn = false;
          if (this.timer) clearTimeout(this.timer);
          return;
        }

        if (this.timer) clearTimeout(this.timer);
        this.timer = setTimeout(() => {
          const currentPath = this.$route.path;
          const currentIsHide = hideList.includes(currentPath) || currentPath.includes('/platform-admin');
          if (!currentIsHide) {
            this.showKefuBtn = true;
          }
        }, 200);
      },
      immediate: true
    }
  },
  methods: {
    contactSupport() {
      const token = localStorage.getItem('token');
      if (!token) {
        this.$message.warning('请先登录后再联系客服');
        this.$router.push('/login');
        return;
      }
      this.$router.push({
        path: '/chat',
        query: {
          targetUserId: 88,
          targetName: '官方客服',
          idleId: 0
        }
      });
    }
  }
}
</script>

<style>
html, body, #app{
  overflow: visible;
  background-color: #f6f6f6;
  min-height: 100vh;
}

.kefu-btn {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 9999;

  background-color: #409EFF;
  color: #fff;
  width: 45px;
  height: 100px;
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
  box-shadow: -2px 0 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.kefu-btn:hover {
  background-color: #66b1ff;
  width: 50px;
}

.kefu-icon i {
  font-size: 24px;
  margin-bottom: 5px;
}

.kefu-text {
  font-size: 14px;
  writing-mode: vertical-lr;
  letter-spacing: 3px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>