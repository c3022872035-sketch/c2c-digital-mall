<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">投诉管理</h2>
      <div class="search-container">
        <el-input
            placeholder="搜索ID/ 涉及商品 / 投诉人 / 商家 / 原因"
            v-model="searchValue"
            @keyup.enter.native="getData"
            prefix-icon="el-icon-search"
            clearable
            class="search-input">
          <el-button slot="append" icon="el-icon-search" @click="getData">搜索</el-button>
        </el-input>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick" type="card" class="custom-tabs">
      <el-tab-pane label="待处理" name="0"></el-tab-pane>
      <el-tab-pane label="已处理" name="1"></el-tab-pane>
    </el-tabs>

    <div class="table-container">
      <el-table
          :data="reportList"
          stripe
          border
          v-loading="loading"
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '16px', height: '55px' }"
          :cell-style="{ fontSize: '16px', padding: '12px 0', color: '#303133' }">

        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>

        <el-table-column label="涉及商品" min-width="160" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">
              {{ scope.row.idleItem ? scope.row.idleItem.idleName : '未知商品' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="投诉人" width="170" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper" v-if="scope.row.user">
              <el-avatar :size="45" :src="scope.row.user.avatar" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.user.nickname }}</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="被投诉商家" width="170" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper" v-if="scope.row.reportedUser">
              <el-avatar :size="45" :src="scope.row.reportedUser.avatar" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.reportedUser.nickname }}</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="reason" label="投诉原因" width="130" align="center">
          <template slot-scope="scope">
            <el-tag type="danger" effect="plain" size="medium">{{ scope.row.reason }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="详细说明" min-width="200" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">{{ scope.row.content }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="投诉时间" width="170" align="center">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="115" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 0" size="mini" type="primary" @click="handleReport(scope.row)" plain>标记已处理</el-button>
            <span v-else style="color: #67C23A; font-weight: bold;"><i class="el-icon-check"></i> 已完成</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="nowPage" :page-size="8" layout="total, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: "ReportList",
  data() {
    return {
      activeTab: '0',
      reportList: [],
      loading: false,
      nowPage: 1,
      total: 0,
      searchValue: ''
    }
  },
  created() {
    this.getData();
  },
  methods: {
    formatTime(time){
      return time ? time.replace("T", " ").substring(0, 19) : '';
    },
    handleTabClick() {
      this.nowPage = 1;
      this.getData();
    },
    handleCurrentChange(val) {
      this.nowPage = val;
      this.getData();
    },
    getData() {
      this.loading = true;
      this.$api.getReportList({
        page: this.nowPage,
        nums: 8,
        status: this.activeTab,
        searchValue: this.searchValue
      }).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.reportList = res.data.list;
          this.total = res.data.count;
        }
      });
    },
    handleReport(row) {
      this.$confirm('确认已核实并处理该投诉吗?', '提示', { type: 'warning' }).then(() => {
        this.$api.handleReport({ id: row.id }).then(res => {
          if (res.status_code === 1) {
            this.$message.success('操作成功');
            this.getData();
            this.$emit('refresh-badge');
          }
        });
      });
    }
  }
}
</script>

<style scoped>
.main-border {
  background-color: #fff;
  margin: 20px;
  padding: 25px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  height: auto;
  min-height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}
.page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }

.search-container { width: 400px; }
.search-input { width: 100%; }
.search-input >>> .el-input__inner { border-radius: 20px 0 0 20px; border-right: none; }
.search-input >>> .el-input-group__append { border-radius: 0 20px 20px 0; background-color: #409EFF; border-color: #409EFF; color: white; }
.search-input >>> .el-input-group__append .el-button { color: white; border: none; }

.custom-tabs >>> .el-tabs__header { margin-bottom: 20px; }
.custom-tabs >>> .el-tabs__item { font-size: 15px; padding: 0 25px; height: 40px; line-height: 40px; }
.custom-tabs >>> .el-tabs__item.is-active { color: #409EFF; font-weight: bold; }

.table-container { margin-bottom: 20px; flex: 1; }
.el-table { border-radius: 6px; overflow: hidden; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }

.user-info-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}
.user-avatar {
  border: 1px solid #eee;
  margin-right: 10px;
  flex-shrink: 0;
}
.user-nickname {
  font-weight: 400;
  color: #303133;
  font-size: 16px;
}
.wrap-text {
  white-space: normal;
  word-break: break-all;
  line-height: 1.5;
  text-align: center;
  padding: 0 10px;
}

@media screen and (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .search-container { width: 100%; margin-top: 15px; }
}
</style>