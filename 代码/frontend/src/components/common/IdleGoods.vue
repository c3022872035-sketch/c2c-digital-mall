<template>
  <div class="main-border">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <div class="header-actions">
        <div class="search-container">
          <el-input
              placeholder="搜索ID / 商品名称 / 用户昵称"
              v-model="findValue"
              @keyup.enter.native="searchIdle"
              prefix-icon="el-icon-search"
              clearable
              class="search-input">
            <el-button slot="append" icon="el-icon-search" @click="searchIdle">搜索</el-button>
          </el-input>
        </div>
        <el-button type="primary" icon="el-icon-plus" @click="openAddDialog" style="margin-left: 15px;">新增商品</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick" type="card" class="custom-tabs">
      <!-- Tab 1: 上线的商品 -->
      <el-tab-pane label="上线的商品" name="1">
        <div class="table-container">
          <el-table
              :data="onlineGoods"
              stripe
              border
              style="width: 100%"
              :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '16px', height: '60px' }"
              :cell-style="{ fontSize: '16px', color: '#303133', padding: '15px 0' }"
              v-loading="tableLoading">

            <el-table-column label="ID" width="80" align="center" prop="id"></el-table-column>

            <el-table-column label="预览图" width="100" align="center">
              <template slot-scope="scope">
                <el-image
                    style="width: 60px; height: 60px; border-radius: 4px;"
                    :src="getFirstImg(scope.row.pictureList)"
                    :preview-src-list="getImgList(scope.row.pictureList)"
                    fit="cover">
                  <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                </el-image>
              </template>
            </el-table-column>

            <el-table-column prop="idleName" label="商品名称" min-width="150" align="center">
              <template slot-scope="scope">
                <div class="wrap-text">{{ scope.row.idleName }}</div>
              </template>
            </el-table-column>

            <el-table-column label="分类" width="100" align="center">
              <template slot-scope="scope">
                <el-tag size="medium" type="info">{{ getCategoryName(scope.row.idleLabel) }}</el-tag>
              </template>
            </el-table-column>

            <el-table-column label="销量" width="100" align="center" prop="salesCount">
              <template slot-scope="scope">{{ scope.row.salesCount || 0 }}</template>
            </el-table-column>

            <el-table-column label="评分" width="80" align="center">
              <template slot-scope="scope">
                                <span v-if="scope.row.avgRating > 0" style="color: #ff9900; font-weight: bold;">
                                    <i class="el-icon-star-on"></i> {{ scope.row.avgRating }}
                                </span>
                <span v-else style="color: #ccc;">暂无</span>
              </template>
            </el-table-column>

            <el-table-column label="发布用户" width="180" align="center">
              <template slot-scope="scope">
                <div class="user-info-wrapper">
                  <el-avatar :size="45" :src="scope.row.user.avatar" class="user-avatar"></el-avatar>
                  <span class="user-nickname">{{ scope.row.user.nickname }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="价格" width="110" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.idleLabel === 10" style="color: #909399; font-size: 14px;">(公告)</span>
                <span v-else class="price-tag">¥{{ scope.row.idlePrice }}</span>
              </template>
            </el-table-column>

            <el-table-column label="发布日期" width="170" align="center">
              <template slot-scope="scope">
                {{ scope.row.releaseTime ? scope.row.releaseTime.substring(0,19).replace('T',' ') : '' }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="scope">
                <div class="action-row">
                  <el-button size="small" type="success" icon="el-icon-edit" @click="openEditDialog(scope.row)" plain>修改</el-button>
                  <el-button size="small" type="danger" icon="el-icon-download" @click="confirmOfflineGoods(scope.$index, scope.row)" plain>下架</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 下架的商品 -->
      <el-tab-pane label="下架的商品" name="2">
        <div class="table-container">
          <el-table
              :data="OfflineGoods"
              stripe
              border
              style="width: 100%"
              :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 'bold', fontSize: '16px', height: '60px' }"
              :cell-style="{ fontSize: '16px', color: '#303133', padding: '15px 0' }"
              v-loading="tableLoading">

            <el-table-column label="ID" width="80" align="center" prop="id"></el-table-column>

            <el-table-column label="预览图" width="100" align="center">
              <template slot-scope="scope">
                <el-image
                    style="width: 60px; height: 60px; border-radius: 4px;"
                    :src="getFirstImg(scope.row.pictureList)"
                    :preview-src-list="getImgList(scope.row.pictureList)"
                    fit="cover">
                  <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                </el-image>
              </template>
            </el-table-column>

            <el-table-column prop="idleName" label="商品名称" min-width="150" align="center">
              <template slot-scope="scope">
                <div class="wrap-text">{{ scope.row.idleName }}</div>
              </template>
            </el-table-column>

            <el-table-column label="分类" width="100" align="center">
              <template slot-scope="scope">
                <el-tag size="medium" type="info">{{ getCategoryName(scope.row.idleLabel) }}</el-tag>
              </template>
            </el-table-column>

            <el-table-column label="销量" width="100" align="center" prop="salesCount">
              <template slot-scope="scope">{{ scope.row.salesCount || 0 }}</template>
            </el-table-column>

            <el-table-column label="评分" width="80" align="center">
              <template slot-scope="scope">
                                <span v-if="scope.row.avgRating > 0" style="color: #ff9900; font-weight: bold;">
                                    <i class="el-icon-star-on"></i> {{ scope.row.avgRating }}
                                </span>
                <span v-else style="color: #ccc;">暂无</span>
              </template>
            </el-table-column>

            <el-table-column label="发布用户" width="180" align="center">
              <template slot-scope="scope">
                <div class="user-info-wrapper">
                  <el-avatar :size="45" :src="scope.row.user.avatar" class="user-avatar"></el-avatar>
                  <span class="user-nickname">{{ scope.row.user.nickname }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="价格" width="110" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.idleLabel === 10" style="color: #909399; font-size: 14px;">(公告)</span>
                <span v-else class="price-tag">¥{{ scope.row.idlePrice }}</span>
              </template>
            </el-table-column>

            <el-table-column label="发布日期" width="170" align="center">
              <template slot-scope="scope">
                {{ scope.row.releaseTime ? scope.row.releaseTime.substring(0,19).replace('T',' ') : '' }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="scope">
                <div class="action-row">
                  <el-button size="small" type="success" icon="el-icon-edit" @click="openEditDialog(scope.row)" plain>修改</el-button>
                  <el-button size="small" type="danger" icon="el-icon-delete" @click="confirmDeleteGoods(scope.$index, scope.row)" plain>删除</el-button>
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

    <!-- 新增/修改商品 对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" center>
      <el-form :model="goodsForm" label-width="100px">
        <el-form-item label="商品标题">
          <el-input v-model="goodsForm.idleName" placeholder="请输入商品标题"></el-input>
        </el-form-item>

        <!-- 选择发布用户（仅新增时显示） -->
        <el-form-item label="发布用户" v-if="dialogTitle === '新增商品'">
          <el-select
              v-model="goodsForm.userId"
              filterable
              remote
              clearable
              placeholder="请输入用户昵称或账号搜索 (留空则为官方发布)"
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

        <el-form-item label="商品详情">
          <el-input type="textarea" :rows="3" v-model="goodsForm.idleDetails" placeholder="请输入详情描述"></el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" v-if="goodsForm.idleLabel !== 10">
              <el-input-number v-model="goodsForm.idlePrice" :precision="2" :step="1" :min="0" style="width:100%"></el-input-number>
            </el-form-item>
            <div v-else style="padding: 40px 0 0 20px; color: #909399; font-size: 13px; line-height: 1;">
              <i class="el-icon-info"></i> 公告类型无需设置价格
            </div>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类">
              <el-select v-model="goodsForm.idleLabel" placeholder="请选择分类" style="width:100%">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品说明">
          <el-input v-model="goodsForm.idlePlace" placeholder="请输入商品说明"></el-input>
        </el-form-item>

        <!-- 平台托管资源上传 -->
        <el-card class="form-section" shadow="never" v-if="goodsForm.idleLabel !== 10" style="margin-bottom: 15px; border: 1px dashed #eee;">
          <div slot="header" style="padding: 5px 0; font-size: 13px; font-weight: bold;">
            <i class="el-icon-folder-opened"></i> 数字资源文件 (平台托管)
          </div>

          <el-form-item label="上传文件">
            <el-upload
                class="upload-demo"
                action="#"
                :http-request="customUpload"
                :limit="1"
                :on-remove="handleRemoveResource"
                :file-list="resourceFileList"
                accept=".zip,.rar">
              <el-button size="small" type="primary" icon="el-icon-upload">点击上传压缩包</el-button>
              <div slot="tip" class="el-upload__tip">买家付款后直接下载。支持.zip格式。</div>
            </el-upload>
          </el-form-item>

          <!-- 文件回显 -->
          <div v-if="goodsForm.originalFileName" style="font-size: 12px; color: #666; padding-left: 100px; margin-bottom: 10px;">
            当前文件：<span style="color:#409EFF">{{ goodsForm.originalFileName }}</span>
          </div>

          <!-- 解压密码 -->
          <el-form-item label="解压密码">
            <el-input v-model="goodsForm.unzipPassword" placeholder="可选，若压缩包有密码请填写" prefix-icon="el-icon-key"></el-input>
          </el-form-item>
        </el-card>

        <!-- 商品图片 -->
        <el-form-item label="预览图">
          <el-upload
              action="http://localhost:8080/file/"
              list-type="picture-card"
              :on-success="handleImageSuccess"
              :on-remove="handleImageRemove"
              :file-list="fileList"
              :limit="5">
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>

        <!-- 版权证明 -->
        <el-form-item label="版权证明">
          <el-upload
              action="http://localhost:8080/file/"
              :on-success="handleProofSuccess"
              :limit="1"
              list-type="picture"
              :file-list="proofFileList"
              accept="image/*">
            <el-button size="small" type="primary" icon="el-icon-upload2">上传/修改证明图</el-button>
          </el-upload>
          <!-- 回显预览 -->
          <div v-if="goodsForm.proofImage" style="margin-top: 5px;">
            <el-image
                style="width: 60px; height: 60px; border-radius: 4px;"
                :src="goodsForm.proofImage"
                :preview-src-list="[goodsForm.proofImage]">
            </el-image>
          </div>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitGoods">确 定</el-button>
            </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { categories, getCategoryLabel } from '@/utils/categoryConfig';

export default {
  name: "IdleGoods",
  data() {
    return {
      activeTab: "1",
      mode: 1,
      nowPage: 1,
      total: 0,
      onlineGoods: [],
      OfflineGoods: [],
      findValue: '',
      status: 1,
      tableLoading: false,

      // Dialog相关数据
      dialogVisible: false,
      dialogTitle: '新增商品',
      goodsForm: {
        id: '',
        userId: '',
        idleName: '',
        idleDetails: '',
        idlePrice: 0,
        idleLabel: '',
        idlePlace: '',
        pictureList: '',
        resourcePath: '',
        originalFileName: '',
        unzipPassword: '',
        proofImage: ''
      },
      fileList: [],
      imgList: [],
      proofFileList: [],
      resourceFileList: [],

      // 用户搜索相关
      selectLoading: false,
      userOptions: [],

      categoryOptions: categories,
    }
  },
  created() {
    this.getOnlineGoods();
  },
  methods: {
    // 自定义上传资源
    customUpload(param) {
      const file = param.file;
      const isZipOrRar = file.name.endsWith('.zip');
      if (!isZipOrRar) {
        this.$message.error('仅支持.zip格式的压缩包!');
        this.resourceFileList = [];
        return;
      }
      const formData = new FormData();
      formData.append('file', file);

      axios.post('http://localhost:8080/file/uploadResource', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        withCredentials: true
      }).then(res => {
        if(res.data.status_code === 1) {
          this.goodsForm.resourcePath = res.data.data;
          this.goodsForm.originalFileName = file.name;
          this.$message.success('资源文件上传成功');
        } else {
          this.$message.error('上传失败');
          this.resourceFileList = [];
        }
      }).catch(err => {
        this.$message.error('网络异常，上传失败');
        this.resourceFileList = [];
      });
    },
    handleRemoveResource(file, fileList) {
      this.goodsForm.resourcePath = '';
      this.goodsForm.originalFileName = '';
      this.$message.info('已移除资源文件');
    },

    getFirstImg(str) {
      try {
        let arr = JSON.parse(str);
        return Array.isArray(arr) && arr.length > 0 ? arr[0] : '';
      } catch(e) { return ''; }
    },
    getImgList(str) {
      try {
        let arr = JSON.parse(str);
        return Array.isArray(arr) ? arr : [];
      } catch(e) { return []; }
    },
    getCategoryName(label) {
      return getCategoryLabel(label); // 使用全局工具函数
    },

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

    openAddDialog() {
      this.dialogTitle = '新增商品';
      this.goodsForm = {
        userId: '',
        idleName: '',
        idleDetails: '',
        idlePrice: 0,
        idleLabel: '',
        idlePlace: '',
        pictureList: '',
        resourcePath: '',
        originalFileName: '',
        unzipPassword: '',
        proofImage: ''
      };
      this.fileList = [];
      this.imgList = [];
      this.proofFileList = [];
      this.resourceFileList = [];
      this.userOptions = [];
      this.dialogVisible = true;
    },

    openEditDialog(row) {
      this.dialogTitle = '修改商品';
      this.goodsForm = {
        id: row.id,
        idleName: row.idleName,
        idleDetails: row.idleDetails,
        idlePrice: row.idlePrice,
        idleLabel: row.idleLabel,
        idlePlace: row.idlePlace,
        userId: row.userId,
        resourcePath: row.resourcePath || '',
        originalFileName: row.originalFileName || '',
        unzipPassword: row.unzipPassword || '',
        proofImage: row.proofImage || ''
      };

      this.imgList = [];
      this.fileList = [];
      if(row.pictureList) {
        try {
          let list = JSON.parse(row.pictureList);
          this.imgList = list;
          this.fileList = list.map(url => ({ url: url }));
        } catch(e) { console.error(e); }
      }

      this.proofFileList = [];
      this.resourceFileList = []; // 文件上传控件不支持直接回显，但我们在表单中显示了 originalFileName

      this.dialogVisible = true;
    },

    handleImageSuccess(res, file) {
      if(res.status_code === 1) {
        this.imgList.push(res.data);
      }
    },
    handleImageRemove(file) {
      let url = file.response ? file.response.data : file.url;
      this.imgList = this.imgList.filter(item => item !== url);
    },
    handleProofSuccess(res, file) {
      if(res.status_code === 1) {
        this.goodsForm.proofImage = res.data;
        this.$message.success('证明图上传成功');
      }
    },

    submitGoods() {
      if(!this.goodsForm.idleName || !this.goodsForm.idleLabel) {
        this.$message.warning('请填写完整信息');
        return;
      }

      this.goodsForm.pictureList = JSON.stringify(this.imgList);

      const successCallback = () => {
        this.dialogVisible = false;
        if(this.mode === 1) this.getOnlineGoods();
        else this.getOfflineGoods();
      };

      if (this.dialogTitle === '新增商品') {
        this.$api.addAdminIdle(this.goodsForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('发布成功');
            successCallback();
          } else {
            this.$message.error(res.msg);
          }
        });
      } else {
        this.$api.updateAdminIdle(this.goodsForm).then(res => {
          if(res.status_code === 1) {
            this.$message.success('修改成功');
            successCallback();
          } else {
            this.$message.error(res.msg);
          }
        });
      }
    },

    handleTabClick(tab) {
      this.mode = parseInt(tab.name);
      this.status = parseInt(tab.name);
      this.nowPage = 1;
      if (this.mode === 1) this.getOnlineGoods();
      else this.getOfflineGoods();
    },
    searchIdle(){
      this.tableLoading = true;
      this.$api.queryIdle({
        findValue: this.findValue, page: this.nowPage, nums: 8, status: this.status
      }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1 && res.data.list != null) {
          if(res.data.list[0].idleStatus === 1){
            this.onlineGoods = res.data.list;
            this.total = res.data.count;
          } else {
            this.OfflineGoods = res.data.list;
            this.total = res.data.count;
          }
        } else{ this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    handleCurrentChange(val) {
      this.nowPage = val;
      if(this.findValue) {
        this.searchIdle();
      } else {
        if (this.mode === 1) this.getOnlineGoods();
        if (this.mode === 2) this.getOfflineGoods();
      }
    },
    confirmOfflineGoods(index, row) {
      this.$confirm('确定要将该闲置商品下架吗？', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => { this.makeOfflineGoods(index); }).catch(() => {});
    },
    confirmDeleteGoods(index, row) {
      this.$confirm('此操作将永久删除该商品, 是否继续?', '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => { this.deleteGoods(index); }).catch(() => {});
    },
    makeOfflineGoods(i) {
      this.tableLoading = true;
      this.$api.updateGoods({ id: this.onlineGoods[i].id, status: 2 }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1) {
          this.$message.success('商品已成功下架');
          this.getOnlineGoods();
        } else { this.$message.error(res.msg) }
      });
    },
    deleteGoods(i) {
      this.tableLoading = true;
      this.$api.updateGoods({ id: this.OfflineGoods[i].id, status: 0 }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1) {
          this.$message.success('商品已永久删除');
          this.getOfflineGoods();
        } else { this.$message.error(res.msg) }
      });
    },
    getOnlineGoods() {
      this.tableLoading = true;
      this.$api.queryIdle({ findValue: this.findValue, page: this.nowPage, nums: 8, status: 1 }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1) {
          this.onlineGoods = res.data.list;
          this.total = res.data.count;
        } else { this.$message.error(res.msg) }
      }).catch(e => { this.tableLoading = false; console.log(e) })
    },
    getOfflineGoods() {
      this.tableLoading = true;
      this.$api.queryIdle({ findValue: this.findValue, page: this.nowPage, nums: 8, status: 2 }).then(res => {
        this.tableLoading = false;
        if (res.status_code === 1) {
          this.OfflineGoods = res.data.list;
          this.total = res.data.count;
        } else { this.$message.error(res.msg) }
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

.user-info-wrapper { display: flex; align-items: center; justify-content: center; }
.user-avatar { border: 1px solid #eee; margin-right: 10px; flex-shrink: 0; }
.user-nickname { font-weight: 400; color: #303133; font-size: 16px; }

.price-tag { color: #ff6b6b; font-weight: bold; font-size: 16px; }
.big-text { font-size: 20px; color: #303133; }
.image-slot { display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }

.action-row {display: flex;flex-direction: column;align-items: center;justify-content: center;}
.action-row .el-button {margin-left: 0 !important;margin-bottom: 3px;width: 70px;padding: 7px 0;text-align: center;}
.action-row .el-button:last-child {margin-bottom: 0;}

.pagination-container { margin-top: 20px; display: flex; justify-content: center; }

@media screen and (max-width: 768px) {
  .page-header { flex-direction: column; align-items: flex-start; }
  .header-actions { width: 100%; flex-direction: column; margin-top: 10px; }
  .search-container { width: 100%; }
  .header-actions .el-button { width: 100%; margin-left: 0 !important; margin-top: 10px; }
  .main-border { padding: 15px; margin: 10px; }
}
</style>