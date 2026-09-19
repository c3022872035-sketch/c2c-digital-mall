package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.IdleItemHistoryModel;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.vo.PageVo;

import java.util.List;
import java.util.Map;

public interface IdleItemService {

    boolean addIdleItem(IdleItemModel idleItemModel);

    IdleItemModel getIdleItem(Long id);

    List<IdleItemModel> getAllIdleItem(Long userId);

    PageVo<IdleItemModel> findIdleItem(String findValue, Integer sortType, int page, int nums);

    PageVo<IdleItemModel> findIdleItemForAdmin(String findValue, int page, int nums);

    PageVo<IdleItemModel> findIdleItem1(String findValue, int status, int page, int nums);

    PageVo<IdleItemModel> findIdleItemByLabel(int idleLabel, Integer sortType, int page, int nums);

    boolean updateIdleItem(IdleItemModel idleItemModel);

    PageVo<IdleItemModel> adminGetIdleList(int status, int page, int nums);

    List<Map<String, Object>> getCategoryStatistics();

    PageVo<IdleItemModel> getAuditList(Integer status, String searchValue, int page, int nums);

    List<IdleItemHistoryModel> getHistoryList(Long idleId);

    boolean checkFileHashExists(String fileHash);

    int repairFileHash(String userFilePath);

    void syncAllItemStats();

    /**
     * 获取热门推荐商品
     * @param nums 获取数量
     */
    List<IdleItemModel> getHotItems(int nums);

    /**
     * 获取同类推荐商品
     * @param label 分类ID
     * @param currentId 当前商品ID（用于排除自身）
     * @param nums 获取数量
     */
    List<IdleItemModel> getRelatedItems(int label, long currentId, int nums);
}