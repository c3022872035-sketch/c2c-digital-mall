<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="result-container">
        <el-card class="result-card">
          <div class="success-icon">
            <i class="el-icon-circle-check"></i>
          </div>
          <h2 class="status-text">支付成功</h2>
          <p class="order-hint">您的订单已成功支付，系统已自动发放资源授权。</p>

          <div class="detail-box">
            <p>订单编号：{{ orderNo }}</p>
            <p>支付金额：<span class="price">￥{{ price }}</span></p>
          </div>

          <div class="timer-text">
            将在 <span class="seconds">{{ countdown }}</span> 秒后自动跳转至订单详情...
          </div>

          <div class="btn-group">
            <el-button type="primary" @click="goToOrder">立即跳转</el-button>
            <el-button @click="goToHome">回首页</el-button>
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

export default {
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      countdown: 3,
      timer: null,
      orderId: this.$route.query.id,
      orderNo: this.$route.query.no,
      price: this.$route.query.price
    };
  },
  mounted() {
    this.startTimer();
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer);
  },
  methods: {
    startTimer() {
      this.timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          this.goToOrder();
        }
      }, 1000);
    },
    goToOrder() {
      this.$router.push({ path: '/order', query: { id: this.orderId } });
    },
    goToHome() {
      this.$router.push('/index');
    }
  }
};
</script>

<style scoped>
.result-container { display: flex; justify-content: center; padding: 40px 0; background: #f5f7fa; min-height: 70vh; }
.result-card { width: 500px; text-align: center; padding: 30px; border-radius: 12px; }
.success-icon { font-size: 80px; color: #67C23A; margin-bottom: 20px; }
.status-text { font-size: 24px; color: #303133; margin-bottom: 10px; }
.order-hint { color: #909399; font-size: 14px; margin-bottom: 30px; }
.detail-box { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: left; margin-bottom: 30px; }
.detail-box p { margin: 8px 0; color: #606266; font-size: 15px; }
.price { color: #f56c6c; font-weight: bold; }
.timer-text { font-size: 14px; color: #999; margin-bottom: 20px; }
.seconds { color: #409EFF; font-weight: bold; margin: 0 5px; }
.btn-group { display: flex; justify-content: center; gap: 15px; }
</style>