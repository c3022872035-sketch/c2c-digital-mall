<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">评价管理</h2>
      <div class="header-actions">
        <div class="search-container">
          <el-input
              placeholder="搜索ID / 商品名称 / 评价者 / 评价内容"
              v-model="searchValue"
              @keyup.enter.native="getData"
              prefix-icon="el-icon-search"
              clearable
              class="search-input">
            <el-button slot="append" icon="el-icon-search" @click="getData">搜索</el-button>
          </el-input>
        </div>
        <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')" style="margin-left: 10px;">新增评价</el-button>
      </div>
    </div>

    <div class="table-container">
      <el-table
          :data="commentList"
          stripe
          border
          v-loading="loading"
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '15px', height: '55px' }"
          :cell-style="{ fontSize: '16px', padding: '12px 0' ,color: '#303133'}">

        <el-table-column label="ID" prop="id" width="80" align="center"></el-table-column>

        <el-table-column label="商品名称" width="200" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">{{ scope.row.idleItem ? scope.row.idleItem.idleName : '未知' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="评价者" width="180" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper" v-if="scope.row.buyer">
              <el-avatar :size="45" :src="scope.row.buyer.avatar" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.buyer.nickname }}</span>
            </div>
            <span v-else>未知用户</span>
          </template>
        </el-table-column>

        <el-table-column label="评价内容" min-width="200" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">{{ scope.row.content }}</div>
          </template>
        </el-table-column>

        <el-table-column label="评分" width="160" align="center">
          <template slot-scope="scope">
            <el-rate v-model="scope.row.rating" disabled text-color="#ff9900" score-template="{value}"></el-rate>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="评价时间" width="180" align="center">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <div class="action-row">
              <el-button size="small" type="success" icon="el-icon-edit" @click="openDialog('edit', scope.row)" plain>修改</el-button>
              <el-button size="small" type="danger" icon="el-icon-delete" @click="deleteComment(scope.row)" plain>删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="nowPage" :page-size="8" layout="total, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" center>
      <el-form :model="form" label-width="80px">
        <div v-if="dialogType === 'add'">
          <el-form-item label="选择商品">
            <el-select
                v-model="form.idleId"
                filterable
                remote
                clearable
                placeholder="请输入商品名称搜索"
                :remote-method="remoteMethodIdle"
                :loading="selectLoading"
                style="width: 100%">
              <el-option
                  v-for="item in idleOptions"
                  :key="item.id"
                  :label="item.idleName"
                  :value="item.id">
                <span style="float: left">{{ item.idleName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">￥{{ item.idlePrice }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="评价用户">
            <el-select
                v-model="form.buyerId"
                filterable
                remote
                clearable
                placeholder="请输入用户账号或昵称"
                :remote-method="remoteMethodUser"
                :loading="selectLoading"
                style="width: 100%">
              <el-option
                  v-for="item in userOptions"
                  :key="item.id"
                  :label="item.nickname"
                  :value="item.id">
                <span style="float: left">{{ item.nickname }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.accountNumber }}</span>
              </el-option>
            </el-select>
          </el-form-item>
        </div>

        <div v-else>
          <el-form-item label="商品ID">
            <el-input v-model="form.idleId" disabled></el-input>
          </el-form-item>
        </div>

        <el-form-item label="评分">
          <el-rate v-model="form.rating" show-text style="margin-top: 10px;"></el-rate>
        </el-form-item>

        <el-form-item label="评价内容">
          <el-input type="textarea" :rows="4" v-model="form.content" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "CommentList",
  data() {
    return {
      commentList: [],
      loading: false,
      nowPage: 1,
      total: 0,
      searchValue: '',
      dialogVisible: false,
      dialogTitle: '',
      dialogType: 'add',
      form: {
        id: '',
        idleId: '',
        buyerId: '',
        rating: 5,
        content: ''
      },
      selectLoading: false,
      idleOptions: [],
      userOptions: []
    }
  },
  created() {
    this.getData();
  },
  methods: {
    formatTime(time){ return time ? time.replace("T", " ").substring(0, 19) : ''; },
    handleCurrentChange(val) { this.nowPage = val; this.getData(); },
    getData() {
      this.loading = true;
      this.$api.getAdminCommentList({
        page: this.nowPage,
        nums: 8,
        searchValue: this.searchValue
      }).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.commentList = res.data.list;
          this.total = res.data.count;
        }
      });
    },
    deleteComment(row) {
      this.$confirm('确定删除该评价吗?', '警告', { type: 'warning' }).then(() => {
        this.$api.deleteAdminComment({ id: row.id }).then(res => {
          if (res.status_code === 1) {
            this.$message.success('删除成功');
            this.getData();
          }
        });
      });
    },
    // 远程搜索商品
    remoteMethodIdle(query) {
      if (query !== '') {
        this.selectLoading = true;
        this.$api.searchIdleForSelect({ findValue: query, page: 1, nums: 20, status: 1 }).then(res => {
          this.selectLoading = false;
          if(res.status_code === 1) {
            this.idleOptions = res.data.list;
          }
        });
      } else {
        this.idleOptions = [];
      }
    },
    // 远程搜索用户
    remoteMethodUser(query) {
      if (query !== '') {
        this.selectLoading = true;
        this.$api.searchUserForSelect({ searchValue: query, page: 1, nums: 20, mode: 1 }).then(res => {
          this.selectLoading = false;
          if(res.status_code === 1) {
            this.userOptions = res.data.list;
          }
        });
      } else {
        this.userOptions = [];
      }
    },
    openDialog(type, row) {
      this.dialogType = type;
      if (type === 'add') {
        this.dialogTitle = '新增评价';
        this.form = { idleId: '', buyerId: '', rating: 5, content: '' };
        this.idleOptions = [];
        this.userOptions = [];
      } else {
        this.dialogTitle = '修改评价';
        this.form = {
          id: row.id,
          idleId: row.idleId,
          rating: row.rating,
          content: row.content
        };
      }
      this.dialogVisible = true;
    },
    submitForm() {
      if (!this.form.content) {
        this.$message.warning('请输入评价内容');
        return;
      }
      if (this.dialogType === 'add') {
        if (!this.form.idleId || !this.form.buyerId) {
          this.$message.warning('请选择商品和用户');
          return;
        }
        this.$api.addAdminComment(this.form).then(res => {
          if(res.status_code === 1) {
            this.$message.success('添加成功');
            this.dialogVisible = false;
            this.getData();
          } else { this.$message.error(res.msg); }
        });
      } else {
        this.$api.updateAdminComment(this.form).then(res => {
          if(res.status_code === 1) {
            this.$message.success('修改成功');
            this.dialogVisible = false;
            this.getData();
          } else { this.$message.error(res.msg); }
        });
      }
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
.header-actions {
  display: flex;
  align-items: center;
}
.search-container { width: 400px; }
.search-input { width: 100%; }
.search-input >>> .el-input__inner { border-radius: 20px 0 0 20px; border-right: none; }
.search-input >>> .el-input-group__append { border-radius: 0 20px 20px 0; background-color: #409EFF; border-color: #409EFF; color: white; }
.search-input >>> .el-input-group__append .el-button { color: white; border: none; }

.page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
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

.action-row {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.action-row .el-button {
  margin-left: 0 !important;
  margin-bottom: 3px;
  width: 70px;
  padding: 7px 0;
  text-align: center;
}

.action-row .el-button:last-child {
  margin-bottom: 0;
}

@media screen and (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .header-actions { width: 100%; margin-top: 15px; flex-direction: column; }
  .search-container { width: 100%; }
  .header-actions .el-button { width: 100%; margin-left: 0 !important; margin-top: 10px; }
}
</style>