<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="message-container">
        <!-- 顶部导航栏 -->
        <div class="message-header">
          <div class="title" v-if="!currentViewUser">私信列表</div>
          <div class="title-back" v-else @click="backToUserList">
            <i class="el-icon-arrow-left"></i> 返回列表
            <span class="sub-title"> 与 {{ currentViewUser.nickname }} 的对话</span>
          </div>
        </div>

        <!-- 视图 1: 用户聚合列表 (Level 1) -->
        <div v-if="!currentViewUser">
          <div class="search-bar">
            <el-input
                placeholder="搜索用户昵称..."
                v-model="keyword1"
                prefix-icon="el-icon-search"
                clearable
                size="small">
            </el-input>
          </div>

          <div v-if="displayList.length === 0" class="empty-state">
            <i class="el-icon-chat-round"></i>
            <p>没有找到相关记录</p>
          </div>

          <div v-else>
            <div v-for="(group, index) in displayList" :key="index" class="message-container-list" @click="enterUserDetail(group)">
              <div class="message-container-list-left">
                <div class="avatar-wrapper">
                  <el-avatar shape="square" :size="55" :src="group.user.avatar"></el-avatar>
                  <el-badge :value="group.totalUnread" :max="99" :hidden="group.totalUnread === 0" class="group-badge" />
                </div>
                <div class="message-container-list-text">
                  <div class="message-nickname">
                    <span class="name">{{ group.user.nickname }}</span>
                    <span class="chat-date">{{ formatTime(group.lastMsg.createTime) }}</span>
                  </div>
                  <div class="message-content">
                    <span style="color: #409EFF" v-if="group.chats.length > 1">[{{ group.chats.length }}个话题] </span>
                    {{ group.lastMsg.content }}
                  </div>
                </div>
              </div>
              <div class="right-icon"><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>

        <!-- 视图 2: 详细会话列表 (Level 2) -->
        <div v-else>
          <div class="search-bar">
            <el-input
                placeholder="搜索商品名称 / 私信内容..."
                v-model="keyword2"
                prefix-icon="el-icon-search"
                clearable
                size="small">
            </el-input>
          </div>

          <div v-if="currentUserChats.length === 0" class="empty-state">
            <i class="el-icon-search"></i>
            <p>没有找到相关会话</p>
          </div>

          <div v-else>
            <div v-for="(chat, index) in currentUserChats" :key="index" class="message-container-list" @click="toChat(chat)">
              <div class="message-container-list-left">
                <div class="avatar-wrapper">
                  <el-image
                      v-if="chat.idle"
                      style="width: 55px; height: 55px; border-radius: 4px;"
                      :src="getIdleImg(chat.idle.pictureList)"
                      fit="cover">
                  </el-image>
                  <div v-else class="system-icon-box">
                    <i class="el-icon-bell"></i>
                  </div>
                  <el-badge :value="chat.unreadCount" :max="99" :hidden="!chat.unreadCount" class="group-badge" />
                </div>

                <div class="message-container-list-text">
                  <div class="message-nickname">
                    <span class="name" v-if="chat.idle">商品：{{ chat.idle.idleName }}</span>
                    <span class="name" v-else>普通私信</span>
                    <span class="chat-date">{{ formatTime(chat.createTime) }}</span>
                  </div>
                  <div class="message-content">
                    <span v-if="String(chat.userId) === String(myUserId)" class="my-prefix">我：</span>
                    {{ chat.content }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import bus from '../common/bus';

export default {
  name: "message",
  components: { AppHead, AppBody, AppFoot },
  data(){
    return{
      rawChatList: [],
      myUserId: '',
      currentViewUser: null,
      keyword1: '', // 搜索昵称
      keyword2: ''  // 搜索内容/商品
    };
  },
  computed: {
    // Level 1: 按用户聚合
    displayList() {
      const groups = {};
      this.rawChatList.forEach(chat => {
        const otherUser = this.getOtherUser(chat);
        if (!otherUser) return;

        const uid = otherUser.id;
        if (!groups[uid]) {
          groups[uid] = {
            user: otherUser,
            chats: [],
            totalUnread: 0,
            lastMsg: null
          };
        }
        groups[uid].chats.push(chat);
        groups[uid].totalUnread += (chat.unreadCount || 0);

        if (!groups[uid].lastMsg || chat.createTime > groups[uid].lastMsg.createTime) {
          groups[uid].lastMsg = chat;
        }
      });

      let result = Object.values(groups).sort((a, b) => {
        return b.lastMsg.createTime.localeCompare(a.lastMsg.createTime);
      });

      if (this.keyword1) {
        const k = this.keyword1.toLowerCase();
        result = result.filter(g => g.user.nickname.toLowerCase().includes(k));
      }
      return result;
    },

    // Level 2: 详细会话列表
    currentUserChats() {
      if (!this.currentViewUser) return [];

      let chats = this.rawChatList.filter(chat => {
        const u = this.getOtherUser(chat);
        return u && String(u.id) === String(this.currentViewUser.id);
      });

      if (this.keyword2) {
        const k = this.keyword2.toLowerCase();
        chats = chats.filter(chat => {
          const contentMatch = chat.content.toLowerCase().includes(k);
          const idleMatch = chat.idle && chat.idle.idleName.toLowerCase().includes(k);
          return contentMatch || idleMatch;
        });
      }
      return chats;
    }
  },
  created(){
    this.initData();
  },
  methods:{
    initData() {
      const token = localStorage.getItem('token');
      if(!token) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      if (this.$globalData.userInfo && this.$globalData.userInfo.id) {
        this.myUserId = this.$globalData.userInfo.id;
        this.getChatList();
      } else {
        this.$api.getUserInfo().then(res => {
          if (res.status_code === 1) {
            this.myUserId = res.data.id;
            this.$globalData.userInfo = res.data;
            this.getChatList();
          }
        });
      }
    },

    getChatList(){
      this.$api.getChatList().then(res => {
        if(res.status_code === 1){
          let list = res.data;
          let totalUnread = 0;

          for(let i=0; i<list.length; i++) {
            // 时间格式处理
            if(list[i].createTime) {
              list[i].createTime = list[i].createTime.replace("T", " ").substring(0, 16);
            }
            // 累加未读数
            totalUnread += (list[i].unreadCount ? parseInt(list[i].unreadCount) : 0);
          }
          this.rawChatList = list;

          // 自动进入详情逻辑 (处理从Chat页面返回)
          const activeUserId = this.$route.query.activeUser;
          if (activeUserId && !this.currentViewUser) {
            const targetChat = this.rawChatList.find(chat => String(this.getOtherId(chat)) === String(activeUserId));
            if (targetChat) {
              this.currentViewUser = this.getOtherUser(targetChat);
            }
          }
          // 发送具体数字给 Header，强制同步
          bus.$emit('refreshUnread', totalUnread);
        }
      });
    },

    enterUserDetail(group) {
      this.keyword2 = '';
      if (group.chats.length === 1) {
        this.toChat(group.chats[0]);
      } else {
        this.currentViewUser = group.user;
      }
    },

    backToUserList() {
      this.keyword1 = '';
      this.currentViewUser = null;
      // 清除 URL 参数
      if (this.$route.query.activeUser) {
        this.$router.replace({ path: '/message' });
      }
      this.getChatList();
    },

    toChat(chat){
      // 1. 计算除了当前点击的会话之外，还剩多少未读
      let currentTotal = 0;
      this.rawChatList.forEach(c => {
        if (c !== chat) {
          currentTotal += (c.unreadCount || 0);
        }
      });

      // 2. 本地清零
      chat.unreadCount = 0;
      this.$forceUpdate();

      // 3. 立即通知 Header 更新为扣减后的数字
      bus.$emit('refreshUnread', currentTotal);

      // 4. 跳转
      let targetId = this.getOtherId(chat);
      let targetName = this.getOtherNickname(chat);

      this.$router.push({
        path: '/chat',
        query: {
          targetUserId: targetId,
          targetName: targetName,
          idleId: chat.idle ? chat.idle.id : '0'
        }
      });
    },

    getOtherUser(chat) {
      if (String(chat.userId) === String(this.myUserId)) return chat.toU;
      return chat.fromU;
    },
    getOtherNickname(chat) {
      let u = this.getOtherUser(chat);
      return u ? u.nickname : '未知用户';
    },
    getOtherAvatar(chat) {
      let u = this.getOtherUser(chat);
      return u ? u.avatar : '';
    },
    getOtherId(chat) {
      let u = this.getOtherUser(chat);
      return u ? u.id : '';
    },
    getIdleImg(pictureListStr) {
      if(!pictureListStr) return '';
      try {
        let list = JSON.parse(pictureListStr);
        return list.length > 0 ? list[0] : '';
      } catch(e) { return ''; }
    },
    formatTime(timeStr) {
      if(!timeStr) return '';
      return timeStr.replace("T", " ").substring(5, 16);
    }
  }
}
</script>

<style scoped>
.message-container{
  min-height: 85vh;
  padding: 0 20px;
  max-width: 1000px;
  margin: 0 auto;
  background-color: #fff;
}

.message-header {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}
.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.title-back {
  font-size: 16px;
  color: #409EFF;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.sub-title {
  color: #303133;
  margin-left: 10px;
  font-weight: bold;
}

.search-bar {
  padding: 15px 10px 5px 10px;
}

.empty-state { text-align: center; padding: 80px 0; color: #909399; }
.empty-state i { font-size: 60px; margin-bottom: 10px; }

.message-container-list{
  cursor: pointer;
  padding: 20px 10px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s;
}
.message-container-list:hover { background-color: #f9fbfd; }

.message-container-list-left{
  display: flex;
  align-items: center;
  flex: 1;
  overflow: hidden;
}

.avatar-wrapper { margin-right: 15px; flex-shrink: 0; position: relative; }

.group-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

.system-icon-box {
  width: 55px; height: 55px;
  background: #e6a23c;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.message-container-list-text{
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.message-nickname{
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.name { font-weight: 600; font-size: 16px; color: #333; }
.chat-date { font-size: 12px; color: #999; font-weight: normal; }

.message-content{
  font-size: 14px;
  color: #666;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  line-height: 1.4;
  padding-right: 20px;
}
.my-prefix { color: #999; }
.right-icon { color: #ccc; }

@media (max-width: 768px) {
  .message-content { max-width: 200px; }
}
</style>