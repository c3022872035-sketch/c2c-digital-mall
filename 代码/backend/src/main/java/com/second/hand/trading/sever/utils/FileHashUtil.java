package com.second.hand.trading.server.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

public class FileHashUtil {

    public static String getCleanSHA256(File file, boolean isZip) {
        try {
            // 如果是 ZIP，计算其内部结构哈希，无视外部注释干扰
            if (isZip) {
                return getZipInternalStructureHash(file);
            }
            // 如果不是 ZIP（如普通的图片、文档），按原样计算全文件哈希
            return getFileSHA256(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 【核心】计算 ZIP 内部结构哈希
     * 逻辑：提取所有文件的名称、大小、CRC，排序后组合哈希
     */
    private static String getZipInternalStructureHash(File file) throws Exception {
        ZipFile zipFile = new ZipFile(file);
        zipFile.setCharset(Charset.forName("GBK"));

        // 1. 获取压缩包内所有文件的头信息
        List<FileHeader> headers = zipFile.getFileHeaders();

        // 2. 将每个文件的特征（名字+大小+CRC）提取出来，并按文件名排序（防止顺序干扰）
        String structureString = headers.stream()
                .map(h -> h.getFileName() + "_" + h.getUncompressedSize() + "_" + h.getCrc())
                .sorted()
                .collect(Collectors.joining("|"));

        // 3. 对这个“结构特征字符串”计算 SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(structureString.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return "STRUCT-" + hexString.toString(); // 增加前缀以区分普通哈希
    }

    /**
     * 常规全文件哈希计算
     */
    private static String getFileSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}