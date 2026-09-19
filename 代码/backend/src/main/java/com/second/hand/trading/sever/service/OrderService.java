package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.vo.PageVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderService {

    boolean addOrder(OrderModel orderModel);

    OrderModel getOrder(Long id);

    boolean addOrderHelp(IdleItemModel updateItem, OrderModel orderModel);

    PageVo<OrderModel> findOrderByNumber(String searchValue, int page, int nums);

    boolean updateOrder(OrderModel orderModel);

    List<OrderModel> getMyOrder(Long userId);

    List<OrderModel> getMySoldIdle(Long userId);

    PageVo<OrderModel> getAllOrder(int page, int nums);

    boolean deleteOrder(long id);

    int getTotalOrderCount();

    BigDecimal getTotalOrderAmount();

    BigDecimal getTotalCommissionAmount();

    int getCurrentMonthOrderCount();

    BigDecimal getCurrentMonthOrderAmount();

    List<Map<String, Object>> getMonthlyOrderStatistics();
}