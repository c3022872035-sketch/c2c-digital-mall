package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.FavoriteModel;
import com.second.hand.trading.server.service.FavoriteService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add")
    public ResultVo addFavorite(HttpServletRequest request,
                                @RequestBody FavoriteModel favoriteModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        favoriteModel.setUserId(userId);
        favoriteModel.setCreateTime(new Date());
        if(favoriteService.addFavorite(favoriteModel)){
            return ResultVo.success(favoriteModel.getId());
        }
        return ResultVo.fail(ErrorMsg.FAVORITE_EXIT);
    }

    @GetMapping("/delete")
    public ResultVo deleteFavorite(HttpServletRequest request,
                                   @RequestParam Long id){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(favoriteService.deleteFavorite(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/check")
    public ResultVo checkFavorite(HttpServletRequest request,
                                  @RequestParam Long idleId){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return ResultVo.success(false); // 未登录算作未收藏
        }
        return ResultVo.success(favoriteService.isFavorite(userId, idleId));
    }

    @GetMapping("/my")
    public ResultVo getMyFavorite(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(favoriteService.getAllFavorite(userId));
    }
}
