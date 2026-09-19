package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.OrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
public interface OrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(OrderModel record);

    int insertSelective(OrderModel record);

    OrderModel selectByPrimaryKey(Long id);

    List<OrderModel> getMyOrder(Long userId);

    List<OrderModel> getOrderByNumber(@Param("searchValue") String searchValue, @Param("begin") int begin, @Param("nums") int nums);

    int countAllOrder(@Param("searchValue") String searchValue);

    List<OrderModel> findOrderByIdleIdList(List<Long> idleIdList);

    int updateByPrimaryKeySelective(OrderModel record);

    int updateCommentFlag(@Param("id") Long id, @Param("flag") int flag);

    BigDecimal getTotalCommissionAmount();

    BigDecimal getTotalOrderAmount();

    int getOrderCountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    BigDecimal getOrderAmountByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}