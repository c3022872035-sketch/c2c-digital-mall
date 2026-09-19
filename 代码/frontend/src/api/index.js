import request from '../utils/request';

const api = {

    // 用户功能 对应usercontroller
    userLogin(query) {
        return request({
            url: '/user/login',
            method: 'get',
            params: query
        });
    },
    logout(query) {
        return request({
            url: '/user/logout',
            method: 'get',
            params: query
        });
    },
    signIn(data) {
        return request({
            url: '/user/sign-in',
            method: 'post',
            data: data
        });
    },
    getUserInfo(query) {
        return request({
            url: '/user/info',
            method: 'get',
            params: query
        });
    },
    updateUserPublicInfo(data) {
        return request({
            url: '/user/info',
            method: 'post',
            data: data
        });
    },
    updatePassword(query) {
        return request({
            url: '/user/password',
            method: 'get',
            params: query
        });
    },
    //  地址功能  对应addresscontroller
    addAddress(data) {
        return request({
            url: '/address/add',
            method: 'post',
            data: data
        });
    },
    getAddress(query) {
        return request({
            url: '/address/info',
            method: 'get',
            params: query
        });
    },
    updateAddress(data) {
        return request({
            url: '/address/update',
            method: 'post',
            data: data
        });
    },
    deleteAddress(data) {
        return request({
            url: '/address/delete',
            method: 'post',
            data: data
        });
    },

    // 商品操作   对应idleitemController
    addIdleItem(data) {
        return request({
            url: '/idle/add',
            method: 'post',
            data: data
        });
    },
    getIdleItem(query) {
        return request({
            url: '/idle/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleItem(query) {
        return request({
            url: '/idle/all',
            method: 'get',
            params: query
        });
    },
    findIdleItem(query) {
        return request({
            url: '/idle/find',
            method: 'get',
            params: query
        });
    },
    findIdleItemByLabel(query) {
        return request({
            url: '/idle/label', // 后端Controller的路径如果没改，这里URL保持原样
            method: 'get',
            params: query
        });
    },
    updateIdleItem(data) {
        return request({
            url: '/idle/update',
            method: 'post',
            data: data
        });
    },

    // 个人中心的功能  ordercontroller
    addOrder(data) {
        return request({
            url: '/order/add',
            method: 'post',
            data: data
        });
    },
    getOrder(query) {
        return request({
            url: '/order/info',
            method: 'get',
            params: query
        });
    },
    updateOrder(data) {
        return request({
            url: '/order/update',
            method: 'post',
            data: data
        });
    },
    getMyOrder(query) {
        return request({
            url: '/order/my',
            method: 'get',
            params: query
        });
    },
    getMySoldIdle(query) {
        return request({
            url: '/order/my-sold',
            method: 'get',
            params: query
        });
    },

    // 订单的地址信息   orderAddressController
    addOrderAddress(data) {
        return request({
            url: '/order-address/add',
            method: 'post',
            data: data
        });
    },
    updateOrderAddress(data) {
        return request({
            url: '/order-address/update',
            method: 'post',
            data: data
        });
    },
    getOrderAddress(query) {
        return request({
            url: '/order-address/info',
            method: 'get',
            params: query
        });
    },

    // 收藏功能就是购物车    favoriteController
    addFavorite(data) {
        return request({
            url: '/favorite/add',
            method: 'post',
            data: data
        });
    },
    getMyFavorite(query) {
        return request({
            url: '/favorite/my',
            method: 'get',
            params: query
        });
    },
    deleteFavorite(query) {
        return request({
            url: '/favorite/delete',
            method: 'get',
            params: query
        });
    },
    checkFavorite(query) {
        return request({
            url: '/favorite/check',
            method: 'get',
            params: query
        });
    },

    // 留言功能   messagecontroller
    sendMessage(data) {
        return request({
            url: '/message/send',
            method: 'post',
            data: data
        });
    },
    getMessage(query) {
        return request({
            url: '/message/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleMessage(query) {
        return request({
            url: '/message/idle',
            method: 'get',
            params: query
        });
    },
    getAllMyMessage(query) {
        return request({
            url: '/message/my',
            method: 'get',
            params: query
        });
    },
    deleteMessage(query) {
        return request({
            url: '/message/delete',
            method: 'get',
            params: query
        });
    },

    // 管理员相关操作
    getGoods(query) {
        return request({
            url: '/admin/idleList',
            method: 'get',
            params: query
        });
    },
    updateGoods(query) {
        return request({
            url: '/admin/updateIdleStatus',
            method: 'get',
            params: query
        });
    },
    getOrderList(query) {
        return request({
            url: '/admin/orderList',
            method: 'get',
            params: query
        });
    },
    deleteOrder(query) {
        return request({
            url: '/admin/deleteOrder',
            method: 'get',
            params: query
        });
    },
    getUserData(query) {
        return request({
            url: '/admin/userList',
            method: 'get',
            params: query
        });
    },
    getUserManage(query) {
        return request({
            url: '/admin/list',
            method: 'get',
            params: query
        });
    },
    updateUserStatus(query){
        return request({
            url: '/admin/updateUserStatus',
            method: 'get',
            params: query
        });
    },
    regAdministrator(data){
        return request({
            url: '/admin/add',
            method: 'post',
            data: data
        });
    },
    adminLogin(query) {
        return request({
            url: '/admin/login',
            method: 'get',
            params: query
        });
    },
    loginOut(query) {
        return request({
            url: '/admin/loginOut',
            method: 'get',
            params: query
        });
    },
    queryIdle(query) {
        return request({
            url: '/admin/queryIdle',
            method: 'get',
            params: query
        });
    },
    queryOrder(query) {
        return request({
            url: '/admin/queryOrder',
            method: 'get',
            params: query
        });
    },
    queryUser(query) {
        return request({
            url: '/admin/queryUser',
            method: 'get',
            params: query
        });
    },
    updateAlipay(query) {
        return request({
            url: '/alipay/pay',
            method: 'get',
            params: query
        });
    },
    // 获取交易数据统计
    getTradingData(query) {
        return request({
            url: '/statistics/trading-data',
            method: 'get',
            params: query
        });
    },

    // 获取月度统计数据
    getMonthlyStatistics(query) {
        return request({
            url: '/statistics/monthly-statistics',
            method: 'get',
            params: query
        });
    },

    // 获取商品分类统计
    getCategoryStatistics(query) {
        return request({
            url: '/statistics/category-statistics',
            method: 'get',
            params: query
        });
    },

    // 获取用户统计数据
    getUserStatistics(query) {
        return request({
            url: '/statistics/user-statistics',
            method: 'get',
            params: query
        });
    },

    // 添加评价功能
    addComment(data) {
        return request({
            url: '/comment/add',
            method: 'post',
            data: data
        });
    },

    //显示评价
    getCommentList(query) {
        return request({
            url: '/comment/list',
            method: 'get',
            params: query
        });
    },

    getUnreadCount() {
        return request({
            url: '/message/unread/count',
            method: 'get'
        });
    },
    getChatHistory(query) {
        return request({
            url: '/message/chat/history',
            method: 'get',
            params: query
        });
    },
    // 获取会话列表
    getChatList(query) {
        return request({
            url: '/message/chat/list',
            method: 'get',
            params: query
        });
    },

    // 投诉
    addReport(data) {
        return request({
            url: '/report/add',
            method: 'post',
            data: data
        });
    },

    // 管理员-获取待处理投诉数量
    getPendingReportCount() {
        return request({
            url: '/admin/report/pendingCount',
            method: 'get'
        });
    },
    // 管理员-获取投诉列表
    getReportList(query) {
        return request({
            url: '/admin/reportList',
            method: 'get',
            params: query
        });
    },
    // 管理员-处理投诉
    handleReport(data) {
        return request({
            url: '/admin/handleReport',
            method: 'post',
            data: data
        });
    },
    // 管理员-获取评价列表
    getAdminCommentList(query) {
        return request({
            url: '/admin/commentList',
            method: 'get',
            params: query
        });
    },
    // 管理员-删除评价
    deleteAdminComment(query) {
        return request({
            url: '/admin/deleteComment',
            method: 'get',
            params: query
        });
    },
    //管理员聊天接口
    getAdminChatHistory(query) {
        return request({
            url: '/admin/chat/history',
            method: 'get',
            params: query
        });
    },
    sendAdminMessage(data) {
        return request({
            url: '/admin/chat/send',
            method: 'post',
            data: data
        });
    },
    // 管理员-添加商品
    addAdminIdle(data) {
        return request({
            url: '/admin/addIdle',
            method: 'post',
            data: data
        });
    },
    // 管理员-修改商品
    updateAdminIdle(data) {
        return request({
            url: '/admin/updateIdle',
            method: 'post',
            data: data
        });
    },
    // 管理员-新增用户
    addAdminUser(data) {
        return request({
            url: '/admin/addUser',
            method: 'post',
            data: data
        });
    },
    // 管理员-修改用户
    updateAdminUser(data) {
        return request({
            url: '/admin/updateUser',
            method: 'post',
            data: data
        });
    },
    // 管理员-删除用户
    deleteAdminUser(query) {
        return request({
            url: '/admin/deleteUser',
            method: 'get',
            params: query
        });
    },
    updateAdministrator(data) {
        return request({
            url: '/admin/update',
            method: 'post',
            data: data
        });
    },
    // 管理员-删除管理员
    deleteAdministrator(query) {
        return request({
            url: '/admin/delete',
            method: 'get',
            params: query
        });
    },
    // 管理员-新增评价
    addAdminComment(data) {
        return request({
            url: '/admin/addComment',
            method: 'post',
            data: data
        });
    },
    // 管理员-修改评价
    updateAdminComment(data) {
        return request({
            url: '/admin/updateComment',
            method: 'post',
            data: data
        });
    },
    // 获取简易用户列表（用于下拉搜索）- 复用 queryUser
    searchUserForSelect(query) {
        return request({
            url: '/admin/queryUser',
            method: 'get',
            params: query
        });
    },
    // 获取简易商品列表（用于下拉搜索）- 复用 queryIdle
    searchIdleForSelect(query) {
        return request({
            url: '/admin/queryIdle',
            method: 'get',
            params: query
        });
    },
    // 管理员-获取审核列表
    getAuditList(query) {
        return request({
            url: '/admin/auditList',
            method: 'get',
            params: query
        });
    },
    // 管理员-审核操作
    auditItem(data) {
        return request({
            url: '/admin/audit',
            method: 'post',
            params: data
        });
    },
    // 获取商品历史
    getIdleHistory(query) {
        return request({
            url: '/idle/history',
            method: 'get',
            params: query
        });
    },
    // 工具：百度AI图片版权检测
    checkImageCopyright(query) {
        return request({
            url: '/tool/check-copyright',
            method: 'get',
            params: query
        });
    },
    getAdminIdleHistory(query) {
        return request({
            url: '/admin/idle/history', // 对应 AdminController 中的路径
            method: 'get',
            params: query
        });
    },

    getRecommend(query) {
        return request({
            url: '/idle/recommend',
            method: 'get',
            params: query
        });
    },

    // 上传资源文件
    uploadResourceFile(formData) {
        return axios.post('http://localhost:8080/file/uploadResource', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            withCredentials: true
        });
    },

    adminRefundOrder(query) {
        return request({
            url: '/admin/refundOrder',
            method: 'post',
            params: query
        });
    },

    getDownloadUrl(orderId, userId) {
        return `http://localhost:8080/resource/download?orderId=${orderId}&userId=${userId}`;
    }
};

export default api;