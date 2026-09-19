package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.model.IdleItemModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IdleItemDao {
    int deleteByPrimaryKey(Long id);

    int insert(IdleItemModel record);

    int insertSelective(IdleItemModel record);

    IdleItemModel selectByPrimaryKey(Long id);

    List<IdleItemModel> getAllIdleItem(Long userId);

    int countIdleItem(String findValue);

    int countIdleItemByLabel(@Param("idleLabel") int idleLabel);

    int countIdleItemByStatus(int status);

    List<IdleItemModel> findIdleItem(@Param("findValue") String findValue, @Param("begin") int begin, @Param("nums") int nums);

    // 后台查询上线商品
    List<IdleItemModel> findIdleItemForAdmin(@Param("findValue") String findValue, @Param("begin") int begin, @Param("nums") int nums);

    // 后台统计上线商品
    int countIdleItemForAdmin(@Param("findValue") String findValue);

    List<IdleItemModel> findIdleItem1(@Param("findValue") String findValue, @Param("status") int status, @Param("begin") int begin, @Param("nums") int nums);

    List<IdleItemModel> findIdleItemByLabel(int idleLabel, int begin, int nums);

    List<IdleItemModel> getIdleItemByStatus(int status, int begin, int nums);

    int updateByPrimaryKeySelective(IdleItemModel record);

    int updateByPrimaryKey(IdleItemModel record);

    List<IdleItemModel> findIdleByList(List<Long> idList);

    int updateCommentStatus(@Param("idleId") Long idleId, @Param("status") Byte status);

    /**
     * 统计各个分类的商品数量
     * @return 包含分类ID和对应数量的列表
     */
    List<Map<String, Object>> countItemsByCategory();

    /**
     * 获取审核列表
     * @param status 状态码 (3:待审核, 1:通过, 4:驳回)
     * @param begin 分页起始
     * @param nums 每页数量
     */
    List<IdleItemModel> getAuditList(@Param("status") Integer status, @Param("searchValue") String searchValue, @Param("begin") int begin, @Param("nums") int nums);
    /**
     * 统计审核列表数量
     */
    int countAuditList(@Param("status") Integer status, @Param("searchValue") String searchValue);

    // 增加销量
    int increaseSales(@Param("id") Long id);

    // 更新评分
    int updateRating(@Param("id") Long id, @Param("rating") Double rating);

    // 增加 sortType
    List<IdleItemModel> findIdleItem(@Param("findValue") String findValue,
                                     @Param("sortType") Integer sortType,
                                     @Param("begin") int begin,
                                     @Param("nums") int nums);

    // 增加 sortType
    List<IdleItemModel> findIdleItemByLabel(@Param("idleLabel") int idleLabel,
                                            @Param("sortType") Integer sortType,
                                            @Param("begin") int begin,
                                            @Param("nums") int nums);

    int countByFileHash(@Param("fileHash") String fileHash);

    List<IdleItemModel> findItemsMissingHash();

    int syncSalesCount();

    int syncAvgRating();

    // 获取全局热门推荐
    List<IdleItemModel> findHotRecommendations(@Param("nums") int nums);

    // 获取同类相关推荐
    List<IdleItemModel> findRelatedRecommendations(@Param("idleLabel") Integer idleLabel,
                                                   @Param("currentId") Long currentId,
                                                   @Param("nums") int nums);

}