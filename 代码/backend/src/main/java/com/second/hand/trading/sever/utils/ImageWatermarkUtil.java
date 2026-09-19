package com.second.hand.trading.server.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageWatermarkUtil {
    public static boolean addWatermark(String srcPath, String targetPath, String text) {
        try {
            File srcFile = new File(srcPath);
            if (!srcFile.exists()) {
                System.err.println(">>> 水印失败：原文件不存在 " + srcPath);
                return false;
            }

            // 1. 读取原图
            BufferedImage srcImg = ImageIO.read(srcFile);
            if (srcImg == null) {
                System.err.println(">>> 水印失败：无法解析图片内容，可能是不支持的编码(如CMYK或损坏): " + srcPath);
                return false;
            }

            // 2. 获取宽高
            int width = srcImg.getWidth();
            int height = srcImg.getHeight();

            // 3. 创建兼容性画板 (TYPE_INT_RGB 对 JPG 兼容性最好)
            BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();

            // 设置绘图抗锯齿，使水印更清晰
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 4. 绘制背景（白色底，防止原图边缘透明变黑）
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // 5. 将原图绘制到画板上
            g.drawImage(srcImg, 0, 0, width, height, null);

            // 6. 动态计算水印字体大小
            int fontSize = Math.max(20, width / 20);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));

            // 7. 计算水印坐标（右下角）
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            int x = width - textWidth - 30;
            int y = height - 30;

            // 8. 绘制双层水印（黑色半透明底 + 白色半透明面，确保任何背景都可见）
            g.setColor(new Color(0, 0, 0, 100)); // 阴影
            g.drawString(text, x + 1, y + 1);
            g.setColor(new Color(255, 255, 255, 180)); // 文字
            g.drawString(text, x, y);

            g.dispose();

            // 9. 执行写入
            String format = "jpg"; // 默认格式
            if (srcPath.toLowerCase().endsWith(".png")) format = "png";

            File targetFile = new File(targetPath);
            return ImageIO.write(bufImg, format, targetFile);

        } catch (Exception e) {
            System.err.println(">>> 发生异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}