<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="order-page-container">
        <!-- 1. 顶部状态栏 -->
        <div class="order-header">
          <div class="order-status">
            <el-tag :type="getStatusType(orderInfo.orderStatus)" effect="dark" size="medium">
              {{ getOrderStatusTextInDetail() }}
            </el-tag>
            <div class="order-id">订单编号: {{ orderInfo.orderNumber }}</div>
          </div>
        </div>

        <!-- 2. 商品信息卡片 -->
        <el-card class="product-card" shadow="hover">
          <div class="idle-info-container" @click="toDetails(orderInfo.idleItem.id)">
            <el-image class="product-image" :src="orderInfo.idleItem.imgUrl" fit="cover">
              <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline" title="图片加载失败"></i>
              </div>
            </el-image>
            <div class="product-info">
              <div class="idle-info-title">
                <i class="el-icon-shopping-bag-1"></i>
                {{orderInfo.userId == userId ? '买入资源' : '售出资源'}}：{{orderInfo.idleItem.idleName}}
              </div>
              <div class="idle-info-price">￥{{orderInfo.orderPrice}}</div>
            </div>
          </div>
        </el-card>

        <!-- 3. 数字资源下载卡片 (仅买家支付后显示) -->
        <el-card class="resource-card" shadow="hover" v-if="isBuyer && orderInfo.paymentStatus === 1 && orderInfo.idleItem.resourcePath">
          <div slot="header" class="clearfix">
            <span style="font-weight: bold; color: #67C23A;"><i class="el-icon-download"></i> 数字资源已交付</span>
          </div>
          <div class="download-area">
            <div class="download-tip">
              <i class="el-icon-info"></i> 安全提示：文件已注入您的专属交易指纹（{{ userInfo.nickname }}），请勿外传。
            </div>
            <el-button type="success" round icon="el-icon-download" @click="downloadResource">
              立即下载资源 ({{ orderInfo.idleItem.originalFileName || '资源包' }})
            </el-button>
            <div v-if="orderInfo.idleItem.unzipPassword" class="password-box">
              解压密码：<span class="pwd-text">{{ orderInfo.idleItem.unzipPassword }}</span>
            </div>
          </div>
        </el-card>

        <!-- 4. 收货联系人 (精简版) -->
        <el-card class="address-card" shadow="hover">
          <div slot="header" class="address-card-header">
            <span><i class="el-icon-user"></i> 收货联系人信息</span>
            <el-button v-if="isBuyer && orderInfo.orderStatus === 0" type="text" icon="el-icon-edit" @click.stop="selectAddressDialog">修改</el-button>
          </div>
          <div class="address-container" :class="{'clickable': isBuyer && orderInfo.orderStatus === 0}" @click.stop="selectAddressDialog">
            <div v-if="addressInfo.consigneeName" class="address-content">
              <div class="address-person">
                <span class="recipient-name">{{addressInfo.consigneeName}}</span>
                <span class="recipient-phone"><i class="el-icon-mobile-phone"></i> {{addressInfo.consigneePhone}}</span>
              </div>
              <div class="delivery-notice"><i class="el-icon-cloudy"></i> 交付方式：支付成功后自动分发权限</div>
            </div>
            <div v-else class="empty-address">
              <i class="el-icon-user empty-icon"></i>
              <p class="empty-text">未设置联系信息</p>
              <el-button @click.stop="selectAddressDialog" type="primary" size="small">选择联系人</el-button>
            </div>
          </div>
        </el-card>

        <!-- 5. 交易详情 -->
        <el-card class="order-info-card" shadow="hover">
          <div slot="header"><span><i class="el-icon-document"></i> 交易日志</span></div>
          <div class="order-info-container">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <div class="order-info-group">
                  <div class="group-title">支付详情</div>
                  <div class="order-info-item"><span class="info-label">支付状态:</span>
                    <el-tag size="small" :type="orderInfo.paymentStatus === 0 ? 'warning' : 'success'">{{orderInfo.paymentStatus === 0 ? '未支付' : '已支付'}}</el-tag>
                  </div>
                  <div class="order-info-item"><span class="info-label">支付方式:</span><span>{{orderInfo.paymentWay || '未支付'}}</span></div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="order-info-group">
                  <div class="group-title">时间戳</div>
                  <div class="order-info-item"><span class="info-label">下单时间:</span><span>{{orderInfo.createTime ? orderInfo.createTime.replace('T',' ').substring(0, 19) : ''}}</span></div>
                  <div class="order-info-item"><span class="info-label">成交时间:</span><span>{{orderInfo.paymentTime ? orderInfo.paymentTime.replace('T',' ').substring(0, 19) : '进行中'}}</span></div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>

        <!-- 6. 底部操作栏 -->
        <el-card class="order-action-card" shadow="hover">
          <div class="order-actions">
            <el-button v-if="isBuyer && orderInfo.orderStatus === 0" type="danger" round plain @click="changeOrderStatus(4,orderInfo)">取消订单</el-button>
            <el-button v-if="isBuyer && orderInfo.orderStatus === 0" type="primary" round icon="el-icon-wallet" @click="changeOrderStatus(1,orderInfo)">立即支付</el-button>
            <el-button v-if="isBuyer && orderInfo.orderStatus === 2" type="success" round icon="el-icon-check" @click="changeOrderStatus(3,orderInfo)">确认收货并结单</el-button>
            <div v-if="orderInfo.orderStatus === 3 && orderInfo.commentFlag === 1" class="order-completed"><i class="el-icon-circle-check"></i> 已完成</div>
          </div>
        </el-card>

        <!-- 7. 评价区域 -->
        <el-card v-if="orderInfo.orderStatus === 3" style="margin-top:20px" shadow="hover">
          <div slot="header">
            <span style="font-weight: bold;"><i class="el-icon-chat-dot-square"></i> 商品评价</span>
          </div>

          <!-- 情况 A：买家尚未评价 -->
          <div v-if="orderInfo.commentFlag !== 1">
            <div v-if="isBuyer" class="comment-form-inner">
              <div style="margin-bottom: 15px; display: flex; align-items: center;">
                <span style="margin-right: 10px;">商品评分:</span>
                <el-rate v-model="form.rating" show-text text-color="#ff9900"></el-rate>
              </div>
              <el-input type="textarea" :rows="4" placeholder="请分享您的使用心得..." v-model="form.content" maxlength="200" show-word-limit/>
              <div style="margin-top: 15px; text-align: right;">
                <el-button type="primary" @click="submitComment" icon="el-icon-edit-outline">提交评价</el-button>
              </div>
            </div>
            <!-- 卖家看到：友好提示 -->
            <div v-else style="text-align: center; padding: 20px; color: #999;">
              <i class="el-icon-timer"></i> 暂无评价，等待买家反馈
            </div>
          </div>

          <!-- 情况 B：评价已完成 (买卖双方均可看到内容) -->
          <div v-else class="comment-display">
            <div v-if="orderComment" style="padding: 10px;">
              <div style="margin-bottom: 10px; display: flex; align-items: center;">
                <span style="margin-right: 10px; color: #666;">买家评分:</span>
                <el-rate v-model="orderComment.rating" disabled show-score text-color="#ff9900"></el-rate>
              </div>
              <div style="background: #f8f9fa; padding: 15px; border-radius: 4px; color: #333; line-height: 1.6;">
                {{ orderComment.content }}
              </div>
              <div style="margin-top: 10px; font-size: 12px; color: #999; text-align: right;">
                评价时间：{{ orderComment.createTime ? orderComment.createTime.replace('T',' ').substring(0,19) : '' }}
              </div>
            </div>
            <div v-else style="text-align: center; padding: 20px; color: #999;">
              正在获取评价详情...
            </div>
          </div>
        </el-card>
      </div>

      <!-- 支付二维码弹窗 -->
      <el-dialog title="支付宝扫码支付" :visible.sync="payDialogVisible" width="380px" center @close="stopPolling" :close-on-click-modal="false">
        <div style="text-align: center;">
          <div class="qr-container" v-if="payQrCodeUrl" style="margin-bottom: 10px;">
            <vue-qr :text="payQrCodeUrl" :size="200" :margin="10" :logoSrc="alipayLogo"></vue-qr>
            <div style="font-size: 13px; color: #909399; margin-top: -5px;">请使用支付宝扫码支付</div>
          </div>

          <div style="margin-top: 15px; font-size: 28px; color: #f56c6c; font-weight: bold;">
            ￥{{ orderInfo.orderPrice }}
          </div>

          <el-divider></el-divider>

          <div style="margin-top: 10px;">
            <el-button type="primary" :loading="payLoading" style="width: 100%;" @click="simulatePaySuccess">
              模拟扫码支付成功
            </el-button>
            <div style="margin-top: 12px; font-size: 12px; color: #999;">提示：演示环境请手动点击上方按钮完成回调</div>
          </div>
        </div>
      </el-dialog>

      <el-dialog title="选择联系信息" :visible.sync="addressDialogVisible" width="500px">
        <el-table :data="addressData" stripe border @row-click="(row)=>selectAddress(0, row)">
          <el-table-column prop="consigneeName" label="姓名" width="120"></el-table-column>
          <el-table-column prop="consigneePhone" label="手机号"></el-table-column>
          <el-table-column label="操作" width="100"><template slot-scope="s"><el-button size="mini" type="text">选择</el-button></template></el-table-column>
        </el-table>
      </el-dialog>

      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import vueQr from 'vue-qr'

export default {
  name: "order",
  components: { AppHead, AppBody, AppFoot, vueQr },
  watch: {
    '$route.query.id': {
      handler(newVal) {
        if (newVal) {
          this.loadOrderData(); // ID 变了就重载数据
        }
      }
    }
  },
  data() {
    return {
      addressDialogVisible:false,
      addressData: [],
      orderInfo: { id: 0, orderNumber: "", orderPrice: 0, orderStatus: 0, paymentStatus: 0, idleItem: {} },
      addressInfo: { id:'', update:false, consigneeName: '', consigneePhone: '' },
      userId: '', userInfo: {},
      form: { rating: 5, content: '' },

      // 支付与轮询
      payDialogVisible: false,
      payLoading: false,
      payQrCodeUrl: '',
      pollingTimer: null,
      alipayLogo: 'https://img.icons8.com/color/48/alipay.png',
      orderComment: null,
    };
  },
  computed: {
    isBuyer() { return String(this.userId) === String(this.orderInfo.userId); },
    isSeller() { return String(this.userId) === String(this.orderInfo.idleItem.userId); }
  },
  created(){
    this.initData();
  },
  beforeDestroy() {
    this.stopPolling();
  },
  methods: {
    initData() {
      this.$api.getUserInfo().then(res => {
        if(res.status_code === 1) {
          this.userInfo = res.data;
          this.userId = res.data.id;
          this.$globalData.userInfo = res.data;
          this.loadOrderData();
        }
      });
    },
    loadOrderData() {
      let orderId = this.$route.query.id;
      if (!orderId) return;

      this.$api.getOrder({ id: orderId }).then(res => {
        if (res.status_code === 1) {
          // 1. 先用一个临时变量接收数据，不要直接操作 this.orderInfo
          let data = res.data;

          // 2. 稳健地处理图片解析
          if (data.idleItem) {
            let picSource = data.idleItem.pictureList;
            if (typeof picSource === 'string' && picSource !== '') {
              try {
                let imgList = JSON.parse(picSource);
                data.idleItem.imgUrl = (Array.isArray(imgList) && imgList.length > 0) ? imgList[0] : '';
              } catch (e) {
                console.error("图片解析失败，数据可能格式有误:", e);
                data.idleItem.imgUrl = '';
              }
            } else if (Array.isArray(picSource)) {
              data.idleItem.imgUrl = picSource.length > 0 ? picSource[0] : '';
            } else {
              data.idleItem.imgUrl = '';
            }
          }
          this.orderInfo = data;

          // 3. 处理评价详情加载
          if (this.orderInfo.commentFlag === 1) {
            this.fetchOrderComment();
          }

          // 4. 处理地址快照
          this.$api.getOrderAddress({ orderId: this.orderInfo.id }).then(addrRes => {
            if(addrRes.data) {
              this.addressInfo = addrRes.data;
              this.addressInfo.update = true;
            } else if (this.orderInfo.orderStatus === 0) {
              this.getAddressData();
            }
          });
        }
      }).catch(err => {
        console.error("获取订单失败:", err);
      });
    },
    fetchOrderComment() {
      this.$api.getCommentList({ idleId: this.orderInfo.idleId }).then(res => {
        if (res.status_code === 1) {
          const comment = res.data.find(c => String(c.orderId) === String(this.orderInfo.id));
          if (comment) {
            this.orderComment = comment;
          }
        }
      });
    },
    startPolling() {
      if(this.pollingTimer) return;
      this.pollingTimer = setInterval(() => {
        this.$api.getOrder({ id: this.orderInfo.id }).then(res => {
          if (res.status_code === 1 && res.data.paymentStatus === 1) {
            this.stopPolling();
            this.payDialogVisible = false;
            // 延时 1 秒跳转，增加成功的反馈感
            setTimeout(() => {
              this.$router.push({
                path: '/pay-result',
                query: { id: this.orderInfo.id, no: this.orderInfo.orderNumber, price: this.orderInfo.orderPrice }
              });
            }, 1000);
          }
        });
      }, 2500);
    },
    stopPolling() {
      if(this.pollingTimer) { clearInterval(this.pollingTimer); this.pollingTimer = null; }
    },
    changeOrderStatus(status, info) {
      if (status === 1) {
        if(!this.addressInfo.consigneeName) return this.$message.error('请先设置联系人信息');
        this.payQrCodeUrl = `https://openapi.alipaydev.com/gateway.do?out_trade_no=${info.orderNumber}`;
        this.payDialogVisible = true;
        this.startPolling();
      } else {
        this.$api.updateOrder({ id: info.id, orderStatus: status }).then(res => {
          if(res.status_code === 1) { this.$message.success('操作成功'); this.loadOrderData(); }
        });
      }
    },
    simulatePaySuccess() {
      this.payLoading = true;
      this.$api.updateOrder({
        id: this.orderInfo.id, orderStatus: 1, paymentStatus: 1, paymentWay: '支付宝支付'
      }).then(res => {
        this.payLoading = false;
        // 成功后轮询会自动带我们起飞
      });
    },
    downloadResource() {
      const token = localStorage.getItem('token');
      window.open(`http://localhost:8080/resource/download?orderId=${this.orderInfo.id}&token=${token}`, '_blank');
    },
    getOrderStatusTextInDetail() {
      const statusMap = ['待付款', '待交付', '已交付(可下载)', '已成功', '已取消'];
      if (this.orderInfo.orderStatus === 3 && this.orderInfo.commentFlag !== 1) return '已成功(待评价)';
      return statusMap[this.orderInfo.orderStatus];
    },
    getStatusType(s) { return ['warning', 'primary', 'success', 'success', 'info'][s]; },
    selectAddressDialog() { if(this.isBuyer && this.orderInfo.orderStatus === 0) this.addressDialogVisible = true; },
    getAddressData() {
      this.$api.getAddress().then(res => {
        if (res.status_code === 1) {
          this.addressData = res.data;
          const def = res.data.find(a => a.defaultFlag);
          if(def && !this.addressInfo.update) this.selectAddress(0, def);
        }
      })
    },
    selectAddress(i, item) {
      this.addressDialogVisible = false;
      this.addressInfo.consigneeName = item.consigneeName;
      this.addressInfo.consigneePhone = item.consigneePhone;
      const api = this.addressInfo.update ? this.$api.updateOrderAddress : this.$api.addOrderAddress;
      api({ id: this.addressInfo.id, orderId: this.orderInfo.id, consigneeName: item.consigneeName, consigneePhone: item.consigneePhone }).then(res => {
        if(res.status_code === 1) { this.addressInfo.update = true; if(!this.addressInfo.id) this.addressInfo.id = res.data.id; }
      });
    },
    submitComment() {
      if(!this.form.content) return this.$message.warning('评价内容不能为空');
      this.$api.addComment({ orderId: this.orderInfo.id, idleId: this.orderInfo.idleId, sellerId: this.orderInfo.idleItem.userId, rating: this.form.rating, content: this.form.content }).then(res => {
        if(res.status_code === 1) { this.$message.success('评价已提交'); this.loadOrderData(); }
      });
    },
    toDetails(id) { this.$router.push({path: 'details', query: {id}}); }
  }
}
</script>

<style scoped>
.order-page-container { min-height: 85vh; padding: 20px; background-color: #f5f7fa; }
.order-header { margin-bottom: 20px; background-color: #fff; padding: 15px 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.order-status { display: flex; justify-content: space-between; align-items: center; }
.order-id { color: #909399; font-size: 13px; }
.product-card, .address-card, .order-info-card, .order-action-card, .resource-card { margin-bottom: 20px; border-radius: 12px; }
.idle-info-container { display: flex; padding: 10px; cursor: pointer; }
.product-image { width: 100px; height: 100px; border-radius: 8px; margin-right: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.idle-info-title { font-size: 16px; font-weight: 600; margin-bottom: 10px; color: #303133; }
.idle-info-price { font-size: 20px; color: #f56c6c; font-weight: bold; }
.download-area { padding: 15px; text-align: center; }
.download-tip { background: #f0f9eb; color: #67c23a; padding: 10px; border-radius: 6px; margin-bottom: 15px; font-size: 13px; }
.password-box { margin-top: 15px; font-size: 14px; color: #606266; }
.pwd-text { color: #f56c6c; font-weight: bold; background: #fff5f5; padding: 2px 8px; border-radius: 4px; border: 1px solid #ffeded; }
.address-container { padding: 10px; }
.recipient-name { font-weight: 600; font-size: 18px; margin-right: 15px; }
.recipient-phone { color: #666; font-size: 16px; }
.delivery-notice { margin-top: 10px; color: #409EFF; font-size: 13px; }
.group-title { font-size: 14px; font-weight: bold; margin-bottom: 12px; color: #333; border-left: 4px solid #409EFF; padding-left: 10px; }
.order-info-item { margin: 8px 0; font-size: 14px; }
.info-label { color: #999; margin-right: 10px; width: 70px; display: inline-block; }
.order-actions { display: flex; justify-content: flex-end; gap: 12px; padding: 10px; }
.order-completed { color: #67c23a; font-weight: bold; }
.clickable { cursor: pointer; transition: background 0.2s; }
.clickable:hover { background: #fafafa; }
</style>