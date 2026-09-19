package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.model.IdleItemModel;
import com.second.hand.trading.server.model.OrderModel;
import com.second.hand.trading.server.model.UserModel;
import com.second.hand.trading.server.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final OrderDao orderDao;
    private final IdleItemDao idleItemDao;
    private final UserDao userDao;

    @Value("${userFilePath}")
    private String userFilePath;

    /**
     * 数字资源安全下载接口
     * 支持带密码的压缩包注释注入（指纹追踪）
     */
    @GetMapping("/download")
    public void downloadResource(@RequestParam Long orderId,
                                 @RequestParam String token,
                                 HttpServletResponse response) throws IOException {

        // 1. 身份鉴权与下载权限校验
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            response.sendError(403, "身份验证失败，请重新登录");
            return;
        }

        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        if (order == null || !order.getUserId().equals(userId) || order.getPaymentStatus() != 1) {
            response.sendError(403, "无权下载：订单未找到或尚未支付");
            return;
        }

        IdleItemModel item = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (item == null || item.getResourcePath() == null) {
            response.sendError(404, "资源已下架或源文件不存在");
            return;
        }

        // 2. 定位物理文件
        File fileDir = new File(userFilePath);
        Path sourcePath = Paths.get(fileDir.getAbsolutePath(), item.getResourcePath());
        File sourceFile = sourcePath.toFile();

        if (!sourceFile.exists()) {
            response.sendError(404, "服务器内部错误：文件物理丢失");
            return;
        }

        // 3. 准备追踪信息
        UserModel buyer = userDao.selectByPrimaryKey(userId);
        String buyerName = (buyer != null) ? buyer.getNickname() : "匿名买家";
        String originalName = (item.getOriginalFileName() != null) ? item.getOriginalFileName() : "resource.zip";

        // 构造下载显示的带指纹的文件名
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".zip";
        String nameWithoutExt = originalName.contains(".") ? originalName.substring(0, originalName.lastIndexOf(".")) : originalName;
        String downloadFileName = String.format("%s[C2C专享-%s]%s", nameWithoutExt, buyerName, extension);

        // 构造指纹注释内容
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fingerprint = "========================================\n" +
                "【数字版权追踪 / Copyright Tracking】\n" +
                "========================================\n" +
                "本资源合法授权给用户：" + buyerName + " (ID:" + userId + ")\n" +
                "订单编号：" + order.getOrderNumber() + "\n" +
                "下载时间：" + sdf.format(new Date()) + "\n" +
                "\n" +
                "警告：系统已记录您的专属交易指纹。\n" +
                "请勿将此资源二次分发或转售。经监测发现泄露，\n" +
                "平台将永久封禁关联账号并追究法律责任！\n" +
                "========================================";

        // 4. 利用临时文件与Zip4j进行非侵入式注入
        Path tempFilePath = null;
        try {
            // 检查是否为ZIP格式，非ZIP格式无法注入注释，走普通下载
            if (!originalName.toLowerCase().endsWith(".zip")) {
                performPlainDownload(sourceFile, downloadFileName, response);
                return;
            }

            // 创建临时副本（防止高并发下多个用户修改同一个源文件导致冲突）
            tempFilePath = Files.createTempFile("dl_fingerprint_" + order.getOrderNumber() + "_", ".zip");
            Files.copy(sourcePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 使用 Zip4j 设置注释。注意：此操作只修改 ZIP 的 EOCD 段，不涉及解压，因此支持加密包。
            ZipFile zipFile = new ZipFile(tempFilePath.toFile());
            zipFile.setCharset(java.nio.charset.Charset.forName("GBK"));
            zipFile.setComment(fingerprint);

            // 5. 设置响应头并流式传输给浏览器
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(downloadFileName, "UTF-8"));
            response.setContentLengthLong(tempFilePath.toFile().length());

            // 将处理好的副本写入响应输出流
            Files.copy(tempFilePath, response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            // 如果在 Zip4j 环节出错了（例如文件格式损坏），降级执行普通下载，确保用户能拿到文件
            if (!response.isCommitted()) {
                performPlainDownload(sourceFile, downloadFileName, response);
            }
        } finally {
            // 6. 释放资源：删除服务器上的临时副本
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * 辅助方法：执行普通无指纹下载（降级方案/非ZIP文件使用）
     */
    private void performPlainDownload(File file, String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int b;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
            os.flush();
        }
    }
}