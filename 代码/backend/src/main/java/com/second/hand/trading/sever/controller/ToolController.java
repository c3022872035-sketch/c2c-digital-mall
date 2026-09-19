package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.utils.BaiduCopyrightUtil;
import com.second.hand.trading.server.vo.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/tool")
public class ToolController {

    @Value("${userFilePath}")
    private String userFilePath;

    @GetMapping("/check-copyright")
    public ResultVo checkCopyright(@RequestParam("imgUrl") String imgUrl) {
        // 1. 从URL中解析出文件名
        if (!imgUrl.contains("imageName=")) {
            return ResultVo.success("图片路径无效，跳过检测");
        }

        String fileName = imgUrl.substring(imgUrl.lastIndexOf("=") + 1);

        // 2. 获取本地文件的绝对路径
        File fileDir = new File(userFilePath);
        String localPath = fileDir.getAbsolutePath() + "/" + fileName;

        // 3. 调用百度AI进行检测
        String riskInfo = BaiduCopyrightUtil.checkImageCopyright(localPath);

        if (riskInfo != null) {
            ResultVo result = new ResultVo();
            result.setStatus_code(0);
            result.setMsg("版权风险警告：" + riskInfo); // 把具体原因设为 msg
            return result;
        }

        return ResultVo.success("版权检测通过");
    }
}