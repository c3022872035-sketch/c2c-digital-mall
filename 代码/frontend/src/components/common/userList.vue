<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <div class="header-actions">
        <div class="search-container">
          <el-input
              :placeholder="mode === 3 ? '搜索管理账号 / 管理名称' : '搜索用户账号 / 用户昵称'"
              v-model="searchValue"
              @keyup.enter.native="searchIdle"
              prefix-icon="el-icon-search"
              clearable
              class="search-input">
            <el-button slot="append" icon="el-icon-search" @click="searchIdle">搜索</el-button>
          </el-input>
        </div>

        <el-button
            v-if="mode !== 3"
            type="primary"
            icon="el-icon-plus"
            @click="openUserDialog('add')"
            style="margin-left: 10px;">
          新增用户
        </el-button>

        <el-button
            v-if="mode === 3"
            type="primary"
            icon="el-icon-plus"
            @click="openAdminDialog('add')"
            style="margin-left: 10px;">
          新增管理员
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick" type="card" class="custom-tabs">
      <el-tab-pane label="正常用户" name="1">
        <div class="table-container">
          <el-table
              :data="userData"
              stripe
              border
              style="width: 100%"
              :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '15px', height: '55px' }"
              :cell-style="{ fontSize: '16px', padding: '12px 0' ,color: '#303133'}"
              v-loading="tableLoading">

            <el-table-column prop="id" label="用户ID" width="80" align="center"></el-table-column>
            <el-table-column prop="accountNumber" label="用户账号" show-overflow-tooltip width="150" align="center"></el-table-column>

            <el-table-column label="用户昵称" width="180" align="center">
              <template slot-scope="scope">
                <div class="user-info-wrapper">
                  <el-avatar :size="45" :src="scope.row.avatar" class="user-avatar">
                    <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
                  </el-avatar>
                  <span class="user-nickname">{{ scope.row.nickname }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="signInTime" label="注册时间" show-overflow-tooltip width="200" align="center">
              <template slot-scope="scope">
                {{ scope.row.signInTime ? scope.row.signInTime.replace('T', ' ').substring(0, 19) : '' }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="280" align="center">
              <template slot-scope="scope">
                <div v-if="scope.row.id !== 88" class="action-row">
                  <el-button size="mini" type="primary" icon="el-icon-chat-dot-round" @click="toChat(scope.row)" plain>私信</el-button>
                  <el-button size="mini" type="success" icon="el-icon-edit" @click="openUserDialog('edit', scope.row)" plain>修改</el-button>
                  <el-button size="mini" type="warning" icon="el-icon-warning-outline" @click="openWarningDialog(scope.row)" plain>警告</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-lock" @click="confirmSealUser(scope.$index)" plain>封号</el-button>
                </div>
                <div v-else>
                  <el-tag type="info" size="mini">系统账号</el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="违规用户" name="2">
        <div class="table-container">
          <el-table
              :data="badUserData"
              stripe
              border
              style="width: 100%"
              :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '15px', height: '55px' }"
              :cell-style="{ fontSize: '16px', padding: '12px 0' ,color: '#303133'}"
              v-loading="tableLoading">

            <el-table-column prop="id" label="用户ID" width="80" align="center"></el-table-column>
            <el-table-column prop="accountNumber" label="用户账号" show-overflow-tooltip width="150" align="center"></el-table-column>

            <el-table-column label="用户昵称" width="180" align="center">
              <template slot-scope="scope">
                <div class="user-info-wrapper">
                  <el-avatar :size="45" :src="scope.row.avatar" class="user-avatar">
                    <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
                  </el-avatar>
                  <span class="user-nickname">{{ scope.row.nickname }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="signInTime" label="注册时间" show-overflow-tooltip width="180" align="center">
              <template slot-scope="scope">
                {{ scope.row.signInTime ? scope.row.signInTime.replace('T', ' ').substring(0, 19) : '' }}
              </template>
            </el-table-column>

            <el-table-column prop="banTime" label="封禁时间" show-overflow-tooltip width="180" align="center">
              <template slot-scope="scope">
                <span style="color: #F56C6C;">{{ scope.row.banTime ? scope.row.banTime.replace('T', ' ').substring(0, 19) : '-' }}</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="260" align="center">
              <template slot-scope="scope">
                <div v-if="scope.row.id !== 88" class="action-row">
                  <el-button size="mini" type="primary" icon="el-icon-chat-dot-round" @click="toChat(scope.row)" plain>私信</el-button>
                  <el-button size="mini" type="success" icon="el-icon-unlock" @click="confirmUnsealUser(scope.$index)" plain>解封</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-delete" @click="confirmDeleteUser(scope.row)" plain>永久封号</el-button>
                </div>
                <div v-else>
                  <el-tag type="info" size="mini">系统账号</el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="管理员" name="3">
        <div class="table-container">
          <el-table
              :data="userManage"
              stripe
              border
              style="width: 100%"
              :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '15px', height: '55px' }"
              :cell-style="{ fontSize: '16px', padding: '12px 0' ,color: '#303133'}"
              v-loading="tableLoading">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="accountNumber" label="管理账号" show-overflow-tooltip align="center"></el-table-column>
            <el-table-column prop="adminName" label="管理名称" align="center">
              <template slot-scope="scope">
                <el-tag type="primary" effect="plain" size="medium">{{ scope.row.adminName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template slot-scope="scope">
                <div class="action-row">
                  <el-button size="mini" type="success" icon="el-icon-edit" @click="openAdminDialog('edit', scope.row)" plain>修改</el-button>
                  <el-button size="mini" type="danger" icon="el-icon-delete" @click="confirmDeleteAdmin(scope.row)" plain>删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <div class="pagination-container">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="nowPage"
          :page-size="8"
          background
          layout="total, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog :title="adminDialogTitle" :visible.sync="adminDialogVisible" width="500px" center>
      <el-form :model="adminForm" label-width="80px">
        <el-form-item label="管理名称">
          <el-input v-model="adminForm.adminName" maxlength="8" placeholder="请输入管理名称" prefix-icon="el-icon-user" clearable></el-input>
        </el-form-item>
        <el-form-item label="管理账号">
          <el-input v-model="adminForm.accountNumber" minlength="8" maxlength="10" placeholder="请输入管理账号" prefix-icon="el-icon-s-custom" clearable :disabled="adminDialogTitle==='修改管理员'"></el-input>
        </el-form-item>
        <el-form-item label="登录密码" v-if="adminDialogTitle==='新增管理员'">
          <el-input v-model="adminForm.adminPassword" minlength="8" placeholder="请输入管理员密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item label="重置密码" v-else>
          <el-input v-model="adminForm.adminPassword" minlength="8" placeholder="留空则不修改" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="adminDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdmin">确 定</el-button>
            </span>
    </el-dialog>

    <el-dialog title="发送违规警告" :visible.sync="warningVisible" width="500px" center>
      <el-form label-position="top">
        <el-form-item label="违规商品名称">
          <el-select
              v-model="warningForm.productName"
              filterable
              remote
              reserve-keyword
              placeholder="请输入关键词搜索商品"
              :remote-method="remoteMethodIdle"
              :loading="selectLoading"
              style="width: 100%">
            <el-option
                v-for="item in idleOptions"
                :key="item.id"
                :label="item.idleName"
                :value="item.idleName">
              <!-- 下拉选项显示更多信息 -->
              <span style="float: left">{{ item.idleName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px; margin-left:10px">ID:{{ item.id }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="违规行为/理由">
          <el-select v-model="warningForm.reason" placeholder="请选择违规理由" style="width: 100%;">
            <el-option label="货不对板" value="货不对板"></el-option>
            <el-option label="发布违禁品" value="发布违禁品"></el-option>
            <el-option label="侵权行为" value="侵权行为"></el-option>
            <el-option label="诈骗/欺诈" value="诈骗/欺诈"></el-option>
            <el-option label="引导站外交易" value="引导站外交易"></el-option>
            <el-option label="辱骂/骚扰他人" value="辱骂/骚扰他人"></el-option>
            <el-option label="其他违规" value="其他违规"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="warningForm.reason === '其他违规'" label="具体违规内容">
          <el-input type="textarea" :rows="2" v-model="warningForm.customDetail" placeholder="请输入具体的违规行为描述"></el-input>
        </el-form-item>
        <div style="background: #f4f4f5; padding: 10px; border-radius: 4px; font-size: 13px; color: #606266; line-height: 1.5;">
          <strong>预览发送内容：</strong><br/>
          尊敬的用户，您好！经查实，您的商品“{{warningForm.productName}}”存在“<span style="color: #F56C6C; font-weight: bold;">{{ getFinalReason() }}</span>”行为，特此给予警告！请尽快整改，否则将面临封号处理。
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="warningVisible = false">取 消</el-button>
                <el-button type="warning" @click="sendWarning">确认发送</el-button>
            </span>
    </el-dialog>

    <el-dialog :title="userDialogTitle" :visible.sync="userDialogVisible" width="500px" center>
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户账号">
          <el-input v-model="userForm.accountNumber" maxlength="11" placeholder="请输入账号" prefix-icon="el-icon-s-custom" :disabled="userDialogTitle==='修改用户'"></el-input>
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" prefix-icon="el-icon-user" ></el-input>
        </el-form-item>
        <el-form-item label="登录密码" v-if="userDialogTitle==='新增用户'">
          <el-input v-model="userForm.userPassword" placeholder="设置初始密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item label="重置密码" v-else>
          <el-input v-model="userForm.userPassword" placeholder="不修改请留空" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>

        <el-form-item label="用户头像">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/file/"
              :show-file-list="false"
              :on-success="handleUserAvatarSuccess"
              accept="image/*">
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="userDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitUser">确 定</el-button>
            </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "userList",
  created() {
    this.getUserData();
  },
  data(){
    return {
      mode: 1,
      activeTab: "1",
      nowPage: 1,
      total: 63,

      // 管理员相关
      adminDialogVisible: false,
      adminDialogTitle: '',
      adminForm: { id: '', accountNumber: '', adminPassword: '', adminName: '' },

      // 表格数据
      userData: [],
      badUserData: [],
      userManage: [],
      searchValue: '',
      tableLoading: false,
      selectLoading: false,
      idleOptions: [],

      // 警告弹窗数据
      warningVisible: false,
      warningForm: { userId: '', productName: '', reason: '', customDetail: '' },

      // 新增/修改用户 弹窗数据
      userDialogVisible: false,
      userDialogTitle: '',
      userForm: { id: '', accountNumber: '', nickname: '', userPassword: '', avatar: '' }
    }
  },
  methods: {
    handleTabClick(tab) {
      this.mode = parseInt(tab.name);
      this.nowPage = 1;
      this.searchValue = ''; // 切换Tab清空搜索
      if (this.mode === 1) this.getUserData();
      else if (this.mode === 2) this.getBadUserData();
      else this.getUserManage();
    },
    handleCurrentChange(val) {
      this.nowPage = val;
      if(this.searchValue) {
        this.searchIdle(); // 如果有搜索词，翻页走搜索接口
      } else {
        if(this.mode === 1) this.getUserData();
        else if(this.mode === 2) this.getBadUserData();
        else this.getUserManage();
      }
    },

    // --- 用户头像上传回调 ---
    handleUserAvatarSuccess(res, file) {
      if(res.status_code === 1) {
        this.userForm.avatar = res.data;
      } else {
        this.$message.error('图片上传失败');
      }
    },
    // 远程搜索商品方法
    remoteMethodIdle(query) {
      if (query !== '') {
        this.selectLoading = true;
        // 复用后端的 queryIdle 接口 (status=1 上架商品，或者不传status查所有)
        // 这里假设后端支持模糊搜索
        this.$api.queryIdle({
          findValue: query,
          page: 1,
          nums: 20,
          status: 1 // 也可以不传status查所有，看你需求
        }).then(res => {
          this.selectLoading = false;
          if(res.status_code === 1) {
            this.idleOptions = res.data.list;
          }
        });
      } else {
        this.idleOptions = [];
      }
    },
    // --- 管理员操作逻辑 ---
    openAdminDialog(type, row) {
      if(type === 'add') {
        this.adminDialogTitle = '新增管理员';
        this.adminForm = { accountNumber: '', adminPassword: '', adminName: '' };
      } else {
        this.adminDialogTitle = '修改管理员';
        this.adminForm = {
          id: row.id,
          accountNumber: row.accountNumber,
          adminName: row.adminName,
          adminPassword: '' // 留空不改密码
        };
      }
      this.adminDialogVisible = true;
    },
    submitAdmin() {
      if(!this.adminForm.accountNumber || !this.adminForm.adminName) {
        this.$message.warning('请填写完整信息');
        return;
      }
      if(this.adminDialogTitle === '新增管理员' && !this.adminForm.adminPassword) {
        this.$message.warning('请设置初始密码');
        return;
      }

      if(this.adminDialogTitle === '新增管理员') {
        this.$api.regAdministrator(this.adminForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('添加成功');
            this.adminDialogVisible = false;
            this.getUserManage();
          } else { this.$message.error(res.msg); }
        });
      } else {
        this.$api.updateAdministrator(this.adminForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('修改成功');
            this.adminDialogVisible = false;
            this.getUserManage();
          } else { this.$message.error(res.msg); }
        });
      }
    },
    confirmDeleteAdmin(row) {
      this.$confirm('确定删除该管理员吗？', '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
          .then(() => {
            this.$api.deleteAdministrator({ id: row.id }).then(res => {
              if (res.status_code === 1) {
                this.$message.success('删除成功');
                this.getUserManage();
              } else { this.$message.error(res.msg); }
            });
          }).catch(() => {});
    },

    confirmSealUser(index) {
      this.$confirm('确定要封禁该用户吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
          .then(() => { this.sealUser(index); }).catch(() => {});
    },
    confirmUnsealUser(index) {
      this.$confirm('确定要解除该用户的封禁状态吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
          .then(() => { this.unsealUser(index); }).catch(() => {});
    },
    confirmDeleteUser(row) {
      this.$confirm('此操作将永久删除该用户，无法恢复，确认继续？', '严重警告', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error' })
          .then(() => {
            this.$api.deleteAdminUser({ id: row.id }).then(res => {
              if (res.status_code === 1) {
                this.$message.success('用户已永久删除');
                this.getBadUserData();
              } else { this.$message.error(res.msg); }
            });
          }).catch(() => {});
    },
    toChat(row) {
      this.$router.push({
        path: '/chat',
        query: {
          targetUserId: row.id,
          targetName: row.nickname,
          idleId: 0,
          isAdmin: true
        }
      });
    },
    // 打开警告对话框时，清空选项
    openWarningDialog(row) {
      this.warningForm = {
        userId: row.id,
        productName: '',
        reason: '',
        customDetail: ''
      };
      this.warningVisible = true;
    },
    getFinalReason() {
      if (this.warningForm.reason === '其他违规') return this.warningForm.customDetail || '其他违规';
      return this.warningForm.reason;
    },
    sendWarning() {
      if(!this.warningForm.productName || !this.warningForm.reason){
        this.$message.warning('请填写完整信息');
        return;
      }
      if(this.warningForm.reason === '其他违规' && !this.warningForm.customDetail.trim()){
        this.$message.warning('请填写具体的违规内容');
        return;
      }
      const finalReasonStr = this.getFinalReason();
      const content = `尊敬的用户，您好！经查实，您的商品“${this.warningForm.productName}”存在“${finalReasonStr}”行为，特此给予警告！请尽快整改，否则将面临封号处理。`;

      this.$api.sendAdminMessage({
        idleId: 0, content: content, toUser: this.warningForm.userId
      }).then(res => {
        if(res.status_code === 1) {
          this.$message.success('警告已发送');
          this.warningVisible = false;
        } else { this.$message.error(res.msg); }
      });
    },
    // 打开新增/修改用户弹窗
    openUserDialog(type, row) {
      if(type === 'add') {
        this.userDialogTitle = '新增用户';
        this.userForm = { accountNumber: '', nickname: '', userPassword: '', avatar: '' };
      } else {
        this.userDialogTitle = '修改用户';
        this.userForm = {
          id: row.id,
          accountNumber: row.accountNumber,
          nickname: row.nickname,
          userPassword: '',
          avatar: row.avatar
        };
      }
      this.userDialogVisible = true;
    },
    // 提交用户表单
    submitUser() {
      if(!this.userForm.accountNumber || !this.userForm.nickname) {
        this.$message.warning('请填写完整信息');
        return;
      }
      if(this.userDialogTitle === '新增用户' && !this.userForm.userPassword) {
        this.$message.warning('请设置初始密码');
        return;
      }

      if(this.userDialogTitle === '新增用户') {
        this.$api.addAdminUser(this.userForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('新增成功');
            this.userDialogVisible = false;
            this.getUserData();
          } else { this.$message.error(res.msg); }
        });
      } else {
        this.$api.updateAdminUser(this.userForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('修改成功');
            this.userDialogVisible = false;
            this.getUserData();
          } else { this.$message.error(res.msg); }
        });
      }
    },
    getUserData(){
      this.tableLoading = true;
      this.$api.getUserData({ page: this.nowPage, nums:8, status:0 }).then(res => {
        this.tableLoading = false;
        if(res.status_code===1){
          this.userData = res.data.list;
          this.total = res.data.count;
        }else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    getBadUserData(){
      this.tableLoading = true;
      this.$api.getUserData({ page: this.nowPage, nums:8, status:1 }).then(res => {
        this.tableLoading = false;
        if(res.status_code===1){
          this.badUserData = res.data.list;
          this.total = res.data.count;
        }else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) });
    },
    getUserManage(){
      this.tableLoading = true;
      this.$api.getUserManage({ page: this.nowPage, nums:8 }).then(res => {
        this.tableLoading = false;
        if(res.status_code===1){
          this.userManage = res.data.list;
          this.total = res.data.count;
        }else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    sealUser(i){
      this.tableLoading = true;
      this.$api.updateUserStatus({ id: this.userData[i].id, status:1 }).then(res => {
        this.tableLoading = false;
        if(res.status_code===1){
          this.$message.success('用户已成功封禁');
          this.getUserData();
        }else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    unsealUser(i){
      this.tableLoading = true;
      this.$api.updateUserStatus({ id: this.badUserData[i].id, status:0 }).then(res => {
        this.tableLoading = false;
        if(res.status_code===1){
          this.$message.success('用户已成功解封');
          this.getBadUserData();
        }else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    searchIdle() {
      this.tableLoading = true;
      this.$api.queryUser({
        searchValue: this.searchValue, mode: this.mode, page: this.nowPage, nums: 8
      }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1) {
          if(this.mode === 1){
            this.userData = res.data.list;
            this.total = res.data.count;
          }else if(this.mode === 2){
            this.badUserData = res.data.list;
            this.total = res.data.count;
          }else {
            this.userManage = res.data.list;
            this.total = res.data.count;
          }
        } else{ this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
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

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.search-container {width: 400px;}

.search-input { width: 100%; }
.search-input >>> .el-input__inner { border-radius: 20px 0 0 20px; border-right: none; }
.search-input >>> .el-input-group__append { border-radius: 0 20px 20px 0; background-color: #409EFF; border-color: #409EFF; color: white; }
.search-input >>> .el-input-group__append .el-button { color: white; border: none; }

.custom-tabs >>> .el-tabs__header { margin-bottom: 20px; }
.custom-tabs >>> .el-tabs__item { font-size: 15px; padding: 0 25px; height: 40px; line-height: 40px; }
.custom-tabs >>> .el-tabs__item.is-active { color: #409EFF; font-weight: bold; }

.table-container { margin-bottom: 20px; flex: 1; }
.el-table { border-radius: 6px; overflow: hidden; }

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
  font-size: 15px;
}

.pagination-container { margin-top: 20px; display: flex; justify-content: center; }

.admin-dialog >>> .el-dialog__header { background-color: #f5f7fa; padding: 15px 20px; border-bottom: 1px solid #ebeef5; border-radius: 10px 10px 0 0; }
.admin-dialog >>> .el-dialog__title { font-size: 18px; font-weight: 600; color: #303133; }
.admin-dialog >>> .el-dialog__body { padding: 20px 25px; }
.admin-form .el-form-item { margin-bottom: 20px; }
.admin-form .el-input { width: 100%; }

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}
.avatar-uploader:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.action-row {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}
.action-row .el-button {
  margin: 0 3px !important;
  padding: 7px 10px;
}

@media screen and (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .header-actions { width: 100%; margin-top: 15px; flex-direction: column; }
  .search-container { width: 100%; }
  .el-button { margin-top: 10px; width: 100%; margin-left: 0 !important; }
  .main-border { padding: 15px; margin: 10px; }
}
</style>