package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.ReportDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.service.ReportService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportDao reportDao;
    @Resource
    private UserDao userDao;
    @Resource
    private IdleItemDao idleItemDao;

    @Override
    public boolean addReport(ReportModel reportModel) {
        return reportDao.insert(reportModel) == 1;
    }

    @Override
    public int countPending() {
        return reportDao.countPending();
    }

    @Override
    public PageVo<ReportModel> getReportList(Integer status, String searchValue, int page, int nums) {
        // 1. 传递 searchValue 给 Dao
        List<ReportModel> list = reportDao.getReportList(status, searchValue, (page - 1) * nums, nums);

        // 2. 循环填充关联信息（保持原有逻辑）
        for (ReportModel report : list) {
            report.setUser(userDao.selectByPrimaryKey(report.getUserId()));
            report.setReportedUser(userDao.selectByPrimaryKey(report.getReportedUserId()));
            report.setIdleItem(idleItemDao.selectByPrimaryKey(report.getIdleId()));
        }

        // 3. 传递 searchValue 给 Count
        int count = reportDao.countReport(status, searchValue);
        return new PageVo<>(list, count);
    }

    @Override
    public boolean handleReport(Long id) {
        return reportDao.updateStatus(id, (byte) 1) == 1;
    }

}