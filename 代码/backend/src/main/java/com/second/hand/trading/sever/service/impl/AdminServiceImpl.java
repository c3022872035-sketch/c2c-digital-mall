package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AdminDao;
import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.service.AdminService;
import com.second.hand.trading.server.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminDao adminDao;

    public AdminModel login(String accountNumber, String adminPassword){
        return adminDao.login(accountNumber,adminPassword);
    }

    @Override
    public PageVo<AdminModel> getAdminList(String searchValue, int page, int nums) {
        List<AdminModel> list = adminDao.findAdmin(searchValue, (page - 1) * nums, nums);
        int count = adminDao.countAdmin(searchValue);
        return new PageVo<>(list, count);
    }
    public boolean addAdmin(AdminModel adminModel){
        return adminDao.insert(adminModel)==1;
    }

    @Override
    public boolean updateAdmin(AdminModel adminModel) {
        return adminDao.updateByPrimaryKeySelective(adminModel) == 1;
    }

    @Override
    public boolean deleteAdmin(Long id) {
        return adminDao.deleteByPrimaryKey(id) == 1;
    }
}
