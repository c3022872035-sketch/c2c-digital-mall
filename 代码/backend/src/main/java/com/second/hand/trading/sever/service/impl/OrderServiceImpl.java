package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.MessageDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.MessageModel;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.utils.OrderTask;
import com.second.hand.trading.server.utils.OrderTaskHandler;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private MessageDao messageDao;

    @Resource
    private UserDao userDao;

    private static HashMap<Integer,ReentrantLock> lockMap=new HashMap<>();
    static {
        for(int i=0;i<100;i++){
            lockMap.put(i,new ReentrantLock(true));
        }
    }

    @Override
    public BigDecimal getTotalCommissionAmount() {
        return orderDao.getTotalCommissionAmount();
    }

    /**
     * 下单入口
     */
    public boolean addOrder(OrderModel orderModel){
        // 1. 校验商品状态
        IdleItemModel idleItemModel = idleItemDao.selectByPrimaryKey(orderModel.getIdleId());

        // 必须是上架状态(1)才能购买
        if(idleItemModel == null || idleItemModel.getIdleStatus() != 1){
            return false;
        }

        // 2. 构造更新对象
        IdleItemModel updateItem = new IdleItemModel();
        updateItem.setId(orderModel.getIdleId());
        updateItem.setUserId(idleItemModel.getUserId());

        // 3. 加锁并执行
        int key = (int) (orderModel.getIdleId() % 100);
        ReentrantLock lock = lockMap.get(key);
        boolean flag;
        try {
            lock.lock();
            flag = addOrderHelp(updateItem, orderModel);
        } finally {
            lock.unlock();
        }
        return flag;
    }

    /**
     * 事务方法：处理订单创建、金额计算及业务联动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrderHelp(IdleItemModel updateItem, OrderModel orderModel) {
        // 1. 获取下单时的商品总价
        BigDecimal totalPrice = orderModel.getOrderPrice();

        // 2. 核心财务逻辑：计算 5% 平台服务费
        // 使用 BigDecimal 避免浮动精度丢失
        BigDecimal rate = new BigDecimal("0.05");
        // 佣金 = 总价 * 0.05 (四舍五入保留两位小数)
        BigDecimal commission = totalPrice.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
        // 卖家收入 = 总价 - 佣金
        BigDecimal income = totalPrice.subtract(commission);

        // 3. 将财务计算结果存入订单对象
        orderModel.setCommissionPrice(commission);
        orderModel.setSellerIncome(income);

        // 4. 初始化核心状态字段 (非常关键：解决买家查不到订单的Bug)
        orderModel.setPaymentStatus((byte) 0); // 0: 未支付
        orderModel.setOrderStatus((byte) 0);   // 0: 待付款
        orderModel.setIsDeleted((byte) 0);      // 0: 正常状态 (不设置此值会导致SQL过滤失效)
        orderModel.setCommentFlag(0);           // 0: 尚未评价
        orderModel.setCreateTime(new Date());   // 记录创建时间

        // 5. 执行数据库插入
        // 这里的 insert 必须对应我们在 XML 中修补过的版本
        if (orderDao.insert(orderModel) == 1) {

            // 6. 业务联动：增加该商品的销量统计
            idleItemDao.increaseSales(updateItem.getId());

            // 7. 任务调度：添加订单超时自动取消任务 (设定为30分钟)
            // 确保 OrderTaskHandler 的依赖已在主类或此处正确初始化
            try {
                OrderTaskHandler.addOrder(new OrderTask(orderModel, 30 * 60));
            } catch (Exception e) {
                System.err.println(">>> 订单超时任务添加失败，但不影响主交易流程");
            }

            return true;
        }

        return false;
    }

    public OrderModel getOrder(Long id){
        OrderModel orderModel=orderDao.selectByPrimaryKey(id);
        if (orderModel != null) {
            orderModel.setIdleItem(idleItemDao.selectByPrimaryKey(orderModel.getIdleId()));
        }
        return orderModel;
    }

    /**
     * 辅助方法：填充订单列表的详情（买家、卖家、商品信息）
     */
    private void fillOrderDetails(List<OrderModel> list) {
        if (list.size() > 0) {
            List<Long> idleIdList = new ArrayList<>();
            List<Long> buyerIdList = new ArrayList<>();

            for (OrderModel i : list) {
                idleIdList.add(i.getIdleId());
                buyerIdList.add(i.getUserId());
            }

            // 1. 批量查询商品
            List<IdleItemModel> idleItems = idleItemDao.findIdleByList(idleIdList);
            Map<Long, IdleItemModel> idleMap = new HashMap<>();
            List<Long> sellerIdList = new ArrayList<>(); // 收集卖家ID

            for (IdleItemModel idle : idleItems) {
                idleMap.put(idle.getId(), idle);
                sellerIdList.add(idle.getUserId());
            }

            // 2. 批量查询买家
            List<UserModel> buyers = userDao.findUserByList(buyerIdList);
            Map<Long, UserModel> userMap = new HashMap<>();
            for (UserModel u : buyers) {
                userMap.put(u.getId(), u);
            }

            // 3. 批量查询卖家
            if(sellerIdList.size() > 0){
                List<UserModel> sellers = userDao.findUserByList(sellerIdList);
                for (UserModel u : sellers) {
                    userMap.put(u.getId(), u); // 合并到同一个Map里
                }
            }

            // 4. 组装数据
            for (OrderModel i : list) {
                // 设置买家
                i.setUser(userMap.get(i.getUserId()));

                // 设置商品及卖家
                IdleItemModel item = idleMap.get(i.getIdleId());
                if (item != null) {
                    item.setUser(userMap.get(item.getUserId()));
                    i.setIdleItem(item);
                }
            }
        }
    }

    @Override
    public PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums) {
        // 调用 Dao 进行模糊搜索
        List<OrderModel> list = orderDao.getOrderByNumber(searchValue, (page - 1) * nums, nums);
        // 填充详细信息
        fillOrderDetails(list);
        // 统计数量
        int count = orderDao.countAllOrder(searchValue);
        return new PageVo<>(list, count);
    }

    @Override
    public PageVo<OrderModel> getAllOrder(int page, int nums) {
        // 复用搜索方法，传 null 即查所有
        return findOrderByNumber(null, page, nums);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderModel orderModel){
        // 1. 防止关键信息被恶意修改
        orderModel.setOrderNumber(null);
        orderModel.setUserId(null);
        orderModel.setIdleId(null);
        orderModel.setCreateTime(null);

        // 2. 处理支付成功逻辑
        if(orderModel.getPaymentStatus() != null && orderModel.getPaymentStatus().equals((byte) 1)){
            System.out.println(">>> 正在处理支付成功回调，订单ID: " + orderModel.getId());
            orderModel.setPaymentTime(new Date());

            // --- 自动发货逻辑 ---
            OrderModel fullOrder = orderDao.selectByPrimaryKey(orderModel.getId());
            if(fullOrder != null) {
                IdleItemModel item = idleItemDao.selectByPrimaryKey(fullOrder.getIdleId());

                if (item != null && item.getResourcePath() != null && !item.getResourcePath().isEmpty()) {
                    orderModel.setOrderStatus((byte) 2);

                    // 发送私信逻辑
                    MessageModel autoMsg = new MessageModel();
                    autoMsg.setUserId(item.getUserId());
                    autoMsg.setToUser(fullOrder.getUserId());
                    autoMsg.setIdleId(item.getId());
                    autoMsg.setCreateTime(new Date());
                    autoMsg.setMessageStatus((byte)0);

                    String content = "【系统自动私信】\n" +
                            "感谢您的购买！\n" +
                            "\uD83D\uDCC4 文件名：" + (item.getOriginalFileName() != null ? item.getOriginalFileName() : "资源文件") + "\n" +
                            "\u2705 状态：已授权\n" +
                            "\uD83D\uDCE5 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n" +
                            "⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。";

                    if (item.getUnzipPassword() != null && !item.getUnzipPassword().isEmpty()) {
                        content += "\n解压密码：" + item.getUnzipPassword();
                    }

                    autoMsg.setContent(content);
                    messageDao.insert(autoMsg);
                    System.out.println(">>> 订单 " + fullOrder.getOrderNumber() + " 自动发货成功！状态已更新为已发货。");
                }
            }
        }

        // 3. 取消订单逻辑
        if(orderModel.getOrderStatus() != null && orderModel.getOrderStatus() == 4){
            return orderDao.updateByPrimaryKeySelective(orderModel) == 1;
        }

        return orderDao.updateByPrimaryKeySelective(orderModel) == 1;
    }

    public List<OrderModel> getMyOrder(Long userId){
        List<OrderModel> list=orderDao.getMyOrder(userId);
        if(list.size()>0){
            List<Long> idleIdList=new ArrayList<>();
            for(OrderModel i:list){
                idleIdList.add(i.getIdleId());
            }
            List<IdleItemModel> idleItemModelList=idleItemDao.findIdleByList(idleIdList);
            Map<Long,IdleItemModel> map=new HashMap<>();
            for(IdleItemModel idle:idleItemModelList){
                map.put(idle.getId(),idle);
            }
            for(OrderModel i:list){
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        return list;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OrderModel> getMySoldIdle(Long userId) {
        List<IdleItemModel> list = idleItemDao.getAllIdleItem(userId);
        List<OrderModel> orderList = new ArrayList<>();

        if (list.size() > 0) {
            List<Long> idleIdList = new ArrayList<>();
            Map<Long, IdleItemModel> map = new HashMap<>();

            for (IdleItemModel idle : list) {
                idleIdList.add(idle.getId());
                if (idle.getPictureList() != null) {
                    try {
                        List<String> pics = com.alibaba.fastjson.JSON.parseArray(idle.getPictureList(), String.class);
                        if (pics != null && !pics.isEmpty()) {
                            idle.setImgUrl(pics.get(0));
                        }
                    } catch (Exception e) {
                        System.err.println("解析商品图片异常: " + e.getMessage());
                    }
                }
                map.put(idle.getId(), idle);
            }
            orderList = orderDao.findOrderByIdleIdList(idleIdList);

            for (OrderModel o : orderList) {
                o.setIdleItem(map.get(o.getIdleId()));
            }
        }
        return orderList;
    }

    public boolean deleteOrder(long id){
        return orderDao.deleteByPrimaryKey(id)==1;
    }

    @Override
    public int getTotalOrderCount() { return orderDao.countAllOrder(null); }

    @Override
    public BigDecimal getTotalOrderAmount() { return orderDao.getTotalOrderAmount(); }

    @Override
    public int getCurrentMonthOrderCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        return orderDao.getOrderCountByDateRange(startDate, endDate);
    }

    @Override
    public BigDecimal getCurrentMonthOrderAmount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        return orderDao.getOrderAmountByDateRange(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getMonthlyOrderStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 6; i++) {
            Date endDate = calendar.getTime();
            calendar.add(Calendar.MONTH, -1);
            Date startDate = calendar.getTime();
            int orderCount = orderDao.getOrderCountByDateRange(startDate, endDate);
            BigDecimal orderAmount = orderDao.getOrderAmountByDateRange(startDate, endDate);
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthFormat.format(endDate));
            monthData.put("orderCount", orderCount);
            monthData.put("orderAmount", orderAmount);
            result.add(0, monthData);
            calendar.add(Calendar.MONTH, -1);
        }
        return result;
    }
}