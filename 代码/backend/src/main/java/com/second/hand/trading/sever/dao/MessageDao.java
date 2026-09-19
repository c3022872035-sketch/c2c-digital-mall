package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageDao {
    int deleteByPrimaryKey(Long id);

    int insert(MessageModel record);

    int insertSelective(MessageModel record);

    MessageModel selectByPrimaryKey(Long id);

    List<MessageModel> getMyMessage(Long userId);

    List<MessageModel> getIdleMessage(Long idleId);

    int updateByPrimaryKeySelective(MessageModel record);

    int updateByPrimaryKey(MessageModel record);

    List<MessageModel> getChatHistory(@Param("userId") Long userId,
                                      @Param("targetUserId") Long targetUserId,
                                      @Param("idleId") Long idleId);

    List<MessageModel> getAllMyRelatedMessages(Long userId);

    // 统计未读
    int getUnreadCount(Long userId);

    int countUnread(@Param("userId") Long userId,
                    @Param("targetId") Long targetId,
                    @Param("idleId") Long idleId);
    // 标记已读
    void updateMessageStatus(@Param("userId") Long userId,
                             @Param("targetUserId") Long targetUserId,
                             @Param("idleId") Long idleId);
}