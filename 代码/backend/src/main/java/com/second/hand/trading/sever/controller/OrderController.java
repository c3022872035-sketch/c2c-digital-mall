package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.utils.IdFactoryUtil;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.utils.OrderTaskHandler;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/add")
    public ResultVo addOrder(HttpServletRequest request,
                             @RequestBody OrderModel orderModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(OrderTaskHandler.orderService==null){
            OrderTaskHandler.orderService=orderService;
        }
        orderModel.setOrderNumber(IdFactoryUtil.getOrderId());
        orderModel.setCreateTime(new Date());
        orderModel.setUserId(userId);
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte)0);
        if(orderService.addOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/info")
    public ResultVo getOrderInfo(HttpServletRequest request,
                                 @RequestParam Long id){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        OrderModel orderModel=orderService.getOrder(id);
        if(orderModel.getUserId().equals(userId)||
                orderModel.getIdleItem().getUserId().equals(userId)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @PostMapping("/update")
    public ResultVo updateOrder(HttpServletRequest request,
                                @RequestBody OrderModel orderModel,
                                @RequestParam(value = "price",required = false) String price){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(orderModel.getPaymentStatus()!=null&&orderModel.getPaymentStatus().equals((byte) 1)){
            orderModel.setPaymentTime(new Date());
        }
        if(orderService.updateOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/my")
    public ResultVo getMyOrder(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(orderService.getMyOrder(userId));
    }

    @GetMapping("/my-sold")
    public ResultVo getMySoldIdle(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        return ResultVo.success(orderService.getMySoldIdle(userId));
    }
}
