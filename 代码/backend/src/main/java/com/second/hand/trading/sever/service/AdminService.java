package com.second.hand.trading.server.service;

import com.second.hand.trading.server.model.AdminModel;
import com.second.hand.trading.server.vo.PageVo;

public interface AdminService {

    AdminModel login(String accountNumber, String adminPassword);

    PageVo<AdminModel> getAdminList(String searchValue, int page, int nums);

    boolean addAdmin(AdminModel adminModel);

    boolean updateAdmin(AdminModel adminModel);

    boolean deleteAdmin(Long id);
}
