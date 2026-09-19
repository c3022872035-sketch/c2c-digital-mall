package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.OrderAddressModel;
import com.second.hand.trading.server.service.OrderAddressService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order-address")
@RequiredArgsConstructor
public class OrderAddressController {

    private final OrderAddressService orderAddressService;

    @PostMapping("/add")
    public ResultVo addOrderAddress(HttpServletRequest request,
                                    @RequestBody OrderAddressModel orderAddressModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(orderAddressService.addOrderAddress(orderAddressModel));
    }

    @PostMapping("/update")
    public ResultVo updateOrderAddress(HttpServletRequest request,
                                       @RequestBody OrderAddressModel orderAddressModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(orderAddressService.updateOrderAddress(orderAddressModel)){
            return ResultVo.success(orderAddressModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/info")
    public ResultVo getOrderAddress(HttpServletRequest request,
                                    @RequestParam Long orderId){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(orderAddressService.getOrderAddress(orderId));
    }
}
