<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="release-page-container">
        <el-card class="release-card" shadow="hover">
          <div slot="header" class="release-header">
            <h2 class="release-title">
              <i class="el-icon-upload2"></i> 发布商品/公告
            </h2>
            <p class="release-subtitle">填写商品信息，并通过版权审核后即可上架</p>
          </div>

          <el-form label-position="top" :model="idleItemInfo" class="release-form">

            <!-- 1. 基本信息 -->
            <el-card class="form-section" shadow="never">
              <div slot="header" class="section-header">
                <i class="el-icon-document"></i> 基本信息
              </div>
              <el-form-item label="商品名称">
                <el-input
                    placeholder="请输入商品/公告名称（简洁醒目更容易吸引买家）"
                    v-model="idleItemInfo.idleName"
                    maxlength="30"
                    prefix-icon="el-icon-edit"
                    show-word-limit>
                </el-input>
              </el-form-item>
              <el-form-item label="商品描述">
                <el-input
                    class="release-idle-details"
                    type="textarea"
                    :rows="4"
                    placeholder="请详细描述您的商品..."
                    v-model="idleItemInfo.idleDetails"
                    maxlength="1000"
                    show-word-limit>
                </el-input>
              </el-form-item>
            </el-card>

            <!-- 2. 分类与价格 -->
            <el-card class="form-section" shadow="never">
              <div slot="header" class="section-header">
                <i class="el-icon-s-grid"></i> 分类与价格
              </div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="商品类别">
                    <el-select
                        v-model="idleItemInfo.idleLabel"
                        placeholder="请选择类别"
                        style="width: 100%"
                        class="category-select">

                      <i slot="prefix"
                         :class="getCategoryIcon(idleItemInfo.idleLabel)"
                         v-if="idleItemInfo.idleLabel"
                         style="line-height: 40px; margin-left: 5px; color: #409EFF;">
                      </i>

                      <el-option
                          v-for="item in options2"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value">
                        <span class="category-icon">
                          <i :class="getCategoryIcon(item.value)"></i>
                        </span>
                        <span>{{ item.label }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>

                <el-col :span="12">
                  <el-form-item label="商品价格" v-if="idleItemInfo.idleLabel !== 10">
                    <el-input-number
                        v-model="idleItemInfo.idlePrice"
                        :precision="2"
                        :step="10"
                        :min="0"
                        :max="10000000"
                        style="width: 100%"
                        class="price-input">
                      <template slot="prepend">
                        <i class="el-icon-price-tag"></i> ¥
                      </template>
                    </el-input-number>
                  </el-form-item>
                  <div v-else class="notice-hint">
                    <i class="el-icon-info"></i> 公告类型无需设置价格
                  </div>
                </el-col>
              </el-row>

              <el-form-item label="商品说明">
                <el-input
                    v-model="idleItemInfo.idlePlace"
                    placeholder="填写商品说明，可填写'仅供学习参考，严禁商用' 或 '有问题站内私信'"
                    maxlength="50"
                    show-word-limit>
                </el-input>
              </el-form-item>
            </el-card>

            <!-- 3. 数字资源上传 (含解压密码) -->
            <el-card class="form-section" shadow="never" v-if="idleItemInfo.idleLabel !== 10">
              <div slot="header" class="section-header">
                <i class="el-icon-folder-opened"></i> 资源上传 (平台托管)
                <span class="upload-tip">（买家付款后可直接在订单页下载）</span>
              </div>

              <el-form-item label="上传资源文件 (仅支持 .zip格式)">
                <el-upload
                    class="upload-demo"
                    action="#"
                    :http-request="customUpload"
                    :limit="1"
                    :on-remove="handleRemoveResource"
                    :file-list="resourceFileList"
                    accept=".zip">
                  <el-button size="small" type="primary" icon="el-icon-upload">点击上传压缩包</el-button>
                  <div slot="tip" class="el-upload__tip"><strong>仅支持 .zip 格式</strong>。请将资源打包为 ZIP 后上传。</div>
                </el-upload>
              </el-form-item>

              <!-- 解压密码输入框 -->
              <el-form-item label="解压密码 (选填)">
                <el-input
                    v-model="idleItemInfo.unzipPassword"
                    placeholder="如果压缩包设置了密码，请在此填写"
                    prefix-icon="el-icon-key"
                    show-password>
                </el-input>
              </el-form-item>
            </el-card>

            <!-- 4. 商品图片上传 (含百度AI版权检测) -->
            <el-card class="form-section" shadow="never">
              <div slot="header" class="section-header">
                <i class="el-icon-picture-outline"></i> 预览图
                <span class="upload-tip">（系统将自动进行全网版权比对，请勿上传盗版图片）</span>
              </div>
              <el-upload
                  action="http://localhost:8080/file/"
                  :on-preview="fileHandlePreview"
                  :on-remove="fileHandleRemove"
                  :on-success="fileHandleSuccess"
                  :show-file-list="showFileList"
                  :limit="10"
                  :on-exceed="handleExceed"
                  accept="image/*"
                  drag
                  multiple
                  class="upload-area">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">拖拽图片到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip" slot="tip">支持JPG/PNG等格式，单张不超过5MB</div>
              </el-upload>

              <el-dialog :visible.sync="imgDialogVisible">
                <img width="100%" :src="dialogImageUrl" alt="">
              </el-dialog>
            </el-card>

            <!-- 5. 版权证明与承诺 -->
            <el-card class="form-section" shadow="never">
              <div slot="header" class="section-header">
                <i class="el-icon-s-check"></i> 版权与认证
              </div>

              <el-form-item label="版权/原创证明图（选填，建议上传）">
                <div class="upload-tip-text">请上传源文件编辑截图、设计分层图、或第三方平台后台截图，以证明资源合法性。上传证明可提高审核通过率。</div>
                <el-upload
                    action="http://localhost:8080/file/"
                    :on-success="proofSuccess"
                    :limit="1"
                    list-type="picture"
                    accept="image/*">
                  <el-button size="small" type="primary" icon="el-icon-upload2">点击上传证明图</el-button>
                  <div slot="tip" class="el-upload__tip">仅限一张，管理员审核可见</div>
                </el-upload>
              </el-form-item>

              <!-- 承诺勾选区域 -->
              <div class="promise-box">
                <el-form-item class="promise-item">
                  <el-checkbox v-model="copyrightPromise">
                    <span class="promise-text">我承诺该资源为原创或拥有合法授权，未侵犯第三方权益。</span>
                  </el-checkbox>
                </el-form-item>

                <el-form-item class="promise-item">
                  <el-checkbox v-model="protocolPromise" @change="handleProtocolCheck">
                    <span class="promise-text">我承诺遵守平台规定的</span>
                    <span class="protocol-link" @click.stop="openProtocolDialog">《数字商品版权保护协议》</span>
                  </el-checkbox>
                </el-form-item>
              </div>
            </el-card>

            <!-- 提交区域 -->
            <div class="submit-section">
              <el-button
                  type="primary"
                  round
                  :icon="idleItemInfo.idleLabel === 10 ? 'el-icon-s-flag' : 'el-icon-s-claim'"
                  @click="releaseButton"
                  :loading="submitting"
                  class="submit-button">
                提交并审核
              </el-button>
              <el-button
                  round
                  icon="el-icon-refresh-left"
                  @click="resetForm"
                  class="reset-button">
                重置表单
              </el-button>
            </div>
          </el-form>
        </el-card>

        <!-- 版权协议弹窗 -->
        <el-dialog
            title="数字商品版权保护协议"
            :visible.sync="protocolVisible"
            width="600px"
            center
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            :show-close="false">
          <div class="protocol-content" @scroll="handleScroll" ref="protocolContent">
            <h3>第一条：版权声明</h3>
            <p>1.1 用户在平台发布的任何数字商品（包括但不限于文档、图片、软件、代码等），必须保证拥有完整的知识产权或合法的转售授权。</p>
            <p>1.2 严禁上传侵犯第三方著作权、商标权、专利权等合法权益的内容。</p>
            <h3>第二条：违规处理</h3>
            <p>2.1 平台有权利用技术手段（如以图搜图、哈希比对）对商品进行版权检测。</p>
            <p>2.2 一经发现盗版、侵权行为，平台将立即下架相关商品，并视情节轻重对账号进行警告、封禁处理。</p>
            <p>2.3 若因用户上传侵权商品导致法律纠纷，一切法律责任由用户自行承担，平台不承担连带责任。</p>
            <h3>第三条：交易规范</h3>
            <p>3.1 严禁引导买家进行站外交易以规避平台监管。</p>
            <p>3.2 严禁在商品描述或文件中包含病毒、木马等恶意代码。</p>
            <p>3.3 对于“货不对板”、“虚假发货”等欺诈行为，平台将严厉打击并协助受害人维权。</p>
            <h3>第四条：信息存储</h3>
            <p>4.1 用户同意平台存储其上传的商品文件及证明材料，用于审核及后续可能的维权举证。</p>
            <p>4.2 平台承诺保护用户隐私，除法律规定或相关部门要求外，不向第三方透露用户信息。</p>
            <p style="margin-top: 50px; color: #999; text-align: center;">-- 已到底部 --</p>
          </div>
          <span slot="footer" class="dialog-footer">
                <div class="read-tip" v-if="!isReadFinished">请向下滑动阅读完完整协议</div>
                <el-button @click="protocolVisible = false">取 消</el-button>
                <el-button type="primary" :disabled="!isReadFinished" @click="confirmProtocol">我已阅读并同意</el-button>
            </span>
        </el-dialog>

      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue'
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'
import axios from 'axios'
import { categories } from '@/utils/categoryConfig';

export default {
  name: 'release',
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      imgDialogVisible: false,
      dialogImageUrl: '',
      showFileList: true,
      submitting: false,

      copyrightStatus: true, // 百度AI版权检测状态
      copyrightPromise: false, // 版权承诺勾选
      protocolPromise: false, // 协议勾选
      proofImage: '', // 证明图路径

      protocolVisible: false, // 协议弹窗
      isReadFinished: false, // 协议是否读完

      resourceFileList: [], // 资源文件列表（UI显示用）

      options2: categories, // 直接指向全局配置

      imgList: [], // 预览图列表
      idleItemInfo: {
        idleName: '',
        idleDetails: '',
        pictureList: '',
        idlePrice: 0,
        idleLabel: '',
        idlePlace: '',
        resourcePath: '',
        originalFileName: '',
        unzipPassword: '',
        fileHash: ''
      }
    }
  },
  methods: {
    // 自定义上传资源文件逻辑（处理查重和路径返回）
    customUpload(param) {
      const file = param.file;
      const isZip = file.name.toLowerCase().endsWith('.zip');

      if (!isZip) {
        this.$message.error('仅支持 .zip 格式的压缩包！');
        this.resourceFileList = [];
        return;
      }

      const formData = new FormData();
      formData.append('file', file);

      axios.post('http://localhost:8080/file/uploadResource', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        withCredentials: true
      }).then(res => {
        // 后端逻辑：如果哈希重复，status_code 为 0，msg 为“资源已存在...”
        if(res.data.status_code === 1) {
          this.idleItemInfo.resourcePath = res.data.data.fileName;
          this.idleItemInfo.fileHash = res.data.data.fileHash;
          this.idleItemInfo.originalFileName = file.name;
          this.$message.success('资源文件上传成功，已通过唯一性校验');
        } else {
          // 拦截提示：哈希冲突或上传失败
          this.$message.error(res.data.msg || '上传失败');
          this.resourceFileList = []; // 清空上传控件
          this.idleItemInfo.resourcePath = '';
          this.idleItemInfo.fileHash = '';
        }
      }).catch(err => {
        console.error(err);
        this.$message.error('网络异常，资源上传失败');
        this.resourceFileList = [];
      });
    },

    handleRemoveResource(file, fileList) {
      this.idleItemInfo.resourcePath = '';
      this.idleItemInfo.fileHash = '';
      this.idleItemInfo.originalFileName = '';
      this.$message.info('已移除待上传资源文件');
    },

    getCategoryIcon(categoryId) {
      const item = this.options2.find(c => c.value === categoryId);
      return item ? item.icon : '';
    },

    // 预览图相关
    fileHandleRemove(file) {
      const url = file.response ? file.response.data : file.url
      this.imgList = this.imgList.filter(i => i !== url)
      this.copyrightStatus = true; // 移除图片后重置版权状态
    },
    fileHandlePreview(file) {
      this.dialogImageUrl = file.response ? file.response.data : file.url
      this.imgDialogVisible = true
    },
    fileHandleSuccess(res) {
      if (res.status_code === 1) {
        this.imgList.push(res.data);
        // 图片上传成功后触发百度AI版权比对
        this.checkCopyright(res.data);
      } else {
        this.$message.error('预览图上传失败');
      }
    },

    // 证明图上传
    proofSuccess(res) {
      if(res.status_code === 1) {
        this.proofImage = res.data;
        this.$message.success('原创/版权证明图上传成功');
      }
    },

    // 百度AI版权比对工具
    checkCopyright(imgUrl) {
      const loading = this.$loading({
        lock: true,
        text: '正在进行全网版权比对...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      axios.get('http://localhost:8080/tool/check-copyright', {
        params: { imgUrl: imgUrl },
        withCredentials: true
      }).then(res => {
        loading.close();
        if (res.data.status_code === 1) {
          this.$message.success('图片内容合规性检测通过');
        } else {
          this.$message.error(res.data.msg || '检测到图片存在版权风险');
          this.copyrightStatus = false; // 标记存在风险，阻止提交
        }
      }).catch(err => {
        loading.close();
        console.error("版权检测服务异常", err);
      });
    },

    handleExceed() {
      this.$message.warning('最多支持上传 10 张预览图')
    },

    // 协议处理
    openProtocolDialog() {
      this.protocolVisible = true;
      this.isReadFinished = false;
      this.$nextTick(() => {
        if(this.$refs.protocolContent) {
          this.$refs.protocolContent.scrollTop = 0;
        }
      });
    },
    handleProtocolCheck(val) {
      if (val) {
        this.protocolPromise = false; // 强制弹窗，不允许直接勾选
        this.openProtocolDialog();
      }
    },
    handleScroll(e) {
      const { scrollTop, clientHeight, scrollHeight } = e.target;
      if (scrollTop + clientHeight >= scrollHeight - 10) {
        this.isReadFinished = true;
      }
    },
    confirmProtocol() {
      this.protocolPromise = true;
      this.protocolVisible = false;
      this.$message.success('您已同意版权保护协议');
    },

    // 最终提交按钮
    releaseButton() {
      if (!localStorage.getItem('token')) {
        this.$message.warning('登录已过期，请重新登录');
        this.$router.replace('/login');
        return;
      }

      // 表单校验
      if (!this.idleItemInfo.idleName) return this.$message.error('请输入商品标题！')
      if (!this.idleItemInfo.idleDetails) return this.$message.error('请输入详细描述！')
      if (!this.idleItemInfo.idleLabel) return this.$message.error('请选择商品类别！')
      if (this.idleItemInfo.idleLabel !== 10 && this.idleItemInfo.idlePrice <= 0) {
        return this.$message.error('请输入有效的销售价格！')
      }

      // 安全校验
      if (!this.copyrightStatus) {
        return this.$message.error('存在版权风险图片，请删除违规预览图后重试。');
      }
      if (!this.copyrightPromise) {
        return this.$message.error('请勾选原创/授权承诺！');
      }
      if (!this.protocolPromise) {
        return this.$message.error('请阅读并同意版权保护协议！');
      }

      this.submitting = true;
      this.idleItemInfo.pictureList = JSON.stringify(this.imgList);
      this.idleItemInfo.proofImage = this.proofImage;

      // 发送到后端 idle/add 接口
      this.$api.addIdleItem(this.idleItemInfo).then(res => {
        this.submitting = false;
        if (res.status_code === 1) {
          this.$message.success('发布成功，商品已进入审核队列！');
          this.$router.replace({ path: '/me' });
        } else {
          this.$message.error('提交失败：' + res.msg);
        }
      }).catch(() => {
        this.submitting = false;
        this.$message.error('服务器连接失败，请稍后重试');
      });
    },

    resetForm() {
      this.$confirm('确定要清空当前所有已填写内容吗？', '提示', { type: 'warning' })
          .then(() => {
            this.idleItemInfo = {
              idleName: '',
              idleDetails: '',
              pictureList: '',
              idlePrice: 0,
              idleLabel: '',
              idlePlace: '',
              resourcePath: '',
              originalFileName: '',
              unzipPassword: '',
              fileHash: '' // 清空哈希
            }
            this.imgList = [];
            this.resourceFileList = [];
            this.proofImage = '';
            this.copyrightStatus = true;
            this.copyrightPromise = false;
            this.protocolPromise = false;
            this.$message.success('表单已重置');
          })
          .catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 样式与之前保持一致 */
.release-page-container {
  min-height: 85vh;
  padding: 20px;
  background-color: #f5f7fa;
}

.release-card {
  max-width: 1000px;
  margin: 0 auto 40px;
  border-radius: 8px;
}

.release-header {
  text-align: center;
  padding-bottom: 10px;
}

.release-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.release-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.release-form {
  padding: 10px 0;
}

.form-section {
  margin-bottom: 20px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.section-header {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.release-idle-details {
  margin: 10px 0;
}

.category-select .category-icon {
  margin-right: 8px;
  color: #409EFF;
}

.price-input {
  width: 100%;
}

.notice-hint {
  padding: 20px 0;
  color: #909399;
  font-size: 14px;
}

.upload-tip {
  font-size: 12px;
  color: #F56C6C;
  font-weight: normal;
  margin-left: 5px;
}

.upload-tip-text {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
  line-height: 1.5;
}

.auto-ship-tip {
  margin-top: 10px;
  padding: 10px;
  background: #fdf6ec;
  color: #E6A23C;
  font-size: 13px;
  border-radius: 4px;
}

.upload-area {
  width: 100%;
}

/* 承诺框样式 */
.promise-box {
  margin-top: 20px;
  background-color: #fdf6ec;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #faecd8;
}

.promise-item {
  margin-bottom: 0;
}

.promise-text {
  font-weight: bold;
  color: #F56C6C;
}

.protocol-link {
  color: #409EFF;
  cursor: pointer;
  text-decoration: underline;
  margin-left: 5px;
}

/* 协议弹窗内容样式 */
.protocol-content {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #eee;
  padding: 15px;
  border-radius: 4px;
  line-height: 1.8;
  color: #606266;
}

.protocol-content h3 {
  color: #303133;
  margin-top: 15px;
  margin-bottom: 10px;
  font-size: 16px;
}

.read-tip {
  color: #F56C6C;
  font-size: 12px;
  margin-bottom: 10px;
}

.submit-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  gap: 15px;
}

.submit-button {
  padding: 12px 30px;
  font-size: 16px;
}

.reset-button {
  padding: 12px 25px;
}

@media (max-width: 768px) {
  .release-page-container {
    padding: 10px;
  }
  .submit-section {
    flex-direction: column;
  }
  .submit-button, .reset-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>