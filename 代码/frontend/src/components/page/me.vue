<template>
  <div>
    <app-head :nickname-value="userInfo.nickname" :avatarValue="userInfo.avatar"></app-head>
    <app-body>
      <!-- 个人中心主视图 -->
      <div v-show="!editAddress" class="profile-container">
        <!-- 1. 用户基本信息卡片 -->
        <el-card class="user-profile-card" shadow="hover">
          <div class="user-info-container">
            <div class="user-info-details">
              <el-upload
                  action="http://localhost:8080/file/"
                  :on-success="fileHandleSuccess"
                  :file-list="imgFileList"
                  :show-file-list="false"
                  accept="image/*"
                  class="avatar-uploader">
                <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar">
                  <i class="el-icon-plus avatar-uploader-icon"></i>
                </el-avatar>
                <div class="avatar-hint">点击更换头像</div>
              </el-upload>
              <div class="user-info-details-text">
                <div class="user-info-details-text-nickname">{{userInfo.nickname}}</div>
                <div class="user-info-details-text-time">
                  <i class="el-icon-time"></i> {{userInfo.signInTime}} 加入平台
                </div>
                <div class="user-info-details-text-edit">
                  <el-button type="primary" icon="el-icon-edit" round @click="userInfoDialogVisible = true">编辑个人资料</el-button>
                  <el-button type="warning" icon="el-icon-postcard" round @click="editAddress=true">管理联系信息</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 2. 内容交互卡片（选项卡） -->
        <el-card class="user-content-card" shadow="hover">
          <div class="idle-container">
            <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card" class="custom-tabs">
              <el-tab-pane label="待审商品" name="6"><span slot="label"><i class="el-icon-s-check tab-icon"></i> 待审商品</span></el-tab-pane>
              <el-tab-pane label="刚刚发布" name="1"><span slot="label"><i class="el-icon-upload tab-icon"></i> 刚刚发布</span></el-tab-pane>
              <el-tab-pane label="已经下架" name="2"><span slot="label"><i class="el-icon-download tab-icon"></i> 已经下架</span></el-tab-pane>
              <el-tab-pane label="我的购物车" name="3"><span slot="label"><i class="el-icon-shopping-cart-full tab-icon"></i> 我的购物车</span></el-tab-pane>
              <el-tab-pane label="出售记录" name="4"><span slot="label"><i class="el-icon-sold-out tab-icon"></i> 出售记录</span></el-tab-pane>
              <el-tab-pane label="购买记录" name="5">
                  <span slot="label">
                      <i class="el-icon-shopping-bag-1 tab-icon"></i> 购买记录
                      <el-badge :value="pendingReviewCount" :max="99" :hidden="pendingReviewCount===0" class="tab-badge" />
                  </span>
              </el-tab-pane>
            </el-tabs>

            <!-- 列表渲染 -->
            <div class="idle-container-list">
              <div v-for="(item,index) in dataList[parseInt(activeName)-1]" :key="index" class="idle-container-list-item">
                <div class="idle-container-list-item-detail" @click="toDetails(activeName,item)">
                  <el-image style="width: 100px; height: 100px;" :src="item.imgUrl" fit="cover">
                    <div slot="error" class="image-slot"><i class="el-icon-picture-outline">无图</i></div>
                  </el-image>
                  <div class="idle-container-list-item-text">
                    <div class="idle-container-list-title">
                      {{item.idleName}}
                      <el-tooltip content="待评价订单" placement="top" v-if="activeName==='5' && showReviewAlert(item)">
                        <i class="el-icon-warning" style="color: #F56C6C; margin-left: 5px; animation: pulse 2s infinite;"></i>
                      </el-tooltip>
                    </div>

                    <div class="idle-container-list-idle-details">{{item.idleDetails}}</div>
                    <div class="idle-container-list-idle-time">{{item.timeStr}}</div>

                    <div class="idle-item-foot">
                      <div class="idle-price">
                        ￥{{item.idlePrice}}
                        <!-- 后台抽成显示 -->
                        <span v-if="activeName==='4' && item.commissionPrice" style="margin-left: 10px; font-size: 12px; color: #909399; font-weight: normal;">
                          (服务费: -￥{{item.commissionPrice}} 实收: ￥{{item.sellerIncome}})
                        </span>
                        <!-- 购买状态展示 -->
                        <span v-if="activeName==='5'" style="margin-left: 10px; font-size: 14px;">
                            <span v-if="item.orderStatus === 3 && (!item.commentFlag || item.commentFlag === 0)" style="color: #409EFF; font-weight: bold;">待评价</span>
                            <span v-else style="color: #909399;">{{ orderStatus[item.orderStatus] }}</span>
                        </span>

                        <template v-if="activeName==='6'">
                          <el-tag v-if="item.idleStatus===3" size="small" type="warning" effect="dark" style="margin-left: 10px;">正在审核中</el-tag>
                          <div v-if="item.idleStatus===4" style="display:inline-block; margin-left: 10px;">
                            <span class="reject-hint-text">
                              <i class="el-icon-warning"></i> 审核已被驳回，原因详看私信，请点击修改继续提交审核。
                            </span>
                          </div>
                        </template>
                      </div>

                      <div class="idle-foot-actions">
                        <el-button v-if="activeName==='1' || (activeName==='6' && item.idleStatus===3)" type="primary" size="mini" icon="el-icon-time" plain @click.stop="openHistory(item)">历史</el-button>

                        <!-- 修改与删除 -->
                        <el-button v-if="activeName==='1' || activeName==='6'" type="success" size="mini" icon="el-icon-edit" plain @click.stop="openEditDialog(item)">修改</el-button>
                        <el-button v-if="activeName!=='4' && activeName!=='5'" type="danger" size="mini" plain :icon="getHandleIcon(activeName)" @click.stop="handle(activeName,item,index)">
                          删除
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- 空状态 -->
              <div v-if="dataList[parseInt(activeName)-1].length === 0" class="empty-state">
                <i :class="getEmptyIcon(activeName)" class="empty-icon"></i>
                <p class="empty-text">{{getEmptyText(activeName)}}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 3. 联系人信息管理视图 -->
      <div v-show="editAddress" class="address-container">
        <el-card class="address-card" shadow="hover">
          <div slot="header" class="address-header">
            <el-page-header @back="editAddress=false" content="常用联系人管理"></el-page-header>
          </div>

          <el-card class="new-address-card" shadow="never">
            <div slot="header"><span><i class="el-icon-edit"></i> {{addressInfo.id ? '编辑信息' : '新增联系人'}}</span></div>
            <el-form label-position="top" :model="addressInfo">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="姓名">
                    <el-input placeholder="请输入姓名" v-model="addressInfo.consigneeName" maxlength="10" prefix-icon="el-icon-user"></el-input>
                  </el-form-item>
                </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="手机号码">
                      <el-input placeholder="用于接收通知" v-model="addressInfo.consigneePhone" maxlength="11" prefix-icon="el-icon-mobile-phone"></el-input>
                    </el-form-item>
                  </el-col>
              </el-row>
              <el-form-item>
                <el-checkbox v-model="addressInfo.defaultFlag">设置为默认联系人</el-checkbox>
                <el-button type="primary" style="margin-left: 20px" @click="saveAddress">保存信息</el-button>
                <el-button @click="resetAddressForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

            <div class="address-list-section">
              <div class="address-list-header"><i class="el-icon-postcard"></i> 已保存联系人</div>
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" v-for="(address, index) in addressData" :key="index">
                  <el-card class="address-item" :class="{'default-address': address.defaultFlag}">
                    <div class="address-item-header">
                      <span class="address-name">{{address.consigneeName}}</span>
                      <span class="address-phone">{{address.consigneePhone}}</span>
                      <el-tag v-if="address.defaultFlag" size="mini" type="success">默认</el-tag>
                    </div>
                    <div class="address-item-actions">
                      <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(index, address)">编辑</el-button>
                      <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C" @click="handleDelete(index, address)">删除</el-button>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-card>
      </div>

      <!-- 4. 个人资料修改弹窗 -->
      <el-dialog @close="finishEdit" title="编辑个人信息" :visible.sync="userInfoDialogVisible" width="400px" center>
        <div class="edit-form">
          <div class="edit-form-item">
            <div class="edit-tip">用户昵称</div>
            <el-input v-model="userInfo.nickname" :disabled="notUserNicknameEdit" placeholder="请输入昵称">
              <el-button slot="append" icon="el-icon-edit" @click="notUserNicknameEdit = false">编辑</el-button>
            </el-input>
          </div>
          <div class="edit-form-item" v-if="userPasswordEdit">
            <div class="edit-tip">新密码</div>
            <el-input v-model="userPassword2" placeholder="请输入新密码" show-password></el-input>
            <div class="edit-actions" style="margin-top:10px">
              <el-button type="primary" size="mini" @click="savePassword">提交修改</el-button>
              <el-button size="mini" @click="userPasswordEdit = false">取消</el-button>
            </div>
          </div>
          <div class="edit-form-item" v-else style="margin-top:15px">
            <el-button type="text" icon="el-icon-lock" @click="userPasswordEdit = true">修改登录密码</el-button>
          </div>
        </div>
      </el-dialog>

      <!-- 5. 修改商品信息弹窗  -->
      <el-dialog title="完善商品信息并重新提交" :visible.sync="editDialogVisible" width="650px" center>
        <el-form :model="editForm" label-width="110px">
          <el-form-item label="商品标题">
            <el-input v-model="editForm.idleName" placeholder="商品标题"></el-input>
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input type="textarea" :rows="4" v-model="editForm.idleDetails" placeholder="请详细说明内容"></el-input>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="商品价格" v-if="editForm.idleLabel !== 10">
                <el-input-number v-model="editForm.idlePrice" :precision="2" :step="1" :min="0" style="width:100%"></el-input-number>
              </el-form-item>
            </el-col>
              <el-col :span="12">
                <el-form-item label="商品类别">
                  <el-select v-model="editForm.idleLabel" placeholder="请选择" style="width:100%">
                    <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
          </el-row>

          <!-- 数字化资源包管理 -->
          <el-card shadow="never" v-if="editForm.idleLabel !== 10" style="margin-bottom: 15px; border: 1px dashed #eee;">
            <div slot="header" style="font-size: 13px; font-weight: bold;"><i class="el-icon-upload"></i> 数字资源包</div>
            <el-form-item label="更换文件">
              <el-upload action="#" :http-request="customUpload" :limit="1" :file-list="resourceFileList" accept=".zip">
                <el-button size="small" type="primary">重新上传 (.zip)</el-button>
              </el-upload>
            </el-form-item>
            <div v-if="editForm.originalFileName" style="font-size: 12px; color: #666; padding-left: 110px; margin-bottom: 5px;">
              当前：<span style="color:#409EFF">{{ editForm.originalFileName }}</span>
            </div>
            <el-form-item label="解压密码">
              <el-input v-model="editForm.unzipPassword" placeholder="若有密码请填写" prefix-icon="el-icon-key"></el-input>
            </el-form-item>
          </el-card>

          <el-card shadow="never" style="margin-bottom: 15px; border: 1px dashed #E6A23C; background: #fffdf9;">
            <div slot="header" style="font-size: 13px; font-weight: bold; color: #E6A23C;"><i class="el-icon-medal"></i> 版权证明</div>
            <el-form-item label="版权证明图">
              <el-upload
                  action="http://localhost:8080/file/"
                  :on-success="handleEditProofSuccess"
                  :limit="1"
                  list-type="picture"
                  :file-list="proofFileList"
                  accept="image/*">
                <el-button size="small" type="warning" plain icon="el-icon-upload2">上传新证明图</el-button>
              </el-upload>
            </el-form-item>
          </el-card>

          <el-form-item label="预览图">
            <el-upload action="http://localhost:8080/file/" list-type="picture-card" :on-success="handleGoodsImageSuccess" :on-remove="handleGoodsImageRemove" :file-list="goodsFileList" :limit="5">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="editDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitEdit">重新提交审核</el-button>
        </div>
      </el-dialog>

      <!-- 6. 历史记录展示弹窗 (结构完整保留) -->
      <el-dialog title="商品版本变更历史" :visible.sync="historyVisible" width="780px" center>
        <div v-if="historyList.length > 0" class="history-timeline">
          <el-timeline>
            <el-timeline-item v-for="(hist, index) in historyList" :key="index" :timestamp="formatTime(hist.createTime)" placement="top" color="#409EFF">
              <el-card class="history-card">
                <div slot="header" class="history-header">
                  <span>版本快照 (v{{ historyList.length - index }})</span>
                  <el-tag size="mini" type="info">{{ getStatusText(hist.idleStatus) }}</el-tag>
                </div>
                <div class="history-content">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="hist-item"><span class="hist-label">标题:</span> {{ hist.idleName }}</div>
                      <div class="hist-item"><span class="hist-label">价格:</span> <b style="color:#F56C6C">¥{{ hist.idlePrice }}</b></div>
                      <div class="hist-item"><span class="hist-label">分类:</span> {{ getCategoryName(hist.idleLabel) }}</div>
                    </el-col>
                    <el-col :span="12">
                      <div class="hist-item"><span class="hist-label">说明:</span> {{ hist.idlePlace }}</div>
                      <div class="hist-item" v-if="hist.originalFileName"><span class="hist-label">文件:</span> {{ hist.originalFileName }}</div>
                      <div class="hist-item" v-if="hist.unzipPassword"><span class="hist-label">密码:</span> {{ hist.unzipPassword }}</div>
                    </el-col>
                  </el-row>
                  <div class="hist-item" style="margin-top:10px">
                    <span class="hist-label">描述:</span>
                    <div class="hist-desc-box">{{ hist.idleDetails }}</div>
                  </div>
                  <div class="hist-item" style="margin-top:15px">
                    <span class="hist-label">快照预览:</span>
                    <div style="display:flex; flex-wrap:wrap; gap:10px; margin-top:5px">
                      <el-image v-for="(pic, i) in getImgList(hist.pictureList)" :key="i" class="hist-thumb" :src="pic" :preview-src-list="getImgList(hist.pictureList)"></el-image>
                      <el-image v-if="hist.proofImage" class="hist-thumb proof" :src="hist.proofImage" :preview-src-list="[hist.proofImage]"></el-image>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
        <div v-else class="empty-history"><i class="el-icon-info"></i> 该商品尚无修改记录</div>
      </el-dialog>
    </app-body>
    <app-foot></app-foot>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';
import { categories, getCategoryLabel } from '@/utils/categoryConfig';
import axios from 'axios';

export default {
  name: "me",
  components: { AppHead, AppBody, AppFoot },
  data() {
    return {
      imgFileList: [],
      addressInfo: { consigneeName: '', consigneePhone: '', defaultFlag: false },
      activeName: '6',
      handleName: ['下架', '删除', '取消购物车', '', '', '删除'],
      dataList: [[], [], [], [], [], [], [], []],
      orderStatus: ['待付款', '待交付', '已交付', '已完成', '已取消'],
      userInfoDialogVisible: false,
      notUserNicknameEdit: true,
      userPasswordEdit: false,
      userPassword1: '', userPassword2: '',
      editAddress: false, // 拼写修复
      userInfo: { accountNumber: "", avatar: "", nickname: "", signInTime: "" },
      addressData: [],
      categoryOptions: categories,

      // 修改相关
      editDialogVisible: false,
      editForm: { id: '', idleName: '', idleDetails: '', idlePrice: 0, idleLabel: '', idlePlace: '', pictureList: '', proofImage: '', idleStatus: 0, resourcePath: '', originalFileName: '', unzipPassword: '', fileHash: '' },
      goodsFileList: [], goodsImgList: [], proofFileList: [], resourceFileList: [],

      historyVisible: false, historyList: [],
      readReviewOrders: JSON.parse(localStorage.getItem('readReviewOrders') || '[]'),
    };
  },
  computed: {
    pendingReviewCount() {
      if (!this.dataList[4]) return 0;
      return this.dataList[4].filter(item => item.orderStatus === 3 && (!item.commentFlag || item.commentFlag === 0) && !this.readReviewOrders.includes(item.id)).length;
    }
  },
  created() {
    this.initUser();
    this.getAddressData();
    this.getIdleItemData();
    this.getMyOrder();
    this.getMySoldIdle();
    this.getMyFavorite();
  },
  methods: {
    initUser() {
      if (!this.$globalData.userInfo.nickname) {
        this.$api.getUserInfo().then(res => {
          if (res.status_code === 1) {
            res.data.signInTime = res.data.signInTime.substring(0, 10);
            this.$globalData.userInfo = res.data;
            this.userInfo = this.$globalData.userInfo;
          }
        })
      } else { this.userInfo = this.$globalData.userInfo; }
    },
    // --- 核心方法：版权证明图上传处理 ---
    handleEditProofSuccess(res) {
      if(res.status_code === 1) {
        this.editForm.proofImage = res.data;
        this.$message.success('版权证明已更新');
      }
    },
    openEditDialog(item) {
      this.editForm = JSON.parse(JSON.stringify(item));
      this.goodsImgList = JSON.parse(item.pictureList || '[]');
      this.goodsFileList = this.goodsImgList.map(url => ({ url }));

      // 证明图回显
      this.proofFileList = [];
      if (this.editForm.proofImage) {
        this.proofFileList = [{ name: '当前证明', url: this.editForm.proofImage }];
      }
      this.resourceFileList = [];
      this.editDialogVisible = true;
    },
    submitEdit() {
      if(!this.editForm.idleName || !this.editForm.idleLabel) return this.$message.warning('请填写必填项');
      this.editForm.pictureList = JSON.stringify(this.goodsImgList);
      this.editForm.idleStatus = 3; // 重置状态为待审
      this.$api.updateIdleItem(this.editForm).then(res => {
        if(res.status_code === 1) {
          this.$message.success('已重新提交审核');
          this.editDialogVisible = false;
          this.getIdleItemData();
          this.activeName = '6';
        }
      });
    },
    // 商品列表加载逻辑
    getIdleItemData() {
      this.dataList = [[], [], [], [], [], [], [], []];
      this.$api.getAllIdleItem().then(res => {
        if (res.status_code === 1) {
          res.data.forEach(item => {
            item.timeStr = item.releaseTime.replace("T", " ").substring(0, 16);
            let pics = JSON.parse(item.pictureList || '[]');
            item.imgUrl = pics.length > 0 ? pics[0] : '';
            if (item.idleStatus === 1) this.dataList[0].push(item);
            else if (item.idleStatus === 2) this.dataList[1].push(item);
            else if (item.idleStatus === 3 || item.idleStatus === 4) this.dataList[5].push(item);
          });
        }
      })
    },
    // 联系人管理逻辑
    saveAddress() {
      const data = { ...this.addressInfo, provinceName: '数字', cityName: '交付', regionName: '节点', detailAddress: '在线' };
      const api = this.addressInfo.id ? this.$api.updateAddress : this.$api.addAddress;
      api(data).then(res => { if (res.status_code === 1) { this.$message.success('保存成功'); this.getAddressData(); this.resetAddressForm(); } });
    },
    getAddressData() { this.$api.getAddress().then(res => { if (res.status_code === 1) this.addressData = res.data; }) },
    resetAddressForm() { this.addressInfo = { consigneeName: '', consigneePhone: '', defaultFlag: false }; },
    handleEdit(index, row) { this.addressInfo = JSON.parse(JSON.stringify(row)); },
    handleDelete(index, row) { this.$api.deleteAddress(row).then(() => this.getAddressData()); },
    handleSetDefault(index, row) { row.defaultFlag = true; this.$api.updateAddress(row).then(() => this.getAddressData()); },

    // 基础业务加载
    getMyOrder(){ this.$api.getMyOrder().then(res=>{ if (res.status_code === 1){ res.data.forEach(o => { let pics = JSON.parse(o.idleItem.pictureList || '[]'); this.dataList[4].push({ id:o.id, imgUrl:pics[0], idleName:o.idleItem.idleName, idleDetails:o.idleItem.idleDetails, timeStr:o.createTime.replace("T"," ").substring(0,16), idlePrice:o.orderPrice, orderStatus:o.orderStatus, commentFlag: o.commentFlag }); }); } }) },
    getMySoldIdle(){ this.$api.getMySoldIdle().then(res=>{ if (res.status_code === 1){ res.data.forEach(o => { let pics = JSON.parse(o.idleItem.pictureList || '[]'); this.dataList[3].push({ id:o.id, imgUrl:pics[0], idleName:o.idleItem.idleName, idleDetails:o.idleItem.idleDetails, timeStr:o.createTime.replace("T"," ").substring(0,16), idlePrice:o.orderPrice, orderStatus:o.orderStatus, commissionPrice: o.commissionPrice, sellerIncome: o.sellerIncome }); }); } }) },
    getMyFavorite(){ this.$api.getMyFavorite().then(res=>{ if (res.status_code === 1){ res.data.forEach(f => { let pics = JSON.parse(f.idleItem.pictureList || '[]'); this.dataList[2].push({ favoriteId:f.id, id:f.idleItem.id, imgUrl:pics[0], idleName:f.idleItem.idleName, idleDetails:f.idleItem.idleDetails, timeStr:f.createTime.replace("T"," ").substring(0,16), idlePrice:f.idleItem.idlePrice }); }); } }) },

    // 辅助工具方法
    getCategoryName(val) { return getCategoryLabel(val); },
    getImgList(str) { try { return JSON.parse(str); } catch(e) { return []; } },
    formatTime(t) { return t ? t.replace("T", " ").substring(0, 16) : ''; },
    getStatusText(s) { const m = {1: '上线', 2: '下架', 3: '待审', 4: '驳回'}; return m[s] || '未知'; },
    getHandleIcon(i) { const m = {'1': 'el-icon-download', '2': 'el-icon-delete', '3': 'el-icon-star-off', '6': 'el-icon-delete'}; return m[i] || ''; },
    getEmptyIcon(t) { const m = {'1': 'el-icon-upload', '2': 'el-icon-download', '3': 'el-icon-shopping-cart-full', '4': 'el-icon-sold-out', '5': 'el-icon-shopping-bag-1', '6': 'el-icon-s-check'}; return m[t] || ''; },
    getEmptyText(t) { const m = {'1': '无发布商品', '2': '无下架商品', '3': '购物车为空', '4': '无售出记录', '5': '无购买记录', '6': '无待审商品'}; return m[t] || ''; },
    showReviewAlert(i) { return i.orderStatus === 3 && (!i.commentFlag || i.commentFlag === 0) && !this.readReviewOrders.includes(i.id); },
    toDetails(n, i) { if (n === '5' || n === '4') this.$router.push({path: '/order', query: {id: i.id}}); else this.$router.push({path: '/details', query: {id: i.id}}); },
    handleClick() {},
    saveUserNickname() { this.notUserNicknameEdit = true; this.$api.updateUserPublicInfo({ nickname: this.userInfo.nickname }).then(() => this.$globalData.userInfo.nickname = this.userInfo.nickname); },
    savePassword() { this.$api.updatePassword({ oldPassword: '0', newPassword: this.userPassword2 }).then(res => { if(res.status_code === 1) this.$message.success('修改成功'); }); },
    finishEdit() { this.notUserNicknameEdit = true; this.userInfoDialogVisible = false; this.userPasswordEdit = false; },
    handle(n, i, idx) { this.$confirm('确认操作？').then(() => { if(n === '1') this.$api.updateIdleItem({ id:i.id, idleStatus:2 }).then(() => { this.dataList[0].splice(idx,1); this.getIdleItemData(); }); else if(n === '2' || n === '6') this.$api.updateIdleItem({ id:i.id, idleStatus:0 }).then(() => this.dataList[n==='2'?1:5].splice(idx,1)); else if(n === '3') this.$api.deleteFavorite({ id: i.favoriteId }).then(() => this.dataList[2].splice(idx,1)); }); },
    fileHandleSuccess(res) { if(res.status_code === 1) this.$api.updateUserPublicInfo({ avatar: res.data }).then(() => this.userInfo.avatar = res.data); },
    customUpload(param) {
      const formData = new FormData(); formData.append('file', param.file);
      axios.post('http://localhost:8080/file/uploadResource', formData, { withCredentials: true }).then(res => {
        if(res.data.status_code === 1) { this.editForm.resourcePath = res.data.data.fileName; this.editForm.originalFileName = param.file.name; this.$message.success('资源包已更新'); }
      });
    },
    handleRemoveResource() { this.editForm.resourcePath = ''; this.editForm.originalFileName = ''; },
    openHistory(item) { this.historyVisible = true; this.historyList = []; this.$api.getIdleHistory({ idleId: item.id }).then(res => { if(res.status_code === 1) this.historyList = res.data; }); },
    handleGoodsImageSuccess(res) { if(res.status_code === 1) this.goodsImgList.push(res.data); },
    handleGoodsImageRemove(file) { let url = file.response ? file.response.data : file.url; this.goodsImgList = this.goodsImgList.filter(u => u !== url); }
  }
}
</script>

<style scoped>
.reject-hint-text {
  font-size: 13px;
  color: #F56C6C;
  background: #fff5f5;
  padding: 5px 12px;
  border-radius: 4px;
  border: 1px solid #ffeded;
  margin-left: 10px;
  font-weight: normal;
  display: inline-block;
  animation: fadePulse 2s infinite;
}

@keyframes fadePulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.profile-container { padding: 20px; }
.user-profile-card, .user-content-card, .address-card { margin-bottom: 20px; border-radius: 12px; }
.user-info-container { display: flex; align-items: center; padding: 10px 0; }
.user-info-details { display: flex; align-items: center; }
.user-avatar { border: 3px solid #fff; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
.user-info-details-text { margin-left: 25px; }
.user-info-details-text-nickname { font-size: 28px; font-weight: bold; color: #333; margin-bottom: 8px; }
.user-info-details-text-time { color: #999; font-size: 14px; margin-bottom: 15px; }
.user-info-details-text-edit { display: flex; gap: 10px; }

.custom-tabs { border-radius: 8px; overflow: hidden; }
.idle-container-list-item { border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.3s; }
.idle-container-list-item:hover { background: #fafafa; }
.idle-container-list-item-detail { display: flex; padding: 15px; }
.idle-container-list-item-text { flex: 1; margin-left: 20px; }
.idle-container-list-title { font-weight: bold; font-size: 17px; color: #333; display: flex; align-items: center; }
.idle-container-list-idle-details { font-size: 14px; color: #666; margin: 8px 0; height: 38px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.idle-item-foot { display: flex; justify-content: space-between; align-items: center; }
.idle-price { color: #f56c6c; font-weight: bold; font-size: 18px; }
.idle-foot-actions { display: flex; gap: 8px; }

.history-timeline { max-height: 550px; overflow-y: auto; padding: 10px; }
.history-header { display: flex; justify-content: space-between; font-weight: bold; }
.hist-item { margin-bottom: 10px; font-size: 14px; }
.hist-label { color: #909399; font-weight: 600; margin-right: 8px; }
.hist-desc-box { background: #f8f9fa; padding: 10px; border-radius: 6px; margin-top: 5px; color: #666; border: 1px solid #eee; }
.hist-thumb { width: 50px; height: 50px; border-radius: 4px; border: 1px solid #ddd; }
.hist-thumb.proof { border: 2px solid #e1f3d8; }

.address-item { margin-bottom: 15px; border-radius: 8px; }
.default-address { border: 1px solid #67c23a; background: #f0f9eb; }
.address-item-header { display: flex; justify-content: space-between; margin-bottom: 10px; font-weight: bold; }
.empty-state, .empty-address { padding: 60px 0; text-align: center; color: #ccc; }
.empty-icon { font-size: 50px; margin-bottom: 10px; }
</style>