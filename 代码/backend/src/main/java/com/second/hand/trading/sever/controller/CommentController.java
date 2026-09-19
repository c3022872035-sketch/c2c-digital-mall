package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.CommentModel;
import com.second.hand.trading.server.service.CommentService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public ResultVo addComment(HttpServletRequest request,
                               @RequestBody CommentModel commentModel) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        commentModel.setBuyerId(userId);
        commentModel.setCreateTime(new Date());

        if (commentService.addComment(commentModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.REPEAT_COMMIT_ERROR);
    }

    @GetMapping("/list")
    public ResultVo list(@RequestParam Long idleId) {
        return ResultVo.success(commentService.listComment(idleId));
    }


}