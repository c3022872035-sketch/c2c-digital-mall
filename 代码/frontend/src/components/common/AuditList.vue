<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">审核管理</h2>
      <div class="search-container">
        <el-input
            placeholder="搜索商品名称 / 发布用户"
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
      <el-tab-pane label="待审核" name="3"></el-tab-pane>
      <el-tab-pane label="审核通过" name="1"></el-tab-pane>
      <el-tab-pane label="审核驳回" name="4"></el-tab-pane>
    </el-tabs>

    <div class="table-container">
      <el-table
          :data="list"
          stripe
          border
          v-loading="loading"
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '16px', height: '60px' }"
          :cell-style="{ fontSize: '16px', color: '#303133', padding: '15px 0' }">

        <el-table-column label="预览图" width="130" align="center">
          <template slot-scope="scope">
            <div class="img-switcher" v-if="scope.row.imgList && scope.row.imgList.length > 0">
              <div class="arrow-btn left" @click.stop="prevImg(scope.row)" v-show="scope.row.imgList.length > 1"><i class="el-icon-arrow-left"></i></div>
              <el-image style="width: 80px; height: 80px; border-radius: 4px;" :src="scope.row.imgList[scope.row.curImgIdx]" :preview-src-list="scope.row.imgList" fit="cover"></el-image>
              <div class="arrow-btn right" @click.stop="nextImg(scope.row)" v-show="scope.row.imgList.length > 1"><i class="el-icon-arrow-right"></i></div>
              <div class="img-indicator" v-if="scope.row.imgList.length > 1">{{ scope.row.curImgIdx + 1 }} / {{ scope.row.imgList.length }}</div>
            </div>
            <span v-else style="color:#999; font-size: 16px;">无图</span>
          </template>
        </el-table-column>

        <el-table-column prop="idleName" label="商品名称" min-width="150" align="center">
          <template slot-scope="scope"><div class="wrap-text">{{ scope.row.idleName }}</div></template>
        </el-table-column>

        <el-table-column prop="idleDetails" label="商品详情" show-overflow-tooltip min-width="180" align="center">
          <template slot-scope="scope"><div class="wrap-text">{{ scope.row.idleDetails }}</div></template>
        </el-table-column>

        <el-table-column label="发布用户" width="170" align="center">
          <template slot-scope="scope">
            <div class="user-info-wrapper">
              <el-avatar :size="45" :src="scope.row.user ? scope.row.user.avatar : ''" class="user-avatar"></el-avatar>
              <span class="user-nickname">{{ scope.row.user ? scope.row.user.nickname : '未知' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="价格" width="100" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.idleLabel === 10" style="color:#909399; font-size: 16px;">(公告)</span>
            <span v-else style="color:#f56c6c; font-weight:bold;">¥{{ scope.row.idlePrice }}</span>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="100" align="center">
          <template slot-scope="scope"><el-tag size="big" type="info">{{ getCategoryName(scope.row.idleLabel) }}</el-tag></template>
        </el-table-column>

        <el-table-column label="资源文件" min-width="150" align="center">
          <template slot-scope="scope">
            <div v-if="scope.row.resourcePath">
              <div class="wrap-text" style="font-size: 16px;">{{ scope.row.originalFileName }}</div>
              <el-button type="text" icon="el-icon-download" size="big" @click="downloadFile(scope.row)">下载</el-button>
            </div>
            <span v-else style="color:#ccc; font-size: 16px;">无资源</span>
          </template>
        </el-table-column>

        <el-table-column label="解压密码" width="120" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.unzipPassword" style="color:#F56C6C; font-weight:bold;">{{ scope.row.unzipPassword }}</span>
            <span v-else style="color:#ccc;">无</span>
          </template>
        </el-table-column>

        <el-table-column label="版权证明" width="100" align="center">
          <template slot-scope="scope">
            <el-image v-if="scope.row.proofImage" style="width: 50px; height: 50px; cursor: pointer; border-radius: 4px;" :src="scope.row.proofImage" :preview-src-list="[scope.row.proofImage]">
              <div slot="error"><i class="el-icon-picture-outline"></i></div>
            </el-image>
            <span v-else style="color:#999; font-size: 16px;">未上传</span>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="170" align="center">
          <template slot-scope="scope">
            <div style="font-size: 16px; line-height: 1.5;">
              <div style="color:#606266">提交: {{ formatTime(scope.row.releaseTime) }}</div>
              <div v-if="scope.row.auditTime" style="color: #67C23A;">审核: {{ formatTime(scope.row.auditTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="action-row">
              <el-button size="mini" type="primary" icon="el-icon-time" @click="openHistory(scope.row)" plain>历史</el-button>
              <template v-if="activeTab === '3'">
                <el-button size="mini" type="success" icon="el-icon-check" @click="passAudit(scope.row)" plain>通过</el-button>
                <el-button size="mini" type="danger" icon="el-icon-close" @click="rejectDialog(scope.row)" plain>驳回</el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="审核驳回" :visible.sync="rejectVisible" width="400px" center>
      <el-input type="textarea" :rows="4" placeholder="请输入驳回理由（如：存在侵权行为、商品信息不完全等）" v-model="rejectReason"></el-input>
      <span slot="footer" class="dialog-footer">
                <el-button @click="rejectVisible = false">取消</el-button>
                <el-button type="danger" @click="confirmReject">确认驳回</el-button>
            </span>
    </el-dialog>

    <el-dialog title="商品修改历史" :visible.sync="historyVisible" width="750px" center>
      <div v-if="historyList.length > 0" class="history-timeline">
        <el-timeline>
          <el-timeline-item
              v-for="(hist, index) in historyList"
              :key="index"
              :timestamp="formatTime(hist.createTime)"
              placement="top"
              color="#409EFF">
            <el-card class="history-card">
              <div slot="header" class="history-header">
                <span>修改前版本 (v{{ historyList.length - index }})</span>
                <div style="display:flex; align-items:center">
                  <span style="font-weight:normal; font-size:16px; color:#666; margin-right:10px;" v-if="historyUser">
                                        <i class="el-icon-user"></i> {{ historyUser.nickname }}
                                    </span>
                  <el-tag size="mini" :type="getStatusType(hist.idleStatus)">{{ getStatusText(hist.idleStatus) }}</el-tag>
                </div>
              </div>
              <div class="history-content">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="item"><span class="label">名称:</span> {{ hist.idleName }}</div>
                    <div class="item"><span class="label">价格:</span> <span style="color:#f56c6c;font-weight:bold;">¥{{ hist.idlePrice }}</span></div>
                    <div class="item"><span class="label">分类:</span> {{ getCategoryName(hist.idleLabel) }}</div>
                  </el-col>
                  <el-col :span="12">
                    <div class="item"><span class="label">发货说明:</span> {{ hist.idlePlace }}</div>
                    <div class="item" v-if="hist.originalFileName">
                      <span class="label">资源名称:</span> {{ hist.originalFileName }}
                    </div>
                    <div class="item" v-if="hist.unzipPassword">
                      <span class="label">解压密码:</span> <span style="color:#F56C6C">{{ hist.unzipPassword }}</span>
                    </div>
                  </el-col>
                </el-row>

                <div class="item" style="margin-top:10px;">
                  <div class="label">详情描述:</div>
                  <div class="detail-text">{{ hist.idleDetails }}</div>
                </div>

                <el-divider content-position="left"><span style="font-size:12px;color:#999">图片信息</span></el-divider>
                <div class="img-row">
                  <div class="img-group">
                    <span class="label">预览图:</span>
                    <div v-if="hist.pictureList && getImgList(hist.pictureList).length > 0">
                      <el-image v-for="(img, i) in getImgList(hist.pictureList)" :key="i" style="width: 50px; height: 50px; margin-right: 5px; border-radius: 4px;" :src="img" :preview-src-list="getImgList(hist.pictureList)"></el-image>
                    </div>
                    <span v-else style="font-size:14px;color:#999">无</span>
                  </div>
                  <div class="img-group">
                    <span class="label">版权证图:</span>
                    <el-image v-if="hist.proofImage" style="width: 50px; height: 50px; border-radius: 4px;" :src="hist.proofImage" :preview-src-list="[hist.proofImage]"></el-image>
                    <span v-else style="font-size:14px;color:#999">无</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
      <div v-else class="empty-history">
        <i class="el-icon-info"></i> 该商品暂无修改历史记录
      </div>
    </el-dialog>

    <div class="pagination-container">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="nowPage" :page-size="8" layout="total, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>
  </div>
</template>

<script>
import { getCategoryLabel } from '@/utils/categoryConfig';

export default {
  name: "AuditList",
  data() {
    return {
      activeTab: '3',
      list: [],
      loading: false,
      nowPage: 1,
      total: 0,
      searchValue: '',

      rejectVisible: false,
      rejectReason: '',
      currentRow: null,

      historyVisible: false,
      historyList: [],
      historyUser: null,
    }
  },
  created() {
    this.getData();
  },
  methods: {
    formatTime(time){ return time ? time.replace("T", " ").substring(2, 16) : ''; },
    getCategoryName(label) {
      return getCategoryLabel(label);
    },

    initImgList(str) { try { let arr = JSON.parse(str); return Array.isArray(arr) ? arr : []; } catch(e){ return []; } },
    getImgList(str) { try { return JSON.parse(str); } catch(e){ return []; } },

    prevImg(row) { row.curImgIdx > 0 ? row.curImgIdx-- : row.curImgIdx = row.imgList.length - 1; },
    nextImg(row) { row.curImgIdx < row.imgList.length - 1 ? row.curImgIdx++ : row.curImgIdx = 0; },

    handleCurrentChange(val) { this.nowPage = val; this.getData(); },
    handleTabClick() { this.nowPage = 1; this.searchValue = ''; this.getData(); },

    downloadFile(row) {
      const url = `http://localhost:8080/file/download?fileName=${row.resourcePath}`;
      window.open(url, '_blank');
    },

    getData() {
      this.loading = true;
      this.$api.getAuditList({
        page: this.nowPage,
        nums: 8,
        status: this.activeTab,
        searchValue: this.searchValue
      }).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          let rawList = res.data.list;
          rawList.forEach(item => {
            item.imgList = this.initImgList(item.pictureList);
            item.curImgIdx = 0;
          });
          this.list = rawList;
          this.total = res.data.count;
        }
      });
    },

    openHistory(row) {
      this.historyVisible = true;
      this.historyList = [];

      const loading = this.$loading({ target: '.el-dialog', text: '正在调取云端历史快照...' });

      this.$api.getAdminIdleHistory({ idleId: row.id }).then(res => {
        loading.close();
        if(res.status_code === 1) {
          this.historyList = res.data;
        } else {
          this.$message.error(res.msg || '历史记录调取失败');
        }
      }).catch(() => {
        loading.close();
        this.$message.error('网络异常，无法连接后台历史库');
      });
    },

    getStatusType(status) {
      if(status === 1) return 'success';
      if(status === 2) return 'info';
      if(status === 3) return 'warning';
      if(status === 4) return 'danger';
      return '';
    },
    getStatusText(status) {
      const map = {1: '上线', 2: '下架', 3: '待审', 4: '驳回', 0: '删除'};
      return map[status] || '未知';
    },

    passAudit(row) {
      this.$confirm('确认通过该商品的审核吗？', '提示', { type: 'success' }).then(() => {
        this.$api.auditItem({ id: row.id, status: 1 }).then(res => {
          if(res.status_code === 1) {
            this.$message.success('已通过');
            this.getData();
            this.$emit('refresh-badge');
          }
        });
      });
    },

    rejectDialog(row) {
      this.currentRow = row;
      this.rejectReason = '';
      this.rejectVisible = true;
    },

    confirmReject() {
      if(!this.rejectReason.trim()) {
        this.$message.warning('请填写驳回理由');
        return;
      }
      this.$api.auditItem({
        id: this.currentRow.id,
        status: 4,
        reason: this.rejectReason
      }).then(res => {
        if(res.status_code === 1) {
          this.$message.success('已驳回');
          this.rejectVisible = false;
          this.getData();
          this.$emit('refresh-badge');
        }
      });
    }
  }
}
</script>

<style scoped>
.main-border { background-color: #fff; margin: 20px; padding: 25px; box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08); border-radius: 10px; height: auto; min-height: calc(100vh - 140px); display: flex; flex-direction: column; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 1px solid #ebeef5; padding-bottom: 15px; }
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

.user-info-wrapper { display: flex; align-items: center; justify-content: center; }
.user-avatar { border: 1px solid #eee; margin-right: 10px; flex-shrink: 0; }
.user-nickname { font-weight: 400; color: #303133; font-size: 16px; }
.wrap-text { white-space: normal; word-break: break-all; line-height: 1.5; }
.img-switcher { position: relative; display: flex; align-items: center; justify-content: center; width: 100%; height: 80px; }
.arrow-btn { cursor: pointer; font-weight: bold; color: #909399; transition: all 0.3s; padding: 0 5px; font-size: 16px; user-select: none; }
.arrow-btn:hover { color: #409EFF; transform: scale(1.2); }
.img-indicator { position: absolute; bottom: -18px; left: 50%; transform: translateX(-50%); font-size: 12px; color: #909399; background: rgba(255,255,255,0.8); padding: 0 5px; border-radius: 4px; white-space: nowrap; }

.action-row { display: flex; flex-direction: column; align-items: center; justify-content: center; }
.action-row .el-button { margin-left: 0 !important; margin-bottom: 3px; width: 70px; padding: 7px 0; text-align: center; }
.action-row .el-button:last-child { margin-bottom: 0; }

.history-timeline { max-height: 400px; overflow-y: auto; padding: 10px; }
.history-card { border: 1px solid #eee; box-shadow: none; }
.history-header { display: flex; justify-content: space-between; align-items: center; font-weight: bold; color: #606266; }
.history-content { font-size: 14px; }
.history-content .item { margin-bottom: 8px; color: #333; display: flex; align-items: center; }
.history-content .label { font-weight: 600; margin-right: 8px; color: #909399; min-width: 60px; display: inline-block; }
.detail-text { background: #f9f9f9; padding: 8px; border-radius: 4px; font-size: 13px; color: #555; margin-top: 5px; line-height: 1.5; }
.img-row { display: flex; gap: 30px; margin-top: 10px; }
.img-group { display: flex; align-items: center; }
.empty-history { text-align: center; padding: 40px; color: #909399; }
</style>