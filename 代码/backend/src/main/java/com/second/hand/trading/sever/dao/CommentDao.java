package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.CommentModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {
    int insert(CommentModel c);

    List<CommentModel> listByIdle(Long idleId);

    CommentModel selectByPrimaryKey(Long id);

    int countByOrder(Long orderId);

    int deleteByPrimaryKey(Long id);
    // 分页获取所有评价
    List<CommentModel> getAllComments(@Param("searchValue") String searchValue, @Param("begin") int begin, @Param("nums") int nums);
    // 统计总数
    int countAllComments(@Param("searchValue") String searchValue);

    int updateByPrimaryKeySelective(CommentModel record);


}