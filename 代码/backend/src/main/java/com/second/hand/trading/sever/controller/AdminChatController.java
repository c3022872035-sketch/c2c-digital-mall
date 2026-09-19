package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.MessageModel;
import com.second.hand.trading.server.service.MessageService;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController
@RequestMapping("admin/chat")
@RequiredArgsConstructor

public class AdminChatController {

    private final MessageService messageService;

    // 定义虚拟用户的ID常量
    private static final Long ADMIN_VIRTUAL_USER_ID = 88L;

    /**
     * 管理员获取与某用户的聊天记录
     */
    @GetMapping("/history")
    public ResultVo getChatHistory(HttpSession session,
                                   @RequestParam Long targetUserId,
                                   @RequestParam(required = false, defaultValue = "0") Long idleId) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 强制使用虚拟ID获取记录
        return ResultVo.success(messageService.getChatHistory(ADMIN_VIRTUAL_USER_ID, targetUserId, idleId));
    }

    /**
     * 管理员发送消息
     */
    @PostMapping("/send")
    public ResultVo sendMessage(HttpSession session,
                                @RequestBody MessageModel messageModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        messageModel.setUserId(ADMIN_VIRTUAL_USER_ID);
        messageModel.setCreateTime(new Date());
        messageModel.setMessageStatus((byte)0);

        if (messageService.addMessage(messageModel)) {
            return ResultVo.success(messageModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}