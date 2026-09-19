<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
      <div class="search-container">
        <el-input
            placeholder="搜索订单号 / 商品名称"
            v-model="searchValue"
            @keyup.enter.native="searchIdle"
            prefix-icon="el-icon-search"
            clearable
            class="search-input">
          <el-button slot="append" icon="el-icon-search" @click="searchIdle">搜索</el-button>
        </el-input>
      </div>
    </div>

    <div class="table-container">
      <el-table
          :data="Order"
          stripe
          border
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '16px', height: '55px' }"
          :cell-style="{ fontSize: '16px', padding: '12px 0'   ,color: '#303133'}"
          v-loading="tableLoading">

        <el-table-column prop="orderNumber" label="订单号" width="105" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">{{ scope.row.orderNumber }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="idleItem.idleName" label="商品名称" show-overflow-tooltip min-width="165" align="center">
          <template slot-scope="scope">
            <div class="wrap-text">{{ scope.row.idleItem.idleName }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="orderPrice" label="金额" width="100" align="center">
          <template slot-scope="scope">
            <span class="price-tag">¥{{ scope.row.orderPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column label="买家" width="170" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper">
              <el-avatar :size="45" :src="scope.row.user ? scope.row.user.avatar : ''" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.user ? scope.row.user.nickname : '未知' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="卖家" width="170" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper">
              <el-avatar :size="45" :src="scope.row.idleItem && scope.row.idleItem.user ? scope.row.idleItem.user.avatar : ''" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.idleItem && scope.row.idleItem.user ? scope.row.idleItem.user.nickname : '未知' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="订单状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="getOrderStatusType(scope.row.orderStatus)" size="small" effect="plain">
              {{ orderStatus[scope.row.orderStatus] }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="支付状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.paymentStatus ? 'success' : 'warning'" size="small">
              {{ paymentStatus[scope.row.paymentStatus] }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="220" align="center">
          <template slot-scope="scope">
            <div style="font-size: 16px; line-height: 1.5; text-align: left; padding-left: 10px;">
              <div style="color: #606266;">创建: {{ formatTime(scope.row.createTime) }}</div>
              <div v-if="scope.row.paymentTime" style="color: #67C23A;">支付: {{ formatTime(scope.row.paymentTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="90" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-btn-container">
              <el-button
                  size="mini"
                  type="warning"
                  icon="el-icon-refresh-left"
                  @click="confirmRefund(scope.row)"
                  plain>退款</el-button>

              <el-button
                  size="mini"
                  type="danger"
                  icon="el-icon-delete"
                  @click="confirmDeleteOrder(scope.$index, scope.row)"
                  plain>删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

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
  </div>
</template>

<script>
export default {
  name: "orderList",
  created() {
    this.getOrder();
  },
  methods:{
    formatTime(time){
      return time ? time.replace("T", " ").substring(0, 19) : '';
    },
    confirmRefund(row) {
      this.$confirm('确定要为订单 ' + row.orderNumber + ' 执行退款吗？', '退款确认', {
        confirmButtonText: '确定退款',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.tableLoading = true;
        this.$api.adminRefundOrder({ id: row.id }).then(res => {
          this.tableLoading = false;
          if (res.status_code === 1) {
            this.$message.success('已成功执行退款并重置订单状态');
            this.getOrder(); // 刷新列表数据
          } else {
            this.$message.error(res.msg);
          }
        }).catch(() => {
          this.tableLoading = false;
        });
      }).catch(() => {
        // 取消操作
      });
    },
    getOrderStatusType(status) {
      const statusMap = {
        0: 'warning',
        1: 'primary',
        2: 'info',
        3: 'success',
        4: 'danger'
      };
      return statusMap[status] || 'info';
    },
    confirmDeleteOrder(index, row) {
      this.$confirm('确定要删除该订单吗？此操作将不可恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteOrder(index);
      }).catch(() => {});
    },
    getOrder(){
      this.tableLoading = true;
      // 复用 queryOrder 接口，searchValue 为空查所有
      this.$api.queryOrder({
        page: this.nowPage,
        nums: 8,
        searchValue: this.searchValue
      }).then(res => {
        this.tableLoading = false;
        if(res.status_code==1){
          this.Order = res.data.list;
          this.total = res.data.count;
        }else {
          this.$message.error(res.msg)
        }
      }).catch(e => {
        this.tableLoading = false;
        console.log(e)
      })
    },
    deleteOrder(index){
      this.tableLoading = true;
      this.$api.deleteOrder({
        id:this.Order[index].id
      }).then(res=>{
        this.tableLoading = false;
        if(res.status_code==1){
          this.$message.success('订单已成功删除');
          this.getOrder();
        }else {
          this.$message.error(res.msg)
        }
      }).catch(e => {
        this.tableLoading = false;
        console.log(e)
      })
    },
    handleCurrentChange(val) {
      this.nowPage = val;
      this.getOrder();
    },
    searchIdle(){
      this.nowPage = 1;
      this.getOrder();
    }
  },
  data(){
    return {
      mode:1,
      nowPage: 1,
      total: 0,
      paymentStatus:['未支付','已支付'],
      orderStatus: ['待付款', '待交付', '已交付', '已完成', '已取消'],
      Order: [],
      searchValue: '',
      tableLoading: false
    }
  },
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

.table-container { margin-bottom: 20px; flex: 1; }
.el-table { border-radius: 6px; overflow: hidden; }

.price-tag { color: #ff6b6b; font-weight: bold; }

.wrap-text {
  white-space: normal;
  word-break: break-all;
  line-height: 1.4;
}

.user-info-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}
.user-avatar {
  border: 1px solid #eee;
  margin-right: 8px;
  flex-shrink: 0;
}
.user-nickname {
  font-weight: 400;
  color: #303133;
  font-size: 16px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.action-btn-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.action-btn-container .el-button--mini {
  width: 80px;
  margin-left: 0 !important;
  padding: 7px 0;
}

@media screen and (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .search-container { width: 100%; margin-top: 15px; }
  .main-border { padding: 15px; margin: 10px; }
}
</style>