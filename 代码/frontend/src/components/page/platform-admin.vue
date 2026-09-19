<template>
  <div class="admin-container">
    <el-container>
      <el-header height="60px" class="header-container">
        <div class="header">
          <div class="header-left">
            <router-link to="/platform-admin" class="app-name">C2C交易平台管理后台</router-link>
          </div>
          <div class="header-right">
            <span class="admin-info">管理员：{{admin.nickname}}</span>
            <el-button type="primary" size="small" @click="logout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      <el-container class="main-container">
        <el-aside width="220px">
          <el-menu
              :default-active="mode.toString()"
              class="el-menu-vertical-demo"
              @select="handleSelect"
              background-color="#ffffff"
              text-color="#303133"
              active-text-color="#409EFF">

            <el-menu-item index="1">
              <i class="el-icon-goods"></i>
              <span>商品管理</span>
            </el-menu-item>

            <el-menu-item index="2">
              <i class="el-icon-s-goods"></i>
              <span>订单管理</span>
            </el-menu-item>

            <el-menu-item index="3">
              <i class="el-icon-s-custom"></i>
              <span>用户管理</span>
            </el-menu-item>

            <el-menu-item index="5">
              <i class="el-icon-chat-dot-square"></i>
              <span>评价管理</span>
            </el-menu-item>

            <!-- 审核管理 -->
            <el-menu-item index="7">
              <div style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
                <div><i class="el-icon-s-check"></i> 审核管理</div>
                <el-badge :value="pendingAuditCount" :max="99" :hidden="pendingAuditCount===0" class="menu-badge" type="danger" />
              </div>
            </el-menu-item>

            <el-menu-item index="6" style="overflow: visible;">
              <div style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
                <div><i class="el-icon-warning"></i> 投诉管理</div>
                <el-badge :value="pendingReportCount" :max="99" :hidden="pendingReportCount===0" class="menu-badge" type="danger" />
              </div>
            </el-menu-item>

            <el-menu-item index="4" style="overflow: visible;">
              <div style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
                <div><i class="el-icon-data-analysis"></i> 数据统计</div>
                <el-badge value="!" :hidden="!hasNewData" class="menu-badge" type="warning" />
              </div>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <IdleGoods v-if="mode === 1"></IdleGoods>
          <orderList v-if="mode === 2"></orderList>
          <userList v-if="mode === 3"></userList>
          <DataStatistics v-if="mode === 4"></DataStatistics>
          <CommentList v-if="mode === 5"></CommentList>
          <ReportList v-if="mode === 6" @refresh-badge="getPendingCount"></ReportList>
          <AuditList v-if="mode === 7"></AuditList>
        </el-main>
      </el-container>
    </el-container>
    <footer class="footer">
      <app-foot></app-foot>
    </footer>
  </div>
</template>

<script>
import AppFoot from '../common/AppFoot.vue'
import IdleGoods from '../common/IdleGoods.vue'
import orderList from '../common/orderList.vue'
import userList from '../common/userList.vue'
import DataStatistics from '../common/DataStatistics.vue'
import CommentList from '../common/CommentList.vue'
import ReportList from '../common/ReportList.vue'
import AuditList from '../common/AuditList.vue'

export default {
  name: "platform-admin",
  components: {
    AppFoot, IdleGoods, orderList, userList, DataStatistics,
    CommentList, ReportList, AuditList
  },
  data() {
    return {
      mode: 1,
      admin: { nickname: '' },
      pendingReportCount: 0,
      pendingAuditCount: 0,
      hasNewData: true
    }
  },
  created() {
    this.getUserInfo();
    this.getPendingCount();
    this.getAuditCount();

    // 初始化读取 URL 参数
    const pageParam = this.$route.query.page;
    if (pageParam) {
      this.mode = parseInt(pageParam);
      if(this.mode === 4) this.hasNewData = false;
    }
  },
  watch: {
    '$route.query.page'(val) {
      if (val) {
        this.mode = parseInt(val);
      }
    }
  },
  methods: {
    getUserInfo() {
      this.$api.getUserInfo().then(res => {
        if (res.status_code === 1 && res.data.adminName) {
          this.admin.nickname = res.data.adminName;
        }
      });
    },
    getPendingCount() {
      this.$api.getPendingReportCount().then(res => {
        if (res.status_code === 1) {
          this.pendingReportCount = res.data;
        }
      });
    },
    getAuditCount() {
      // 调用待审数量接口 (status=3, searchValue=null)
      this.$api.getAuditList({ status: 3, page: 1, nums: 1 }).then(res => {
        if (res.status_code === 1) {
          this.pendingAuditCount = res.data.count;
        }
      });
    },
    logout() {
      this.$api.loginOut({}).then(res => {
        if (res.status_code === 1) {
          this.$sta.isLogin = false;
          this.$router.push({path: '/login-admin'});
        }
      });
    },
    handleSelect(val) {
      const newVal = parseInt(val);
      if (this.mode !== newVal) {
        this.mode = newVal;

        if (newVal === 4) this.hasNewData = false;
        if (newVal === 6) this.getPendingCount();
        if (newVal === 7) this.getAuditCount(); // 刷新审核计数

        this.$router.push({ query: { ...this.$route.query, page: val } });
      }
    }
  },
}
</script>

<style scoped>
/* 样式复用之前修改过的，这里保持一致 */
.admin-container {
  position: relative; width: 100%; height: 100vh; overflow: hidden; background-color: #f6f6f6;
}
.header-container {
  position: absolute; top: 0; left: 0; width: 100%; height: 60px; background-color: #fff; z-index: 1000;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); padding: 0;
}
.header {
  height: 100%; display: flex; justify-content: space-between; align-items: center; padding: 0 20px;
}
.header-left, .header-right { display: flex; align-items: center; }
.app-name { color: #409EFF; font-size: 18px; font-weight: 800; text-decoration: none; }
.admin-info { margin-right: 20px; font-size: 14px; color: #606266; }

.main-container {
  position: absolute; top: 60px; bottom: 60px; left: 0; right: 0; width: 100%; overflow: hidden;
}
.el-aside {
  height: 100%; background-color: #fff; box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05); overflow-y: auto; z-index: 10;
}
.el-aside::-webkit-scrollbar { width: 0; }

.el-main {
  position: relative; height: 100%; padding: 0; background-color: #f6f6f6; overflow-y: auto;
}
.footer {
  position: absolute; bottom: 0; left: 0; width: 100%; height: 60px; background-color: #fff; padding: 15px 0;
  text-align: center; border-top: 1px solid #e5e5e5; z-index: 1000; box-sizing: border-box;
}
.menu-badge { margin-right: 10px; margin-top: -2px; }
.menu-badge >>> .el-badge__content { border: none; }
</style>