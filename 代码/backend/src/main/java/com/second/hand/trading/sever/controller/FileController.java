package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.FileService;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.utils.FileHashUtil;
import com.second.hand.trading.server.utils.IdFactoryUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
@RequiredArgsConstructor
public class FileController {

    @Value("${userFilePath}")
    private String userFilePath;

    @Value("${baseUrl}")
    private String baseUrl;

    private final FileService fileService;

    private final IdleItemService idleItemService;

    /**
     * 图片上传接口 (公开)
     * 返回完整的图片访问URL，用于前端回显
     */
    @PostMapping("/file")
    public ResultVo uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        // 生成唯一文件名
        String uuid = "file" + IdFactoryUtil.getFileId();
        String fileName = uuid + multipartFile.getOriginalFilename();
        try {
            if (fileService.uploadFile(multipartFile, fileName)) {
                // 返回完整的 HTTP URL
                return ResultVo.success(baseUrl + "/image?imageName=" + fileName);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
        return ResultVo.fail(ErrorMsg.FILE_UPLOAD_ERROR);
    }

    /**
     * 数字资源上传接口 (公开，但建议配合前端限制)
     * 返回文件名 (resource_path)，用于存入数据库
     * release.vue 和 me.vue 中调用的是这个接口
     */
    @PostMapping("/file/uploadResource")
    public ResultVo uploadResource(@RequestParam("file") MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        boolean isZip = originalFilename != null && originalFilename.toLowerCase().endsWith(".zip");

        // 1. 必须先存为临时文件，因为计算哈希和剥离注释需要操作物理文件
        File fileDir = new File(userFilePath);
        String tempName = "temp_" + System.currentTimeMillis() + originalFilename;
        File tempFile = new File(fileDir.getAbsolutePath() + File.separator + tempName);

        try {
            multipartFile.transferTo(tempFile);

            // 2. 计算“洗白”后的哈希值（剥离注释）
            String fileHash = FileHashUtil.getCleanSHA256(tempFile, isZip);

            // 3. 查重校验
            if (idleItemService.checkFileHashExists(fileHash)) {
                tempFile.delete(); // 重复了就删掉临时文件
                return new ResultVo(0, "资源库中已存在该原始资源（包含指纹变体），请勿重复发布！", null);
            }

            // 4. 生成最终正式文件名
            String uuid = "res-" + System.currentTimeMillis();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String finalFileName = uuid + suffix;
            File finalFile = new File(fileDir.getAbsolutePath() + File.separator + finalFileName);

            // 5. 将临时文件重命名为正式文件
            tempFile.renameTo(finalFile);

            // 6. 返回结果
            java.util.Map<String, String> res = new java.util.HashMap<>();
            res.put("fileName", finalFileName);
            res.put("fileHash", fileHash);
            return ResultVo.success(res);

        } catch (IOException e) {
            if (tempFile.exists()) tempFile.delete();
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    /**
     * 图片查看/下载接口 (公开)
     * 用于前端 <img> 标签展示图片
     */
    @GetMapping("/image")
    public void getImage(@RequestParam("imageName") String imageName,
                         HttpServletResponse response) throws IOException {
        File fileDir = new File(userFilePath);
        File image = new File(fileDir.getAbsolutePath() + "/" + imageName);
        if (image.exists()) {
            FileInputStream fileInputStream = new FileInputStream(image);
            byte[] bytes = new byte[fileInputStream.available()];
            if (fileInputStream.read(bytes) > 0) {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.close();
            }
            fileInputStream.close();
        } else {
            response.setStatus(404);
        }
    }

    /**
     * 管理员专用文件下载接口 (需权限校验)
     * 用于后台审核管理中下载资源文件
     *
     * @param fileName 存储在服务器上的文件名 (resource_path)
     * @param session 用于验证管理员身份
     */
    @GetMapping("/file/download")
    public void downloadFile(@RequestParam("fileName") String fileName,
                             HttpServletResponse response,
                             HttpSession session) throws IOException {

        // 【安全校验】只有管理员 Session 存在才能下载
        if (session.getAttribute("admin") == null) {
            response.sendError(403, "Access Denied: 非管理员禁止访问此接口");
            return;
        }

        File fileDir = new File(userFilePath);
        File file = new File(fileDir.getAbsolutePath() + "/" + fileName);

        if (file.exists()) {
            // 设置强制下载响应头
            response.setContentType("application/octet-stream");
            // 对文件名编码，防止中文乱码
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            if (fileInputStream.read(bytes) > 0) {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.close();
            }
            fileInputStream.close();
        } else {
            response.sendError(404, "File not found: 文件物理丢失");
        }
    }
}