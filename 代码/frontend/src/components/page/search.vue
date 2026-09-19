<template>
  <div>
    <app-head :searchInput="searchValue"></app-head>
    <app-body>
      <div style="min-height: 85vh;">
        <!-- 搜索结果标题与高级排序栏 -->
        <div class="search-header">
          <div class="header-left">
            关于 <span class="search-keyword">"{{ searchValue }}"</span> 的搜索结果
            <span class="total-count" v-if="totalItem > 0">（共计 {{ totalItem }} 件）</span>
          </div>

          <div class="sort-bar">
            <span class="sort-label">排序：</span>
            <el-button-group>
              <el-button size="small" :type="sortMode === 'time' ? 'primary' : ''" @click="toggleSort('time')">
                综合 <i :class="getSortIcon('time')"></i>
              </el-button>
              <el-button size="small" :type="sortMode === 'sales' ? 'primary' : ''" @click="toggleSort('sales')">
                销量 <i :class="getSortIcon('sales')"></i>
              </el-button>
              <el-button size="small" :type="sortMode === 'rating' ? 'primary' : ''" @click="toggleSort('rating')">
                评分 <i :class="getSortIcon('rating')"></i>
              </el-button>
              <el-button size="small" :type="sortMode === 'price' ? 'primary' : ''" @click="toggleSort('price')">
                价格 <i :class="getSortIcon('price')"></i>
              </el-button>
            </el-button-group>
          </div>
        </div>

        <div class="items-container">
          <!-- 空状态处理 -->
          <div v-if="idleList.length === 0 && !loading" class="empty-state">
            <i class="el-icon-search"></i>
            <p>没有找到相关的</p>
            <el-button type="primary" size="small" plain @click="$router.push('/')">去首页看看</el-button>
          </div>

          <!-- 列表展示 -->
          <el-row :gutter="20" v-loading="loading">
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
                    <div class="item-place"><i class="el-icon-location"></i> {{idle.idlePlace}}</div>
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
        </div>

        <div class="pagination-container" v-if="totalItem > 0">
          <el-pagination
              background
              layout="prev, pager, next, jumper"
              :current-page="currentPage"
              :page-size="8"
              :total="totalItem"
              @current-change="handleCurrentChange">
          </el-pagination>
        </div>
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import { getCategoryLabel } from '@/utils/categoryConfig';

export default {
  name: "search",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      idleList: [],
      currentPage: 1,
      searchValue: '',
      totalItem: 0,
      loading: false,

      // 排序状态控制
      sortMode: 'time',
      isAsc: false
    };
  },
  computed: {
    // 映射与 index.vue 一致的 0-7 排序逻辑
    sortTypeValue() {
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
    this.initParams();
    this.loadData();
  },
  watch: {
    '$route'(to) {
      this.initParams();
      this.loadData();
    }
  },
  methods: {
    initParams() {
      this.searchValue = this.$route.query.searchValue || '';
      this.currentPage = parseInt(this.$route.query.page) || 1;
      // 初始化排序方向 (可选：从 URL 读取 sortType 反推方向)
      const st = parseInt(this.$route.query.sortType) || 0;
      this.syncSortState(st);
    },

    // 根据 sortType 反向同步按钮 UI 状态
    syncSortState(st) {
      if (st <= 1) this.sortMode = 'time', this.isAsc = (st === 1);
      else if (st <= 3) this.sortMode = 'sales', this.isAsc = (st === 3);
      else if (st <= 5) this.sortMode = 'rating', this.isAsc = (st === 5);
      else this.sortMode = 'price', this.isAsc = (st === 6);
    },

    loadData() {
      this.loading = true;
      this.idleList = [];
      this.$api.findIdleItem({
        page: this.currentPage,
        nums: 8,
        findValue: this.searchValue,
        sortType: this.sortTypeValue
      }).then(res => {
        if(res.status_code === 1){
          let list = res.data.list || [];
          list.forEach(item => {
            item.timeStr = item.releaseTime.replace('T', ' ');
            let pictureList = JSON.parse(item.pictureList || '[]');
            item.imgUrl = pictureList.length > 0 ? pictureList[0] : '';
            item.avgRating = item.avgRating ? parseFloat(item.avgRating) : 0;
          });
          this.idleList = list;
          this.totalItem = res.data.count;
        }
      }).catch(e => {
        this.$message.error('搜索异常，请稍后重试');
      }).finally(() => {
        this.loading = false;
      });
    },

    // 核心切换逻辑
    toggleSort(mode) {
      if (this.sortMode === mode) {
        this.isAsc = !this.isAsc;
      } else {
        this.sortMode = mode;
        this.isAsc = (mode === 'price');
      }
      this.currentPage = 1;
      this.updateUrl();
    },

    getSortIcon(mode) {
      if (this.sortMode !== mode) return 'el-icon-d-caret';
      return this.isAsc ? 'el-icon-caret-top' : 'el-icon-caret-bottom';
    },

    updateUrl() {
      this.$router.push({
        path: '/search',
        query: {
          searchValue: this.searchValue,
          page: this.currentPage,
          sortType: this.sortTypeValue
        }
      }).catch(err => {});
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.updateUrl();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    toDetails(idle) {
      this.$router.push({path: '/details', query: {id: idle.id}});
    },
    getItemCategory(label) {
      return getCategoryLabel(label);
    }
  }
}
</script>

<style scoped>
.search-header {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}
.header-left { font-size: 18px; color: #606266; }
.search-keyword { color: #409EFF; font-weight: bold; margin: 0 5px; }
.total-count { font-size: 14px; color: #999; }

.sort-bar { display: flex; align-items: center; }
.sort-label { font-size: 14px; color: #909399; margin-right: 12px; }

.items-container { padding: 0 5px; min-height: 400px; }
.item-card { height: 100%; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); transition: all 0.3s; margin-bottom: 25px; background: #fff; cursor: pointer; }
.item-card:hover { transform: translateY(-5px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2); }
.item-image-container { position: relative; height: 180px; }
.item-image { width: 100%; height: 100%; }
.item-tag { position: absolute; top: 10px; right: 10px; background: rgba(64, 158, 255, 0.8); color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.item-content { padding: 15px; }
.item-title { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-meta { display: flex; justify-content: space-between; margin-bottom: 8px; }
.item-price { font-size: 20px; color: #f56c6c; font-weight: bold; }
.item-place { font-size: 12px; color: #909399; }
.item-time-sales { display: flex; justify-content: space-between; font-size: 12px; color: #999; margin-bottom: 10px; }
.item-sales { color: #E6A23C; font-weight: bold; }
.user-info-row { display: flex; justify-content: space-between; align-items: center; padding-top: 10px; border-top: 1px solid #f0f0f0; }
.user-left { display: flex; align-items: center; }
.user-nickname { margin-left: 10px; font-size: 13px; color: #666; max-width: 60px; overflow: hidden; text-overflow: ellipsis; }
.item-rating { transform: scale(0.85); transform-origin: right center; }

.empty-state { text-align: center; padding: 100px 0; color: #909399; }
.empty-state i { font-size: 60px; margin-bottom: 20px; }
.pagination-container { display: flex; justify-content: center; padding: 30px 0; }

@media (max-width: 768px) {
  .search-header { flex-direction: column; align-items: flex-start; }
  .sort-bar { margin-top: 15px; }
}
</style>