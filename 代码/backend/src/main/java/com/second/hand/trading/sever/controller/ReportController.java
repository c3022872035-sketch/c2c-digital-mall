package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.ReportModel;
import com.second.hand.trading.server.service.ReportService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/add")
    public ResultVo addReport(HttpServletRequest request,
                              @RequestBody ReportModel reportModel) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        reportModel.setUserId(userId);
        reportModel.setCreateTime(new Date());
        reportModel.setStatus((byte) 0);

        if (reportService.addReport(reportModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}