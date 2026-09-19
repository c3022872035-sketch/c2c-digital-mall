package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.AddressModel;
import com.second.hand.trading.server.service.AddressService;
import com.second.hand.trading.server.utils.JwtUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/info")
    public ResultVo getAddress(HttpServletRequest request,
                               @RequestParam(value = "id",required = false) Long id){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        if(null==id){
            return ResultVo.success(addressService.getAddressByUser(userId));
        }else {
            return ResultVo.success(addressService.getAddressById(id, userId));
        }
    }

    @PostMapping("/add")
    public ResultVo addAddress(HttpServletRequest request,
                               @RequestBody AddressModel addressModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        addressModel.setUserId(userId);
        if(addressService.addAddress(addressModel)){
            return ResultVo.success(addressModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @PostMapping("/update")
    public ResultVo updateAddress(HttpServletRequest request,
                                  @RequestBody AddressModel addressModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        addressModel.setUserId(userId);
        if(addressService.updateAddress(addressModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @PostMapping("/delete")
    public ResultVo deleteAddress(HttpServletRequest request,
                                  @RequestBody AddressModel addressModel){
        String token = request.getHeader("token");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        addressModel.setUserId(userId);
        if(addressService.deleteAddress(addressModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
