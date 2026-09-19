<template>
  <div class="header">
    <div class="header-container">
      <div class="app-name">
        <router-link to="/">
          <i class="el-icon-s-shop"></i>
          C2C数字交易平台
        </router-link>
      </div>
      <div class="search-container">
        <el-input
            placeholder="搜索商品..."
            v-model="searchValue"
            @keyup.enter.native="searchIdle"
            class="search-input"
            clearable>
          <el-button slot="append" icon="el-icon-search" @click="searchIdle">搜索</el-button>
        </el-input>
      </div>
      <div class="action-buttons">
        <el-button type="primary" icon="el-icon-plus" class="publish-btn" @click="toRelease">发布商品/公告</el-button>
        <div class="message-btn-container" @click="toMessage">
          <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" class="item">
            <el-button type="info" icon="el-icon-chat-dot-round" class="message-btn">私信</el-button>
          </el-badge>
        </div>

        <router-link v-if="!isLogin" class="user-name-text login-btn" to="/login">
          <i class="el-icon-user"></i> 登录
        </router-link>

        <el-dropdown trigger="click" v-else class="user-dropdown">
          <div class="user-info">
            <div class="user-name">{{nicknameValue?nicknameValue:nickname}}</div>
            <el-avatar :size="32" :src="avatarValue?avatarValue:avatar" class="user-avatar"></el-avatar>
          </div>
          <el-dropdown-menu slot="dropdown" class="dropdown-menu">
            <el-dropdown-item>
              <div @click="toMe" class="dropdown-item">
                <i class="el-icon-user-solid"></i> 个人中心
              </div>
            </el-dropdown-item>
            <el-dropdown-item divided>
              <div @click="loginOut" class="dropdown-item logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script>
import bus from './bus';

export default {
  name: 'Header',
  props: ['searchInput','nicknameValue','avatarValue'],
  data() {
    return {
      searchValue: this.searchInput,
      nickname:'登录',
      avatar:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      isLogin:false,
      unreadCount: 0,
      timer: null,
      isPassiveMode: false
    };
  },
  watch: {
    $route(to, from) {
      // 如果离开消息页面，恢复主动查询模式
      if (from.path === '/message') {
        this.isPassiveMode = false;
        this.getUnread(); // 立即查一次
      }
    }
  },
  created(){
    if(!this.$globalData.userInfo.nickname){
      this.$api.getUserInfo().then(res=>{
        if(res.status_code===1){
          this.nickname=res.data.nickname;
          this.avatar=res.data.avatar;
          res.data.signInTime=res.data.signInTime.substring(0,10);
          this.$globalData.userInfo=res.data;
          this.isLogin=true;
          // 登录成功后初始化消息逻辑
          this.initUnreadLogic();
        }
      })
    }else {
      this.nickname=this.$globalData.userInfo.nickname;
      this.avatar=this.$globalData.userInfo.avatar;
      this.isLogin=true;
      // 已登录则直接初始化
      this.initUnreadLogic();
    }
  },
  beforeDestroy() {
    if(this.timer) clearInterval(this.timer);
    bus.$off('refreshUnread'); // 销毁监听
  },
  methods: {
    // 初始化未读消息逻辑
    initUnreadLogic() {
      this.getUnread();

      // 定时轮询：只有在非被动模式下才执行
      this.timer = setInterval(() => {
        if (!this.isPassiveMode) {
          this.getUnread();
        }
      }, 5000); // 5秒轮询一次

      // 监听事件总线
      bus.$on('refreshUnread', (val) => {
        // 情况1：如果传入了具体数字 (通常来自 message.vue)
        if (typeof val === 'number') {
          this.isPassiveMode = true; // 进入被动模式，停止轮询覆盖
          this.unreadCount = val;
        }
        // 情况2：如果只是通知刷新 (通常来自 chat.vue 或其他)
        else {
          this.isPassiveMode = false; // 恢复主动查询
          this.getUnread();
        }
      });
    },

    // 调用后端接口获取未读数
    getUnread() {
      if(!this.isLogin) return;
      this.$api.getUnreadCount().then(res => {
        if(res.status_code === 1) {
          this.unreadCount = res.data;
        }
      });
    },

    // 搜索跳转
    searchIdle() {
      if(this.searchValue && this.searchValue.trim() !== '') {
        if ('/search' !== this.$route.path) {
          this.$router.push({path: '/search', query: {searchValue: this.searchValue}});
        } else {
          this.$router.replace({path: '/search', query: {searchValue: this.searchValue}});
          this.$router.go(0);
        }
      } else {
        this.$message.warning('请输入搜索关键词');
      }
    },
    toMe() {
      if ('/me' !== this.$route.path) {
        this.$router.push({path: '/me'});
      }
    },
    toMessage(){
      if ('/message' !== this.$route.path) {
        this.$router.push({path: '/message'});
      }
    },
    toRelease(){
      if ('/release' !== this.$route.path) {
        this.$router.push({path: '/release'});
      }
    },
    loginOut(){
      this.$api.logout().then(res=>{
        if(res.status_code===1){
          localStorage.removeItem('token');
          this.$globalData.userInfo={};
          this.$message.success('退出登录成功');
          if ('/index' === this.$route.path) {
            this.$router.go(0);
          }else {
            this.$router.push({path: '/index'});
          }
        }else {
          this.$message.error('网络或系统异常，退出登录失败！');
        }
      });
    }
  }
};
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 60px;
  background: #ffffff;
  display: flex;
  justify-content: center;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.header-container {
  width: 1200px;
  max-width: 95%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.app-name {
  flex-shrink: 0;
  margin-right: 15px;
}

.app-name a {
  color: #409EFF;
  font-size: 20px;
  font-weight: 600;
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.app-name a:hover {
  color: #66b1ff;
}

.app-name i {
  margin-right: 8px;
  font-size: 24px;
}

.search-container {
  flex-grow: 1;
  max-width: 500px;
  margin: 0 20px;
}

.search-input >>> .el-input__inner {
  border-radius: 20px 0 0 20px;
  border-right: none;
}

.search-input >>> .el-input-group__append {
  border-radius: 0 20px 20px 0;
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
  transition: all 0.3s;
  overflow: hidden;
}

.search-input >>> .el-input-group__append .el-button {
  color: white;
  border: none;
  margin: -10px -20px;
  padding: 12px 20px;
}

.search-input >>> .el-input-group__append:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.action-buttons {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.publish-btn {
  margin-right: 10px;
  border-radius: 20px;
}

.message-btn-container {
  margin-right: 10px;
  display: inline-block;
}
.message-btn {
  margin-right: 0 !important;
  border-radius: 20px;
}

.user-name-text {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
  cursor: pointer;
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.user-name-text:hover {
  color: #66b1ff;
}

.login-btn i {
  margin-right: 5px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 0 5px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-name {
  font-size: 15px;
  color: #409EFF;
  margin-right: 8px;
}

.user-avatar {
  border: 2px solid #ebeef5;
}

.dropdown-menu {
  min-width: 130px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.dropdown-item i {
  margin-right: 5px;
}

.logout {
  color: #f56c6c;
}

@media (max-width: 992px) {
  .app-name a { font-size: 18px; }
  .search-container { max-width: 300px; }
  .publish-btn, .message-btn { padding: 9px 12px; }
}

@media (max-width: 768px) {
  .app-name a span { display: none; }
  .app-name i { margin-right: 0; font-size: 22px; }
  .search-container { max-width: 200px; }
  .publish-btn, .message-btn { padding: 7px 10px; font-size: 12px; }
  .user-name { font-size: 14px; margin-right: 5px; }
}
</style>