package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.IdleItemHistoryModel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface IdleItemHistoryDao {
    int insert(IdleItemHistoryModel record);

    // 根据商品ID查询历史记录，按时间倒序
    List<IdleItemHistoryModel> listByIdleId(Long idleId);
}