package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.vo.PageVo;

public interface ReportService {
    // 用户提交投诉
    boolean addReport(ReportModel reportModel);

    // 管理员：获取待处理数量
    int countPending();

    // 管理员：获取投诉列表
    PageVo<ReportModel> getReportList(Integer status, String searchValue, int page, int nums);

    // 管理员：处理投诉
    boolean handleReport(Long id);

}