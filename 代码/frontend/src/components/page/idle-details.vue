<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="idle-details-container">
        <!-- 商品详情卡片 -->
        <el-card class="product-card" shadow="hover">
          <!-- 1. 顶部：用户信息与操作按钮 -->
          <div class="details-header">
            <div class="details-header-user-info">
              <el-avatar
                  :size="80"
                  :src="idleItemInfo.user.avatar"
                  class="user-avatar">
              </el-avatar>
              <div class="user-info-text">
                <div class="details-header-user-info-nickname">{{idleItemInfo.user.nickname}}</div>
                <div class="details-header-user-info-time">
                  <i class="el-icon-time"></i> {{idleItemInfo.user.signInTime.substring(0,10)}} 加入平台
                </div>

                <!-- 联系商家 & 投诉商家 (非本人显示) -->
                <div v-if="!isMaster" style="margin-top: 10px; display: flex; gap: 10px;">
                  <el-button
                      type="primary"
                      icon="el-icon-chat-dot-round"
                      size="small"
                      round
                      plain
                      @click="contactSeller">
                    联系商家
                  </el-button>
                  <el-button
                      type="danger"
                      icon="el-icon-warning-outline"
                      size="small"
                      round
                      plain
                      @click="openReportDialog">
                    投诉商家
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 右侧：价格与购买操作 -->
            <div class="details-header-buy" :class="{'owner-controls': isMaster}">
              <div v-show="idleItemInfo.idlePrice !== 0" class="product-price">
                <span class="price-symbol">¥</span>{{idleItemInfo.idlePrice}}
              </div>
              <div v-if="!isMaster && idleItemInfo.idleStatus!==1" class="product-status">
                <i class="el-icon-warning-outline"></i> 闲置已下架或删除
              </div>
              <div class="action-buttons">
                <el-button
                    v-show="idleItemInfo.idlePrice > 0"
                    v-if="!isMaster && idleItemInfo.idleStatus===1"
                    type="danger"
                    icon="el-icon-shopping-cart-2"
                    round
                    @click="buyButton(idleItemInfo)">
                  立即购买
                </el-button>

                <el-button
                    v-show="idleItemInfo.idlePrice > 0"
                    v-if="!isMaster && idleItemInfo.idleStatus===1"
                    :type="isFavorite ? 'warning' : 'primary'"
                    :icon="isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"
                    round
                    @click="favoriteButton(idleItemInfo)">
                  {{isFavorite ? '取消购物车' : '加入购物车'}}
                </el-button>

                <el-button
                    v-if="isMaster && idleItemInfo.idleStatus===1"
                    type="danger"
                    icon="el-icon-download"
                    round
                    @click="changeStatus(idleItemInfo,2)">
                  下架
                </el-button>

                <el-button
                    v-if="isMaster && idleItemInfo.idleStatus===2"
                    type="success"
                    icon="el-icon-upload2"
                    round
                    @click="changeStatus(idleItemInfo,1)">
                  重新上架
                </el-button>
              </div>
            </div>
          </div>

          <!-- 分隔线 -->
          <el-divider></el-divider>

          <!-- 2. 商品核心信息区域  -->
          <div class="details-info">
            <div class="details-info-row">
              <div class="row-left">
                <span class="product-name">{{idleItemInfo.idleName}}</span>
                <el-tag
                    v-if="idleItemInfo.idleLabel"
                    :type="getTagType(idleItemInfo.idleLabel)"
                    size="medium"
                    class="product-tag">
                  {{getCategoryName(idleItemInfo.idleLabel)}}
                </el-tag>
              </div>
              <div class="row-right">
                <span class="item-sales">有{{ idleItemInfo.salesCount || 0 }}人买过</span>
              </div>
            </div>

            <div class="details-info-row second-row">
              <div class="row-left">
                <div class="product-location" v-if="idleItemInfo.idlePlace">
                   {{idleItemInfo.idlePlace}}
                </div>
              </div>
              <div class="row-right">
                <div class="item-rating">
                  <el-rate
                      v-if="idleItemInfo.avgRating > 0"
                      v-model="idleItemInfo.avgRating"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}">
                  </el-rate>
                  <span v-else class="no-rating">商品暂无评分</span>
                </div>
              </div>
            </div>

            <!-- 版权认证标识 -->
            <div class="trust-badges" v-if="idleItemInfo.proofImage" style="margin-bottom: 15px;">
              <el-tag type="success" effect="dark">
                <i class="el-icon-success"></i> 商家已提交原创/授权证明
              </el-tag>
            </div>

            <!-- 详情文本 -->
            <div class="details-info-main" v-html="idleItemInfo.idleDetails">
              {{idleItemInfo.idleDetails}}
            </div>
          </div>

          <!-- 3. 商品图片轮播 -->
          <div class="details-picture">
            <el-carousel
                v-if="idleItemInfo.pictureList && idleItemInfo.pictureList.length > 0"
                :interval="4000"
                type="card"
                height="400px"
                indicator-position="outside"
                class="image-carousel">
              <el-carousel-item v-for="(imgUrl, i) in idleItemInfo.pictureList" :key="i">
                <el-image
                    :src="imgUrl"
                    fit="contain"
                    class="carousel-image"
                    :preview-src-list="idleItemInfo.pictureList">
                </el-image>
              </el-carousel-item>
            </el-carousel>

            <div v-else class="no-images">
              <i class="el-icon-picture-outline"></i>
              <p>暂无图片</p>
            </div>
          </div>
        </el-card>

        <!-- 4. 商品评价区域 -->
        <el-card class="comment-card" shadow="hover">
          <div slot="header" class="comment-header">
            <span><i class="el-icon-s-comment"></i> 商品评价</span>
            <el-badge :value="commentList.length" :max="99" class="comment-badge" type="primary"/>
          </div>

          <!-- 有评价时显示列表 -->
          <div v-if="commentList.length > 0" class="comment-list">
            <div v-for="(item, index) in commentList" :key="index" class="comment-item">
              <div class="comment-left">
                <el-avatar
                    :size="50"
                    :src="item.buyer && item.buyer.avatar ? item.buyer.avatar : 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                    class="comment-avatar">
                </el-avatar>
              </div>
              <div class="comment-right">
                <div class="comment-top">
                  <span class="comment-nickname">{{ item.buyer ? item.buyer.nickname : '未知用户' }}</span>
                  <el-rate
                      v-model="item.rating"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}">
                  </el-rate>
                </div>
                <div class="comment-content">{{ item.content }}</div>
                <div class="comment-time">{{ item.createTime }}</div>
              </div>
            </div>
          </div>

          <!-- 无评价时显示 -->
          <div v-else class="no-comments">
            <i class="el-icon-chat-line-square"></i>
            <p>该商品暂无评价</p>
          </div>
        </el-card>

        <!-- 5. 猜你喜欢 (新增区域) -->
        <el-card class="related-card" shadow="hover" style="margin-top: 20px;" v-if="relatedList.length > 0">
          <div slot="header" class="related-header">
            <span><i class="el-icon-magic-stick"></i> 猜你喜欢 (同类推荐)</span>
          </div>
          <el-row :gutter="20">
            <el-col :xs="12" :sm="6" v-for="item in relatedList" :key="item.id">
              <div class="related-item" @click="toNewDetail(item.id)">
                <div class="rel-img-box">
                  <el-image :src="item.imgUrl" fit="cover" class="rel-img">
                    <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                  </el-image>
                </div>
                <div class="rel-info">
                  <div class="rel-name">{{item.idleName}}</div>
                  <div class="rel-bottom">
                    <span class="rel-price">￥{{item.idlePrice}}</span>
                    <span class="rel-sales">{{item.salesCount || 0}}人买过</span>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 6. 投诉对话框 -->
        <el-dialog title="投诉商家" :visible.sync="reportVisible" width="550px" center>
          <el-form :model="reportForm" label-width="0px">
            <div class="report-title-tip">请选择您要投诉的原因：</div>
            <el-form-item>
              <el-radio-group v-model="reportForm.reason" class="report-radio-group">
                <el-radio label="货不对板" class="report-radio">
                  货不对板 <span class="sub-text">(收到的商品与描述严重不符)</span>
                </el-radio>
                <el-radio label="发布违禁品" class="report-radio">
                  发布违禁品 <span class="sub-text">(法律禁止交易的物品、危险品)</span>
                </el-radio>
                <el-radio label="侵权行为" class="report-radio">
                  侵权行为 <span class="sub-text">(存在盗版、侵犯他人知识产权的行为)</span>
                </el-radio>
                <el-radio label="诈骗/欺诈" class="report-radio">
                  诈骗/欺诈 <span class="sub-text">(虚假发货、骗取定金、价格欺诈等)</span>
                </el-radio>
                <el-radio label="引导站外交易" class="report-radio">
                  引导站外交易 <span class="sub-text">(诱导私下转账、加微信/QQ交易)</span>
                </el-radio>
                <el-radio label="辱骂/骚扰他人" class="report-radio">
                  辱骂/骚扰他人 <span class="sub-text">(言语辱骂、频繁骚扰、人身攻击)</span>
                </el-radio>
                <el-radio label="其他" class="report-radio">
                  其他 <span class="sub-text">(其他违规行为)</span>
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <div class="report-title-tip">详细说明（选填）：</div>
            <el-form-item>
              <el-input
                  type="textarea"
                  :rows="4"
                  placeholder="请详细描述您遇到的问题，有助于管理员快速处理..."
                  v-model="reportForm.content"
                  maxlength="200"
                  show-word-limit>
              </el-input>
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
                        <el-button @click="reportVisible = false">取 消</el-button>
                        <el-button type="primary" @click="submitReport">提交投诉</el-button>
                    </span>
        </el-dialog>
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import { getCategoryLabel, getCategoryTagType } from '@/utils/categoryConfig';

export default {
  name: "idle-details",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      idleItemInfo:{
        id:'',
        idleName:'',
        idleDetails:'',
        pictureList:[],
        idlePrice:0,
        idlePlace:'',
        idleLabel:'',
        idleStatus:-1,
        userId:'',
        proofImage: '', // 版权证明
        salesCount: 0,  // 销量
        avgRating: 0,   // 评分
        user:{
          avatar:'',
          nickname:'',
          signInTime:''
        },
      },
      isMaster:false, // 是否是自己的商品
      isFavorite:true,
      favoriteId:0,
      commentList: [], // 评价列表
      relatedList: [], // 【新增】猜你喜欢列表

      // 投诉相关数据
      reportVisible: false,
      reportForm: {
        reason: '',
        content: ''
      }
    };
  },
  // 【新增】监听路由ID变化，确保点击推荐商品后页面能重载
  watch: {
    '$route.query.id': {
      handler(newVal) {
        if (newVal) {
          this.initData(newVal);
        }
      }
    }
  },
  created(){
    this.initData(this.$route.query.id);
  },
  methods: {
    // 【新增】封装初始化逻辑
    initData(id) {
      // 重置部分状态
      this.isMaster = false;
      this.relatedList = [];

      // 1. 获取评价
      this.getCommentList(id);

      // 2. 获取商品详情
      this.$api.getIdleItem({
        id:id
      }).then(res=>{
        if(res.data){
          let list=res.data.idleDetails.split(/\r?\n/);
          let str='';
          for(let i=0;i<list.length;i++){
            str+='<p>'+list[i]+'</p>';
          }
          res.data.idleDetails=str;
          res.data.pictureList=JSON.parse(res.data.pictureList);
          res.data.avgRating = res.data.avgRating ? parseFloat(res.data.avgRating) : 0;
          this.idleItemInfo=res.data;

          if(this.$globalData.userInfo && String(this.$globalData.userInfo.id) === String(this.idleItemInfo.userId)){
            this.isMaster=true;
          }
          this.checkFavorite();

          // 【新增】加载完详情后获取推荐位
          this.fetchRelatedItems(res.data.idleLabel, res.data.id);
        }
        // 滚动到顶部
        window.scrollTo({ top: 0, behavior: 'smooth' });
      });
    },

    // 【新增】获取推荐位方法
    fetchRelatedItems(label, currentId) {
      this.$api.getRecommend({ label: label, currentId: currentId }).then(res => {
        if(res.status_code === 1) {
          this.relatedList = res.data.map(item => {
            // 解析图片逻辑同步后端 fillUserInfo
            if(!item.imgUrl && item.pictureList) {
              let pics = JSON.parse(item.pictureList || '[]');
              item.imgUrl = pics[0];
            }
            return item;
          });
        }
      });
    },

    // 【新增】跳转到新详情页方法
    toNewDetail(id) {
      this.$router.push({ path: '/details', query: { id: id } });
    },

    contactSeller() {
      if(!localStorage.getItem('token')){
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      this.$router.push({
        path: '/chat',
        query: {
          targetUserId: this.idleItemInfo.userId,
          targetName: this.idleItemInfo.user.nickname,
          idleId: this.idleItemInfo.id
        }
      });
    },

    openReportDialog() {
      if(!localStorage.getItem('token')){
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      this.reportForm = { reason: '', content: '' };
      this.reportVisible = true;
    },

    submitReport() {
      if (!this.reportForm.reason) {
        this.$message.warning('请选择投诉理由');
        return;
      }
      if (this.reportForm.reason === '其他' && !this.reportForm.content.trim()) {
        this.$message.warning('选择"其他"时，请填写详细说明');
        return;
      }

      this.$api.addReport({
        idleId: this.idleItemInfo.id,
        reportedUserId: this.idleItemInfo.userId,
        reason: this.reportForm.reason,
        content: this.reportForm.content
      }).then(res => {
        if (res.status_code === 1) {
          this.$message.success('投诉已提交，我们会尽快处理');
          this.reportVisible = false;
        } else {
          this.$message.error(res.msg || '提交失败');
        }
      });
    },

    getCommentList(idleId) {
      this.$api.getCommentList({
        idleId: idleId
      }).then(res => {
        if (res.status_code === 1) {
          let list = res.data;
          for (let i = 0; i < list.length; i++) {
            if(list[i].createTime){
              list[i].createTime = list[i].createTime.replace("T", " ").substring(0, 19);
            }
          }
          this.commentList = list;
        }
      }).catch(err => {
        console.error("获取评价失败", err);
      });
    },
    getCategoryName(label) {
      return getCategoryLabel(label);
    },
    getTagType(label) {
      return getCategoryTagType(label);
    },
    checkFavorite(){
      this.$api.checkFavorite({
        idleId:this.$route.query.id
      }).then(res=>{
        if(!res.data){
          this.isFavorite=false;
        }else {
          this.favoriteId=res.data;
        }
      })
    },

    changeStatus(idle,status){
      this.$api.updateIdleItem({
        id:idle.id,
        idleStatus:status
      }).then(res=>{
        console.log(res);
        if(res.status_code===1){
          this.idleItemInfo.idleStatus=status;
          this.$message({
            message: status === 1 ? '商品已重新上架' : '商品已下架',
            type: 'success'
          });
        }else {
          this.$message.error(res.msg)
        }
      });
    },
    buyButton(idleItemInfo){
      if(!localStorage.getItem('token')){
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      this.$api.addOrder({
        idleId:idleItemInfo.id,
        orderPrice:idleItemInfo.idlePrice,
      }).then(res=>{
        console.log(res);
        if(res.status_code===1){
          this.$router.push({path: '/order', query: {id: res.data.id}});
        }else {
          this.$message.error(res.msg)
        }
      }).catch(e=>{
        console.log(e);
      })
    },
    favoriteButton(idleItemInfo){
      if(!localStorage.getItem('token')){
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      if(this.isFavorite){
        this.$api.deleteFavorite({
          id: this.favoriteId
        }).then(res=>{
          console.log(res);
          if(res.status_code===1){
            this.$message({
              message: '已取消购物车！',
              type: 'success'
            });
            this.isFavorite=false;
          }else {
            this.$message.error(res.msg)
          }
        }).catch(e=>{
        })
      }else {
        this.$api.addFavorite({
          idleId:idleItemInfo.id
        }).then(res=>{
          console.log(res);
          if(res.status_code===1){
            this.$message({
              message: '已加入购物车！',
              type: 'success'
            });
            this.isFavorite=true;
            this.favoriteId=res.data;
          }else {
            this.$message.error(res.msg)
          }
        }).catch(e=>{
        })
      }
    }
  }
}
</script>

<style scoped>
.idle-details-container {
  min-height: 85vh;
  padding: 20px 0;
}

.product-card {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  flex-wrap: wrap;
}

.details-header-user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: 2px solid #fff;
}

.user-info-text {
  margin-left: 15px;
}

.details-header-user-info-nickname {
  font-weight: 600;
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
}

.details-header-user-info-time {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
}

.details-header-user-info-time i {
  margin-right: 5px;
}

.details-header-buy {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.owner-controls {
  align-items: center;
}

.product-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 15px;
}

.price-symbol {
  font-size: 16px;
  font-weight: normal;
  margin-right: 2px;
}

.product-status {
  color: #f56c6c;
  font-size: 16px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.product-status i {
  margin-right: 5px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.details-info {
  padding: 20px 0;
}

.details-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.second-row {
  margin-bottom: 20px;
}

.row-left {
  display: flex;
  align-items: center;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-right: 10px;
}

.product-tag {
  margin-left: 10px;
}

.product-location {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
}

.product-location i {
  margin-right: 5px;
  color: #e6a23c;
}

.item-sales {
  color: #E6A23C;
  font-weight: bold;
  font-size: 14px;
}

.item-rating {
  display: flex;
  align-items: center;
  transform: scale(1.1);
}

.no-rating {
  font-size: 12px;
  color: #999;
}

.details-info-main {
  font-size: 16px;
  color: #606266;
  line-height: 1.8;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.details-picture {
  margin: 10px 0 30px;
}

.image-carousel {
  margin: 0 auto;
}

.carousel-image {
  height: 100%;
  width: 100%;
}

.no-images {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  height: 300px;
  color: #909399;
  border-radius: 4px;
}

.no-images i {
  font-size: 50px;
  margin-bottom: 10px;
}

.comment-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.comment-header {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.comment-badge {
  margin-left: 10px;
}

.comment-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-left {
  margin-right: 15px;
  flex-shrink: 0;
}

.comment-avatar {
  border: 1px solid #eee;
}

.comment-right {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.comment-top {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.comment-nickname {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-right: 15px;
}

.comment-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.no-comments {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.no-comments i {
  font-size: 40px;
  margin-bottom: 10px;
  display: block;
}

/* 【新增样式】推荐位部分 */
.related-header { font-size: 18px; font-weight: bold; color: #333; }
.related-item { cursor: pointer; transition: all 0.3s; background: #fff; border-radius: 8px; overflow: hidden; border: 1px solid #f0f0f0; margin-bottom: 15px; }
.related-item:hover { transform: translateY(-3px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.rel-img-box { height: 120px; width: 100%; overflow: hidden; }
.rel-img { height: 100%; width: 100%; }
.rel-info { padding: 10px; }
.rel-name { font-size: 14px; color: #333; height: 1.4em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 5px; }
.rel-bottom { display: flex; justify-content: space-between; align-items: center; }
.rel-price { color: #f56c6c; font-weight: bold; font-size: 15px; }
.rel-sales { font-size: 11px; color: #999; }

.report-title-tip {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.report-radio-group {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.report-radio {
  margin-bottom: 12px;
  margin-left: 0 !important;
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.report-radio >>> .el-radio__label {
  font-size: 16px;
  color: #333;
  line-height: 1.5;
}

.sub-text {
  font-size: 13px;
  color: #909399;
  margin-left: 5px;
}

@media (max-width: 768px) {
  .details-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .details-header-buy {
    margin-top: 20px;
    align-items: flex-start;
    width: 100%;
  }

  .action-buttons {
    flex-wrap: wrap;
    margin-top: 10px;
  }
}
</style>