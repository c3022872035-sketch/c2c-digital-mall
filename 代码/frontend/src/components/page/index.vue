<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div style="min-height: 85vh;">
        <!-- 1. 页面大标题 -->
        <div class="page-header">
          <h1 class="page-title"><span class="highlight">C2C数字交易平台</span></h1>
        </div>

        <!-- 3. 分类标签 -->
        <div class="category-tabs">
          <el-tabs v-model="labelName" type="border-card" @tab-click="handleTabClick" class="custom-tabs">
            <el-tab-pane label="全部" name="0">
              <span slot="label"><i class="el-icon-s-grid tab-icon"></i> 全部</span>
            </el-tab-pane>
            <el-tab-pane
                v-for="item in globalCategories"
                :key="item.value"
                :name="item.value.toString()">
              <span slot="label"><i :class="item.icon" class="tab-icon"></i> {{item.label}}</span>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 4. 排序筛选栏 -->
        <div class="filter-container">
          <div class="sort-bar">
            <span class="sort-label">排序方式：</span>
            <div v-if="labelName !== '10'" class="sort-buttons">
              <el-button-group>
                <el-button size="small" :type="sortMode === 'time' ? 'primary' : ''" @click="toggleSort('time')">
                  综合最新 <i :class="getSortIcon('time')"></i>
                </el-button>
                <el-button size="small" :type="sortMode === 'sales' ? 'primary' : ''" @click="toggleSort('sales')">
                  销量优先 <i :class="getSortIcon('sales')"></i>
                </el-button>
                <el-button size="small" :type="sortMode === 'rating' ? 'primary' : ''" @click="toggleSort('rating')">
                  好评优先 <i :class="getSortIcon('rating')"></i>
                </el-button>
                <el-button size="small" :type="sortMode === 'price' ? 'primary' : ''" @click="toggleSort('price')">
                  价格 <i :class="getSortIcon('price')"></i>
                </el-button>
              </el-button-group>
            </div>
            <div v-else class="sort-buttons">
              <el-button size="small" type="primary" disabled>综合最新</el-button>
              <span class="notice-tip"><i class="el-icon-info"></i> 公告类仅支持按时间排序</span>
            </div>
          </div>
        </div>

        <!-- 5. 商品卡片列表 -->
        <div class="items-container">
          <el-row :gutter="20" v-if="idleList.length > 0">
            <el-col :xs="24" :sm="12" :md="6" v-for="(idle, index) in idleList" :key="index">
              <div class="item-card" @click="toDetails(idle)">
                <div class="item-image-container">
                  <el-image class="item-image" :src="idle.imgUrl" fit="cover">
                    <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                  </el-image>
                  <div class="item-tag">{{getItemCategory(idle.idleLabel)}}</div>
                </div>
                <div class="item-content">
                  <h3 class="item-title">{{idle.idleName}}</h3>
                  <div class="item-meta">
                    <div class="item-price" v-show="idle.idleLabel !== 10">¥{{idle.idlePrice}}</div>
                    <div class="item-place"> {{idle.idlePlace}}</div>
                  </div>
                  <div class="item-time-sales">
                    <span class="item-time"><i class="el-icon-time"></i> {{idle.timeStr.substring(0,10)}}</span>
                    <span class="item-sales" v-show="idle.idleLabel !== 10">销量 {{ idle.salesCount || 0 }}</span>
                  </div>
                  <div class="user-info-row">
                    <div class="user-left">
                      <el-avatar :size="24" :src="idle.user.avatar"></el-avatar>
                      <div class="user-nickname">{{idle.user.nickname}}</div>
                    </div>
                    <div class="item-rating" v-show="idle.idleLabel !== 10">
                      <el-rate v-if="idle.avgRating > 0" v-model="idle.avgRating" disabled text-color="#ff9900"></el-rate>
                      <span v-else class="no-rating">暂无</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
          <div v-else class="empty-state">
            <i class="el-icon-folder-opened"></i>
            <p>该分类下暂无资源</p>
          </div>
        </div>

        <!-- 6. 分页 -->
        <div class="pagination-container">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page.sync="currentPage"
              :page-size="8"
              layout="total, prev, pager, next"
              :total="totalItem">
          </el-pagination>
        </div>
      </div>

      <!-- 7. 热门精选推荐位 -->
      <div class="recommend-container" v-if="recommendList.length > 0">
        <div class="recommend-title">
          <i class="el-icon-medal-1"></i> 热门精选资源
          <span class="recommend-sub">大家都在买的好货</span>
        </div>
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6" v-for="(item, index) in recommendList" :key="'rec-'+index">
            <div class="recommend-card" @click="toDetails(item)">
              <div class="rec-img-wrapper">
                <el-image :src="item.imgUrl" fit="cover" class="rec-img">
                  <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                </el-image>
                <div class="rec-hot-tag">HOT</div>
              </div>
              <div class="rec-info">
                <div class="rec-name">{{item.idleName}}</div>
                <div class="rec-bottom">
                  <span class="rec-price">￥{{item.idlePrice}}</span>
                  <span class="rec-sales">销量 {{item.salesCount || 0}}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';
import { categories } from '@/utils/categoryConfig';

export default {
  name: "index",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      labelName: '0',
      idleList: [],
      recommendList: [], // 补齐推荐列表数据模型
      currentPage: 1,
      totalItem: 0,
      sortMode: 'time',
      isAsc: false,
      globalCategories: categories
    };
  },
  computed: {
    sortTypeValue() {
      if (this.labelName === '10') return 0;
      const map = {
        'time':   this.isAsc ? 1 : 0,
        'sales':  this.isAsc ? 3 : 2,
        'rating': this.isAsc ? 5 : 4,
        'price':  this.isAsc ? 6 : 7
      };
      return map[this.sortMode];
    }
  },
  created() {
    const queryLabel = this.$route.query.labelName;
    if (queryLabel) {
      this.labelName = queryLabel.toString();
    }
    this.currentPage = parseInt(this.$route.query.page) || 1;
    this.fetchData(this.currentPage);

    // 获取热门推荐数据
    this.fetchRecommendations();
  },
  methods: {
    fetchRecommendations() {
      // 调用后台推荐接口 (不传参数默认拉取热门)
      this.$api.getRecommend().then(res => {
        if(res.status_code === 1) {
          this.recommendList = res.data.map(item => {
            // 后端 fillUserInfo 已经处理了 imgUrl，这里做个兜底
            if(!item.imgUrl && item.pictureList) {
              let pics = JSON.parse(item.pictureList || '[]');
              item.imgUrl = pics[0];
            }
            return item;
          });
        }
      });
    },

    handleTabClick(tab) {
      this.labelName = tab.name;
      this.currentPage = 1;
      this.$router.push({
        query: { labelName: this.labelName, page: 1 }
      }).catch(err => {});

      if (this.labelName !== '10') {
        this.sortMode = 'time';
        this.isAsc = false;
      }
      this.fetchData(1);
    },

    fetchData(page) {
      this.idleList = [];
      const loading = this.$loading({
        lock: true,
        text: '检索中...',
        background: 'rgba(255, 255, 255, 0.7)'
      });

      if (this.labelName === '0') {
        this.$api.findIdleItem({
          page: page, nums: 8, sortType: this.sortTypeValue
        }).then(res => { this.handleRes(res); }).finally(() => { loading.close(); });
      } else {
        this.$api.findIdleItemByLabel({
          idleLabel: parseInt(this.labelName), page: page, nums: 8, sortType: this.sortTypeValue
        }).then(res => { this.handleRes(res); }).finally(() => { loading.close(); });
      }
    },

    handleRes(res) {
      if (res.status_code === 1) {
        let list = res.data.list || [];
        list.forEach(item => {
          item.timeStr = item.releaseTime.replace('T', ' ');
          // 使用后端统一处理的 imgUrl，如果为空则前端解析
          if(!item.imgUrl && item.pictureList) {
            let pictureList = [];
            try { pictureList = JSON.parse(item.pictureList); } catch (e) { pictureList = []; }
            item.imgUrl = pictureList.length > 0 ? pictureList[0] : '';
          }
          item.avgRating = item.avgRating ? parseFloat(item.avgRating) : 0;
        });
        this.idleList = list;
        this.totalItem = res.data.count;
      }
    },

    toggleSort(mode) {
      if (this.sortMode === mode) {
        this.isAsc = !this.isAsc;
      } else {
        this.sortMode = mode;
        this.isAsc = (mode === 'price');
      }
      this.currentPage = 1;
      this.fetchData(1);
    },

    getSortIcon(mode) {
      if (this.sortMode !== mode) return 'el-icon-d-caret';
      return this.isAsc ? 'el-icon-caret-top' : 'el-icon-caret-bottom';
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.$router.push({ query: { ...this.$route.query, page: val } }).catch(err => {});
      this.fetchData(val);
      window.scrollTo(0, 0);
    },

    toDetails(idle) {
      this.$router.push({path: '/details', query: {id: idle.id}});
    },

    getItemCategory(label) {
      const item = this.globalCategories.find(c => c.value === parseInt(label));
      return item ? item.label : '';
    }
  }
}
</script>

<style scoped>
.page-header { text-align: center; padding: 25px 0; margin-bottom: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.page-title { font-size: 26px; font-weight: 600; color: #303133; }
.highlight { color: #409EFF; }

/* 推荐位样式 */
.recommend-container { margin-bottom: 30px; padding: 0 10px; }
.recommend-title { font-size: 20px; font-weight: bold; color: #333; margin-bottom: 15px; display: flex; align-items: center; }
.recommend-title i { color: #f56c6c; margin-right: 8px; font-size: 24px; }
.recommend-sub { font-size: 12px; color: #999; font-weight: normal; margin-left: 10px; }

.recommend-card { background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%); border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1); cursor: pointer; transition: all 0.3s; border: 1px solid #eef6ff; }
.recommend-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(64, 158, 255, 0.2); }
.rec-img-wrapper { position: relative; height: 130px; }
.rec-img { width: 100%; height: 100%; }
.rec-hot-tag { position: absolute; top: 0; left: 0; background: #f56c6c; color: #fff; font-size: 10px; padding: 2px 8px; border-bottom-right-radius: 10px; font-weight: bold; }
.rec-info { padding: 12px; }
.rec-name { font-size: 14px; font-weight: bold; color: #333; height: 1.4em; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rec-bottom { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.rec-price { color: #f56c6c; font-weight: bold; font-size: 16px; }
.rec-sales { font-size: 11px; color: #999; }

.category-tabs { margin-bottom: 15px; }
.custom-tabs { border-radius: 8px; overflow: hidden; }
.filter-container { padding: 0 5px; margin-bottom: 20px; display: flex; justify-content: flex-end; }
.sort-bar { background: #fff; padding: 10px 20px; border-radius: 4px; border: 1px solid #ebeef5; display: flex; align-items: center; }
.sort-label { font-size: 14px; color: #606266; margin-right: 15px; }

.item-card { height: 100%; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.08); transition: all 0.3s; margin-bottom: 25px; background: #fff; cursor: pointer; }
.item-card:hover { transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0,0,0,0.12); }
.item-image-container { position: relative; height: 180px; }
.item-image { width: 100%; height: 100%; }
.item-tag { position: absolute; top: 10px; right: 10px; background: rgba(64, 158, 255, 0.9); color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.item-content { padding: 15px; }
.item-title { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.item-price { font-size: 20px; color: #f56c6c; font-weight: bold; }
.item-place { font-size: 12px; color: #909399; }
.item-time-sales { display: flex; justify-content: space-between; font-size: 12px; color: #999; margin-bottom: 12px; }
.item-sales { color: #E6A23C; font-weight: bold; }
.user-info-row { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f0f0; padding-top: 10px; }
.user-nickname { margin-left: 8px; font-size: 13px; color: #666; max-width: 70px; overflow: hidden; text-overflow: ellipsis; }
.item-rating { transform: scale(0.8); transform-origin: right center; }
.pagination-container { display: flex; justify-content: center; padding: 20px 0; }
.empty-state { text-align: center; padding: 100px 0; color: #c0c4cc; }
.empty-state i { font-size: 60px; margin-bottom: 10px; }
</style>