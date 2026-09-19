package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @PostMapping("sign-in")
    public ResultVo signIn(@RequestBody  UserModel userModel) {
        System.out.println(userModel);
        userModel.setSignInTime(new Timestamp(System.currentTimeMillis()));
        if (userModel.getAvatar() == null || "".equals(userModel.getAvatar())) {
            userModel.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }
        if (userService.userSignIn(userModel)) {
            return ResultVo.success(userModel);
        }
        return ResultVo.fail(ErrorMsg.REGISTER_ERROR);
    }

    @RequestMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotEmpty @NotNull String accountNumber,
                          @RequestParam("userPassword") @NotEmpty @NotNull String userPassword,
                          HttpServletResponse response) {
        UserModel userModel = userService.userLogin(accountNumber, userPassword);

        if (null == userModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }

        if(userModel.getUserStatus() != null && userModel.getUserStatus().equals((byte) 1)){
            return ResultVo.fail(ErrorMsg.ACCOUNT_Ban);
        }

        // 生成 Token
        String token = JwtUtil.createToken(userModel.getId(), userModel.getNickname());
        userModel.setToken(token);

        return ResultVo.success(userModel);
    }

    /**
     * 退出登录 (前端清除 localStorage 即可，后端其实可以不做操作，或者是把 token 加入黑名单)
     */
    @RequestMapping("logout")
    public ResultVo logout(HttpServletRequest request) {
        // JWT 无状态，服务端无法强制销毁，直接返回成功，让前端清除 token 即可
        return ResultVo.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("info")
    public ResultVo getOneUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(userService.getUser(userId));
    }

    /**
     * 修改用户公开信息
     */
    @PostMapping("/info")
    public ResultVo updateUserPublicInfo(HttpServletRequest request,
                                         @RequestBody UserModel userModel) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        userModel.setId(userId);
        if (userService.updateUserInfo(userModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 修改密码
     */
    @GetMapping("/password")
    public ResultVo updateUserPassword(HttpServletRequest request,
                                       @RequestParam("oldPassword") @NotEmpty @NotNull String oldPassword,
                                       @RequestParam("newPassword") @NotEmpty @NotNull String newPassword) {
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if (userService.updatePassword(newPassword, oldPassword, userId)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PASSWORD_RESET_ERROR);
    }
}
