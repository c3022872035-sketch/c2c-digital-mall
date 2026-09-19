package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("idle")
@RequiredArgsConstructor
public class IdleItemController {

    private final IdleItemService idleItemService;

    @PostMapping("add")
    public ResultVo addIdleItem(HttpServletRequest request,
                                @RequestBody IdleItemModel idleItemModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        idleItemModel.setUserId(userId);
        idleItemModel.setIdleStatus((byte) 3); // 默认待审核
        idleItemModel.setReleaseTime(new Date());
        if(idleItemService.addIdleItem(idleItemModel)){
            return ResultVo.success(idleItemModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("info")
    public ResultVo getIdleItem(@RequestParam Long id){
        return ResultVo.success(idleItemService.getIdleItem(id));
    }

    @GetMapping("all")
    public ResultVo getAllIdleItem(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(idleItemService.getAllIdleItem(userId));
    }

    @GetMapping("find")
    public ResultVo findIdleItem(@RequestParam(value = "findValue",required = false) String findValue,
                                 @RequestParam(value = "sortType", required = false, defaultValue = "0") Integer sortType,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        if(null==findValue) findValue="";
        int p = (page==null || page<1) ? 1 : page;
        int n = (nums==null || nums<1) ? 8 : nums;

        return ResultVo.success(idleItemService.findIdleItem(findValue, sortType, p, n));
    }

    @GetMapping("label")
    public ResultVo findIdleItemByLabel(@RequestParam(value = "idleLabel") Integer idleLabel,
                                        @RequestParam(value = "sortType", required = false, defaultValue = "0") Integer sortType,
                                        @RequestParam(value = "page",required = false) Integer page,
                                        @RequestParam(value = "nums",required = false) Integer nums){
        int p = (page==null || page<1) ? 1 : page;
        int n = (nums==null || nums<1) ? 8 : nums;

        return ResultVo.success(idleItemService.findIdleItemByLabel(idleLabel, sortType, p, n));
    }

    @PostMapping("update")
    public ResultVo updateIdleItem(HttpServletRequest request,
                                   @RequestBody IdleItemModel idleItemModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        idleItemModel.setUserId(userId);
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 历史记录接口
    @GetMapping("history")
    public ResultVo getIdleHistory(HttpServletRequest request,
                                   @RequestParam Long idleId) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        IdleItemModel item = idleItemService.getIdleItem(idleId);
        if (item == null) return ResultVo.fail(ErrorMsg.PARAM_ERROR);

        if (!item.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
        return ResultVo.success(idleItemService.getHistoryList(idleId));
    }

    @GetMapping("recommend")
    public ResultVo getRecommend(@RequestParam(required = false) Long currentId,
                                 @RequestParam(required = false) Integer label){
        if (currentId != null && label != null) {
            // 详情页：返回 4 个同类推荐
            return ResultVo.success(idleItemService.getRelatedItems(label, currentId, 4));
        } else {
            // 首页：返回 4 个热门推荐
            return ResultVo.success(idleItemService.getHotItems(4));
        }
    }
}
