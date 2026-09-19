package com.second.hand.trading.server.utils;

import com.baidu.aip.imageclassify.AipImageClassify;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class BaiduCopyrightUtil {
    public static final String APP_ID = "7278397";
    public static final String API_KEY = "4lbhqOdlAku19NmRh3jTXMfo";
    public static final String SECRET_KEY = "4rkxeOCYbJJ0yc6XIZMCKltVlZvURsuL";

    private static AipImageClassify client = null;

    /**
     * 获取百度AI客户端单例
     *在此处增加了超时时间设置，解决 SDK108 连接超时问题
     */
    private static AipImageClassify getClient() {
        if (client == null) {
            client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);

            client.setConnectionTimeoutInMillis(20000);
            client.setSocketTimeoutInMillis(60000);
        }
        return client;
    }

    /**
     * 检查图片版权风险
     * @param localPath 本地文件绝对路径
     * @return 风险描述字符串，如果没有风险返回 null
     */
    public static String checkImageCopyright(String localPath) {
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("baike_num", "0");

            // 调用百度AI "通用物体和场景识别高级版"
            JSONObject res = getClient().advancedGeneral(localPath, options);
            System.out.println("======= 百度AI识别结果开始 =======");
            System.out.println(res.toString(2));
            System.out.println("======= 百度AI识别结果结束 =======");

            // 检查是否返回了错误信息 (如 SDK108 等)
            if (res.has("error_code")) {
                System.out.println("百度AI调用出错: " + res.getString("error_msg"));
                return null;
            }

            // 解析返回结果
            if (res.has("result")) {
                JSONArray results = res.getJSONArray("result");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    String keyword = item.getString("keyword");
                    String root = item.getString("root"); // 根类别

                    // 1. 电商平台直接相关 (包含阿里巴巴、1688等)
                    if (keyword.contains("淘宝") || keyword.contains("天猫") ||
                            keyword.contains("京东") || keyword.contains("拼多多") ||
                            keyword.contains("亚马逊") || keyword.contains("闲鱼") ||
                            keyword.contains("阿里巴巴") || keyword.contains("1688") ||
                            keyword.contains("唯品会") || keyword.contains("苏宁")) {
                        return "检测到电商平台元素：" + keyword;
                    }

                    // 2. 资源/素材/设计相关 (针对PPT模版、源码、设计图等数字商品)
                    if (keyword.contains("素材") || keyword.contains("模板") ||
                            keyword.contains("模版") || keyword.contains("幻灯片") ||
                            keyword.contains("PPT") || keyword.contains("样机") ||
                            keyword.contains("设计图") || keyword.contains("效果图") ||
                            keyword.contains("手抄报") || keyword.contains("文档")) {
                        return "检测到疑似素材资源售卖：" + keyword;
                    }

                    // 3. 营销/广告相关 (盗版商品通常直接使用精美的营销图)
                    if (keyword.contains("海报") || keyword.contains("广告") ||
                            keyword.contains("宣传单") || keyword.contains("优惠券") ||
                            keyword.contains("促销") || keyword.contains("易拉宝")) {
                        return "检测到疑似营销广告图：" + keyword;
                    }

                    // 4. 截图/网页判断 (防止直接截取他人的商品详情页)
                    if ((keyword.contains("网页") || keyword.contains("截图") || keyword.contains("屏幕")) &&
                            (root.contains("商品") || root.contains("互联网"))) {
                        return "检测到疑似网页截图：" + keyword;
                    }
                }
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "版权检测服务连接超时，请检查网络或重试";
        }
    }
}