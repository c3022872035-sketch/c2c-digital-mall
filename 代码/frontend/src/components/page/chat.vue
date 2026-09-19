<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="chat-container">
        <el-card class="box-card chat-box" :body-style="{ padding: '0px', flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }">
          <!-- 1. 顶部标题栏 -->
          <div slot="header" class="clearfix chat-header">
            <div class="header-left">
              <span>与 <strong>{{ targetUser.nickname }}</strong> 的对话</span>

              <!-- 管理员模式标识 -->
              <el-tag v-if="isAdminMode" type="danger" size="mini" effect="dark" style="margin-left: 10px;">
                管理员模式
              </el-tag>

              <!-- 商品信息 Tag (点击跳转) -->
              <el-tag
                  size="medium"
                  type="info"
                  v-if="idleInfo.idleName && String(idleId) !== '0'"
                  class="idle-tag clickable-tag"
                  @click.native="toDetail">
                <i class="el-icon-goods"></i> 商品：{{ idleInfo.idleName }}
              </el-tag>

              <el-tag
                  size="medium"
                  type="warning"
                  v-if="String(idleId) === '0'"
                  class="idle-tag">
                <i class="el-icon-service"></i> 官方客服/系统通知
              </el-tag>
            </div>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回</el-button>
          </div>

          <!-- 2. 聊天记录区域 -->
          <div class="chat-content" ref="chatContent">
            <!-- 空状态 -->
            <div v-if="chatHistory.length === 0" class="no-msg">
              <i class="el-icon-chat-dot-round"></i>
              <p>暂无沟通记录，打个招呼吧~</p>
            </div>

            <div v-for="(msg, index) in chatHistory" :key="index">
              <!-- 时间戳 (每5条或第一条显示) -->
              <div class="msg-time-tip" v-if="shouldShowTime(index)">{{ formatTime(msg.createTime) }}</div>

              <!-- 对方的消息 (左侧) -->
              <div class="msg-item msg-left" v-if="String(msg.userId) !== String(myUserId)">
                <el-avatar :size="40" :src="msg.fromU.avatar" class="msg-avatar"></el-avatar>
                <div class="msg-bubble">
                  <!-- 图片消息 -->
                  <el-image
                      v-if="isImage(msg.content)"
                      :src="msg.content"
                      :preview-src-list="[msg.content]"
                      class="chat-image">
                  </el-image>
                  <!-- 文本消息 -->
                  <div v-else class="msg-text">{{ msg.content }}</div>
                </div>
              </div>

              <!-- 我的消息 (右侧) -->
              <div class="msg-item msg-right" v-else>
                <div class="msg-bubble primary">
                  <!-- 图片消息 -->
                  <el-image
                      v-if="isImage(msg.content)"
                      :src="msg.content"
                      :preview-src-list="[msg.content]"
                      class="chat-image">
                  </el-image>
                  <!-- 文本消息 -->
                  <div v-else class="msg-text">{{ msg.content }}</div>
                </div>
                <el-avatar :size="40" :src="msg.fromU.avatar" class="msg-avatar"></el-avatar>
              </div>
            </div>
          </div>

          <!-- 3. 底部输入区域 -->
          <div class="chat-input-area">
            <!-- 工具栏：图片上传 -->
            <div class="chat-tools">
              <el-upload
                  action="http://localhost:8080/file/"
                  :show-file-list="false"
                  :on-success="handleImageSuccess"
                  :before-upload="beforeImageUpload"
                  accept="image/*"
                  class="upload-demo">
                <el-button type="text" icon="el-icon-picture-outline">发送图片</el-button>
              </el-upload>
            </div>
            <!-- 输入框 -->
            <div class="input-wrapper">
              <el-input
                  type="textarea"
                  :rows="3"
                  resize="none"
                  placeholder="请输入消息... (Enter发送)"
                  v-model="inputMsg"
                  @keyup.enter.native="sendMsg">
              </el-input>
              <el-button type="primary" size="small" class="send-btn" @click="sendMsg" icon="el-icon-s-promotion">发送</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';
import bus from "@/components/common/bus";

export default {
  name: "chat",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      myUserId: '',
      targetUserId: '',
      idleId: '',
      idleInfo: { idleName: '' },
      targetUser: { nickname: '加载中...', avatar: '' },
      chatHistory: [],
      inputMsg: '',

      // 模式控制
      isAdminMode: false,
      adminVirtualId: '88',

      // 轮询定时器
      pollingTimer: null
    };
  },
  created() {
    // 1. 获取参数并判断模式
    this.isAdminMode = this.$route.query.isAdmin === 'true';
    this.targetUserId = this.$route.query.targetUserId;
    this.idleId = this.$route.query.idleId || 0;

    // 2. 身份初始化
    this.initUserInfo();

    // 3. 设置对方初步昵称
    if(this.$route.query.targetName){
      this.targetUser.nickname = this.$route.query.targetName;
    }

    // 4. 获取关联商品信息
    if(this.idleId && this.idleId != 0){
      this.getIdleInfo();
    }
  },
  mounted() {
    // 5. 启动实时轮询
    this.startRealtimePolling();
  },
  beforeDestroy() {
    // 6. 关键：离开页面时必须销毁定时器
    this.stopPolling();
  },
  updated() {
    // 数据更新后自动滚动到底部
    this.$nextTick(() => {
      this.scrollToBottom();
    });
  },
  methods: {
    // 身份初始化逻辑
    initUserInfo() {
      if (this.isAdminMode) {
        this.myUserId = this.adminVirtualId;
      } else {
        const token = localStorage.getItem('token');
        if(!token) {
          this.$message.warning('请先登录');
          this.$router.push('/login');
          return;
        }
        if (this.$globalData.userInfo && this.$globalData.userInfo.id) {
          this.myUserId = this.$globalData.userInfo.id;
        } else {
          this.$api.getUserInfo().then(res => {
            if (res.status_code === 1) {
              this.myUserId = res.data.id;
              this.$globalData.userInfo = res.data;
            }
          });
        }
      }
    },

    // 启动轮询
    startRealtimePolling() {
      // 立即执行一次
      this.loadHistory();

      // 每 3000ms (3秒) 自动刷新一次
      this.pollingTimer = setInterval(() => {
        this.loadHistory(true); // 传入 true 表示静默刷新
      }, 3000);
    },

    // 停止轮询
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer);
        this.pollingTimer = null;
      }
    },

    getIdleInfo(){
      this.$api.getIdleItem({ id: this.idleId }).then(res => {
        if(res.status_code === 1 && res.data){
          this.idleInfo = res.data;
        }
      });
    },

    loadHistory(isSilent = false) {
      if(!this.targetUserId) return;

      const apiFunc = this.isAdminMode
          ? this.$api.getAdminChatHistory
          : this.$api.getChatHistory;

      apiFunc({
        targetUserId: this.targetUserId,
        idleId: this.idleId
      }).then(res => {
        if(res.status_code === 1) {
          const newList = res.data;

          // 性能优化：只有消息条数变化时，才更新视图
          if (newList.length !== this.chatHistory.length) {
            this.chatHistory = newList;

            // 更新对方头像昵称信息（仅在首次或对方信息变更时）
            if(newList.length > 0){
              let lastMsg = newList[newList.length-1];
              if(String(lastMsg.userId) === String(this.myUserId)){
                this.targetUser = lastMsg.toU;
              } else {
                this.targetUser = lastMsg.fromU;
              }
            }

            this.$nextTick(() => { this.scrollToBottom(); });
          }

          if(!isSilent) {
            setTimeout(() => { bus.$emit('refreshUnread'); }, 500);
          }
        }
      });
    },

    sendMsg(content) {
      let msgToSend = (typeof content === 'string') ? content : this.inputMsg;
      if(!msgToSend || !msgToSend.trim()) return;

      const sendApiFunc = this.isAdminMode
          ? this.$api.sendAdminMessage
          : this.$api.sendMessage;

      sendApiFunc({
        idleId: this.idleId,
        content: msgToSend,
        toUser: this.targetUserId
      }).then(res => {
        if(res.status_code === 1) {
          this.inputMsg = '';
          this.loadHistory(true);
        } else {
          this.$message.error(res.msg);
        }
      });
    },

    isImage(content) {
      if (!content) return false;
      return content.includes('/image?imageName=') ||
          content.match(/\.(jpeg|jpg|gif|png)$/) != null;
    },

    handleImageSuccess(res, file) {
      if (res.status_code === 1) {
        this.sendMsg(res.data);
      } else {
        this.$message.error('图片上传失败');
      }
    },

    beforeImageUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 5;
      if (!isJPG) this.$message.error('格式错误!');
      if (!isLt2M) this.$message.error('大小超过5MB!');
      return isJPG && isLt2M;
    },

    goBack() {
      if (this.isAdminMode) {
        this.$router.go(-1);
        return;
      }
      this.$router.push({
        path: '/message',
        query: { activeUser: this.targetUserId }
      });
    },

    toDetail() {
      if (this.idleId && this.idleId !== 0) {
        this.$router.push({
          path: '/details',
          query: { id: this.idleId }
        });
      }
    },

    scrollToBottom() {
      const div = this.$refs.chatContent;
      if(div) {
        div.scrollTo({
          top: div.scrollHeight,
          behavior: 'smooth'
        });
      }
    },

    formatTime(timeStr) {
      if(!timeStr) return '';
      return timeStr.replace("T", " ").substring(5, 16);
    },

    shouldShowTime(index) {
      if (index === 0) return true;
      const curr = new Date(this.chatHistory[index].createTime).getTime();
      const prev = new Date(this.chatHistory[index - 1].createTime).getTime();
      return (curr - prev) > 5 * 60 * 1000;
    }
  }
}
</script>

<style scoped>
.chat-container {
  min-height: 85vh;
  padding: 20px 0;
  display: flex;
  justify-content: center;
  background-color: #f5f7fa;
}
.chat-box {
  width: 850px;
  height: 700px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 8px;
}
.chat-header {
  background-color: #fff;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}
.header-left {
  display: flex;
  align-items: center;
  float: left;
}
.idle-tag {
  margin-left: 15px;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}
.clickable-tag {
  cursor: pointer;
  transition: all 0.3s;
}
.clickable-tag:hover {
  background-color: #f0f9eb;
  color: #67c23a;
  border-color: #e1f3d8;
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #ebeef5;
}

.msg-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}
.msg-left {
  justify-content: flex-start;
}
.msg-right {
  justify-content: flex-end;
}
.msg-avatar {
  margin-top: 5px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.msg-left .msg-avatar {
  margin-right: 10px;
}
.msg-right .msg-avatar {
  margin-left: 10px;
}

.msg-bubble {
  max-width: 60%;
  padding: 12px 16px;
  border-radius: 8px;
  position: relative;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.msg-left .msg-bubble {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-top-left-radius: 2px;
}
.msg-right .msg-bubble {
  background: #409EFF;
  color: #fff;
  border: 1px solid #409EFF;
  border-top-right-radius: 2px;
}

.msg-text {
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
  white-space: pre-wrap;
}

.chat-image {
  max-width: 200px;
  border-radius: 4px;
  cursor: pointer;
}

.chat-input-area {
  flex-shrink: 0;
  background: #fff;
  padding: 10px 20px 20px;
}

.chat-tools {
  padding-bottom: 5px;
}
.input-wrapper {
  position: relative;
}
.send-btn {
  position: absolute;
  bottom: 10px;
  right: 10px;
}

.no-msg {
  text-align: center;
  color: #909399;
  margin-top: 100px;
}
.no-msg i {
  font-size: 40px;
  margin-bottom: 10px;
  display: block;
}
.msg-time-tip {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin: 10px 0;
}
</style>