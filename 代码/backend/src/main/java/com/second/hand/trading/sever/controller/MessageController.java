package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.MessageModel;
import com.second.hand.trading.server.service.MessageService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResultVo sendMessage(HttpServletRequest request,
                                @RequestBody MessageModel messageModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        messageModel.setUserId(userId);
        messageModel.setCreateTime(new Date());
        messageModel.setMessageStatus((byte)0);
        if(messageService.addMessage(messageModel)){
            return ResultVo.success(messageModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/info")
    public ResultVo getMessage(@RequestParam Long id){
        return ResultVo.success(messageService.getMessage(id));
    }

    @GetMapping("/idle")
    public ResultVo getAllIdleMessage(@RequestParam Long idleId){
        return ResultVo.success(messageService.getAllIdleMessage(idleId));
    }

    @GetMapping("/my")
    public ResultVo getAllMyMessage(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(messageService.getAllMyMessage(userId));
    }

    @GetMapping("/delete")
    public ResultVo deleteMessage(HttpServletRequest request,
                                  @RequestParam Long id){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(messageService.deleteMessage(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/unread/count")
    public ResultVo getUnreadCount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(messageService.getUnreadCount(userId));
    }

    @GetMapping("/chat/history")
    public ResultVo getChatHistory(HttpServletRequest request,
                                   @RequestParam Long targetUserId,
                                   @RequestParam Long idleId) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(messageService.getChatHistory(userId, targetUserId, idleId));
    }

    @GetMapping("/chat/list")
    public ResultVo getChatList(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(messageService.getChatList(userId));
    }
}
