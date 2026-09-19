/*
 Navicat Premium Data Transfer

 Source Server         : LGT
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : second_hand_trading

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 20/05/2026 01:41:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sh_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_address`;
CREATE TABLE `sh_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人姓名',
  `consignee_phone` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人手机号',
  `default_flag` tinyint NOT NULL COMMENT '是否默认地址',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_address
-- ----------------------------
INSERT INTO `sh_address` VALUES (36, '小张', '12345678910', 1, 40);
INSERT INTO `sh_address` VALUES (37, '小新', '98765432110', 0, 36);
INSERT INTO `sh_address` VALUES (38, '小伟', '18713790195', 1, 37);
INSERT INTO `sh_address` VALUES (39, '美美', '15263698569', 1, 38);
INSERT INTO `sh_address` VALUES (40, '娜娜', '15896565656', 1, 39);
INSERT INTO `sh_address` VALUES (42, '小新', '98765432110', 1, 36);
INSERT INTO `sh_address` VALUES (43, '1', '1', 1, 43);
INSERT INTO `sh_address` VALUES (44, '小明', '12345678911', 1, 888895);
INSERT INTO `sh_address` VALUES (45, '小明', '12345678911', 1, 888900);
INSERT INTO `sh_address` VALUES (46, '小明', '12345678911', 1, 888902);

-- ----------------------------
-- Table structure for sh_admin
-- ----------------------------
DROP TABLE IF EXISTS `sh_admin`;
CREATE TABLE `sh_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `admin_name` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '管理员名字',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_admin
-- ----------------------------
INSERT INTO `sh_admin` VALUES (1, 'admin', '123456', '超级管理员');
INSERT INTO `sh_admin` VALUES (2, 'admin1', '123456', '超级管理员二号');

-- ----------------------------
-- Table structure for sh_comment
-- ----------------------------
DROP TABLE IF EXISTS `sh_comment`;
CREATE TABLE `sh_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `idle_id` bigint NOT NULL,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `rating` tinyint NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_idle`(`idle_id` ASC) USING BTREE,
  CONSTRAINT `sh_comment_chk_1` CHECK (`rating` between 1 and 5)
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sh_comment
-- ----------------------------
INSERT INTO `sh_comment` VALUES (34, 210, 266, 36, 37, 5, '很好！', '2026-03-13 01:10:12');
INSERT INTO `sh_comment` VALUES (35, 213, 267, 37, 38, 5, '很不错！', '2026-04-13 20:27:52');
INSERT INTO `sh_comment` VALUES (36, 214, 265, 39, 36, 4, '还不错', '2026-04-13 21:21:09');
INSERT INTO `sh_comment` VALUES (37, 218, 272, 36, 38, 5, '很好', '2026-04-17 15:59:08');

-- ----------------------------
-- Table structure for sh_favorite
-- ----------------------------
DROP TABLE IF EXISTS `sh_favorite`;
CREATE TABLE `sh_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
  `create_time` datetime NOT NULL COMMENT '加入收藏的时间',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物主键id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC, `idle_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '收藏信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_favorite
-- ----------------------------
INSERT INTO `sh_favorite` VALUES (71, '2026-04-10 17:33:01', 38, 265);
INSERT INTO `sh_favorite` VALUES (72, '2026-04-13 23:33:04', 36, 267);
INSERT INTO `sh_favorite` VALUES (73, '2026-04-17 15:32:02', 888895, 272);
INSERT INTO `sh_favorite` VALUES (75, '2026-04-17 16:08:44', 36, 272);

-- ----------------------------
-- Table structure for sh_idle_item
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item`;
CREATE TABLE `sh_idle_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `idle_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '商品名称',
  `idle_details` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '详情',
  `picture_list` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图集',
  `idle_price` decimal(10, 2) NOT NULL COMMENT '价格',
  `idle_place` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '下载方式',
  `idle_label` int NOT NULL COMMENT '分类标签',
  `release_time` datetime NOT NULL COMMENT '发布时间',
  `idle_status` tinyint NOT NULL COMMENT '状态（发布1、下架2、删除0）',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `comment_status` tinyint NULL DEFAULT 0 COMMENT '0 待评价 1 已完成',
  `proof_image` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '版权/原创证明图片',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `sales_count` int NULL DEFAULT 0 COMMENT '销量',
  `avg_rating` decimal(2, 1) NULL DEFAULT 0.0 COMMENT '平均评分',
  `resource_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源文件本地存储路径',
  `original_file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源原始文件名',
  `unzip_password` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '解压密码',
  `file_hash` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件哈希值(含STRUCT前缀)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `idx_idle_name`(`idle_name` ASC) USING BTREE,
  INDEX `idx_sales_count`(`sales_count` ASC) USING BTREE,
  INDEX `idx_avg_rating`(`avg_rating` ASC) USING BTREE,
  INDEX `idx_idle_price`(`idle_price` ASC) USING BTREE,
  INDEX `idx_file_hash`(`file_hash` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 283 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '二手商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_idle_item
-- ----------------------------
INSERT INTO `sh_idle_item` VALUES (265, '测试1', '测试2', '[\"http://localhost:8080/image?imageName=file17732997308251002file164569201071610097_wm_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, '2026-03-12 00:50:32', 1, 36, 0, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 15:37:26', 3, 4.0, 'res-1773247803146.zip', '测试1.zip', '123456', 'STRUCT-d0e885c5b77dabf3830a1ec48690d8d2119ec175a779f939f5374025c0ffbd9d');
INSERT INTO `sh_idle_item` VALUES (266, '测试2', '测 试 2', '[\"http://localhost:8080/image?imageName=file17733101185611005file17620969648671042file16475926255961002书234asdf_wm.jfif\"]', 10.00, '仅供学习参考，严禁商用', 1, '2026-03-12 18:09:42', 1, 37, 0, 'http://localhost:8080/image?imageName=file17733101798561006file16469610415851007aefsd.jpg', '2026-03-12 19:56:52', 3, 5.0, 'res-1773310157278.zip', '测试2.zip', '123456', 'STRUCT-6e274be283dd6074a3496e499791c8bc03481a4cf3a1f2ec4756d7a0f64edfd4');
INSERT INTO `sh_idle_item` VALUES (267, 'Java 课程', 'Java 课程', '[\"http://localhost:8080/image?imageName=file17758130646391003file17620966377501038file16495867280841002书6_wm.jfif\"]', 5.00, '仅供学习参考，严禁商用', 3, '2026-04-10 17:30:12', 1, 38, 0, 'http://localhost:8080/image?imageName=file17758134094481004file16468992372591015f.jpg', '2026-04-10 17:33:18', 1, 5.0, 'res-1775813387924.zip', 'Java 课程.zip', '123456', 'STRUCT-1bfd004acf0dd2fde377b6bb8949e4821fd63abae0f8b28de319d46face86a8e');
INSERT INTO `sh_idle_item` VALUES (269, '数据库建模工具特别版', '支持ER图绘制，正向反向工程，数据库开发神器。', '[\"http://localhost:8080/image?imageName=file177633651692310035484b4edba10077a_wm.jpg\"]', 10.00, '仅供学习参考，严禁商用', 2, '2026-04-11 18:48:48', 1, 36, 0, 'http://localhost:8080/image?imageName=file17763365243631004file16469607146191005adsf.jpg', '2026-04-16 18:49:04', 0, 0.0, 'res-1776336244426.zip', '数据库建模工具特别版.zip', '123456', 'STRUCT-f7899f0ebfdecd0ae3bce19c17b63beb30b319d4895f4f9100f16ec609407552');
INSERT INTO `sh_idle_item` VALUES (270, '极简个人博客网页源码', '基于HTML5+CSS3，响应式布局，支持自适应手机。', '[\"http://localhost:8080/image?imageName=file17763406462021006small9cd735583d4de20d7ac9d9634713f40a_wm.jpg\"]', 15.00, '仅供学习参考，严禁商用', 4, '2026-04-11 19:57:38', 1, 43, 0, 'http://localhost:8080/image?imageName=file17763406561751007file16518883325891006d0c8a786c9177f3e8f2b07bb76cf3bc79f3d566d.jpg', '2026-04-16 19:57:46', 1, 0.0, 'res-1776338600775.zip', '极简个人博客网页源码.zip', '123456', 'STRUCT-15be0430333a240df132675fbcbd34d7ad5b7a59b44ab90e4449b66eec88fbda');
INSERT INTO `sh_idle_item` VALUES (271, '高保真3D城市建筑模型', '适用于Unity或Blender，支持FBX格式。', '[\"http://localhost:8080/image?imageName=file17763414548711009e684ef137fbea84ed27ad7b4a6f91973_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 5, '2026-04-12 20:11:11', 1, 39, 0, 'http://localhost:8080/image?imageName=file17763414612041010file16468992372591015f.jpg', '2026-04-16 20:11:26', 0, 0.0, 'res-1776341076443.zip', '3D城市建筑模型.zip', '123456', 'STRUCT-1f89e95b3072bd7b838c2112bd8fe66b667fa836461c0ecdb7f7978bbf94af99');
INSERT INTO `sh_idle_item` VALUES (272, '后台管理系统UI设计稿', '标准组件库，可直接用于项目。', '[\"http://localhost:8080/image?imageName=file17763422467081014123_wm.PNG\"]', 15.00, '仅供学习参考，严禁商用', 4, '2026-04-12 20:24:23', 1, 38, 0, 'http://localhost:8080/image?imageName=file17763422613111015file16468958478231007d0c8a786c9177f3e8f2b07bb76cf3bc79f3d566d.jpg', '2026-04-16 20:24:32', 3, 5.0, 'res-1776341675518.zip', 'UI设计稿.zip', '123456', 'STRUCT-bd8f518e447b0a61985cc5283d8f453531acc6cf1555eda6ffe47b15d119631a');
INSERT INTO `sh_idle_item` VALUES (273, 'Vue3+TS全栈实战课', '从零开发企业级电商后台，包含全套讲义与代码。', '[\"http://localhost:8080/image?imageName=file177634255308910161725007210-95ba9988ca0df76_wm.png\"]', 16.00, '仅供学习参考，严禁商用', 3, '2026-04-12 20:29:16', 1, 36, 0, '', '2026-04-16 20:29:24', 0, 0.0, 'res-1776342476885.zip', 'Vue3+TS全栈实战课.zip', '123456', 'STRUCT-b80cf64c22a268ade7ebfe969cdf05cefb418be049c490491dc4a0efa4779819');
INSERT INTO `sh_idle_item` VALUES (274, 'SpringBoot微服务架构精讲', '深度解析SpringCloud组件，适合想要进阶的同学。', '[\"http://localhost:8080/image?imageName=file177634292046710172cf4c2618e9a40bdb81a1c8ac3e0572e_wm.png\"]', 10.00, '仅供学习参考，严禁商用', 3, '2026-04-13 20:35:27', 1, 36, 0, '', '2026-04-16 20:35:48', 0, 0.0, 'res-1776342837246.zip', 'SpringBoot微服务架构精讲.zip', '123456', 'STRUCT-caef5673605d6d47383d1eabb1fa517334a39f2dc643875c057f1b061acd87cb');

-- ----------------------------
-- Table structure for sh_idle_item_history
-- ----------------------------
DROP TABLE IF EXISTS `sh_idle_item_history`;
CREATE TABLE `sh_idle_item_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录主键',
  `idle_id` bigint NOT NULL COMMENT '关联的原商品ID',
  `idle_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `idle_details` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picture_list` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `idle_price` decimal(10, 2) NULL DEFAULT NULL,
  `idle_place` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `idle_label` int NULL DEFAULT NULL,
  `idle_status` tinyint NULL DEFAULT NULL COMMENT '当时的状态',
  `proof_image` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '备份时间',
  `resource_path` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源路径',
  `original_file_name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源文件名',
  `unzip_password` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '解压密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '商品修改历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sh_idle_item_history
-- ----------------------------
INSERT INTO `sh_idle_item_history` VALUES (109, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732478176381002file17048908500421002wanju.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 00:50:49', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (110, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732478176381002file17048908500421002wanju_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 1, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 00:51:53', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (111, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732479121481004123.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 00:52:11', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (112, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732479121481004123_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 1, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 00:53:30', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (113, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732480089631005file17620965246061037file16518880948481002书.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 00:53:44', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (114, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732480089631005file17620965246061037file16518880948481002书_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 1, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 15:15:38', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (115, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732997308251002file164569201071610097.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 15:16:03', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (116, 265, '测试1', '测试1', '[\"http://localhost:8080/image?imageName=file17732997308251002file164569201071610097_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 1, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 15:37:04', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (117, 265, '测试1', '测试2', '[\"http://localhost:8080/image?imageName=file17732997308251002file164569201071610097_wm.jpg\"]', 20.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17732478276121003file164689495865510061.png', '2026-03-12 15:37:27', 'res-1773247803146.zip', '测试1.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (118, 266, '测试2', '测试2', '[\"http://localhost:8080/image?imageName=file17733101185611005file17620969648671042file16475926255961002书234asdf.jfif\"]', 10.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17733101798561006file16469610415851007aefsd.jpg', '2026-03-12 18:10:17', 'res-1773310157278.zip', '测试2.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (119, 266, '测试2', '测试2', '[\"http://localhost:8080/image?imageName=file17733101185611005file17620969648671042file16475926255961002书234asdf.jfif\"]', 10.00, '仅供学习参考，严禁商用', 1, 4, 'http://localhost:8080/image?imageName=file17733101798561006file16469610415851007aefsd.jpg', '2026-03-12 19:56:36', 'res-1773310157278.zip', '测试2.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (120, 266, '测试2', '测 试 2', '[\"http://localhost:8080/image?imageName=file17733101185611005file17620969648671042file16475926255961002书234asdf.jfif\"]', 10.00, '仅供学习参考，严禁商用', 1, 3, 'http://localhost:8080/image?imageName=file17733101798561006file16469610415851007aefsd.jpg', '2026-03-12 19:56:53', 'res-1773310157278.zip', '测试2.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (121, 267, 'Java 课程', 'Java 课程', '[\"http://localhost:8080/image?imageName=file17758130646391003file17620966377501038file16495867280841002书6.jfif\"]', 5.00, '仅供学习参考，严禁商用', 3, 3, 'http://localhost:8080/image?imageName=file17758134094481004file16468992372591015f.jpg', '2026-04-10 17:32:25', 'res-1775813387924.zip', 'Java 课程.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (122, 267, 'Java 课程', 'Java 课程', '[\"http://localhost:8080/image?imageName=file17758130646391003file17620966377501038file16495867280841002书6.jfif\"]', 5.00, '仅供学习参考，严禁商用', 3, 4, 'http://localhost:8080/image?imageName=file17758134094481004file16468992372591015f.jpg', '2026-04-10 17:32:47', 'res-1775813387924.zip', 'Java 课程.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (123, 267, 'Java 课程', 'Java 课程', '[\"http://localhost:8080/image?imageName=file17758130646391003file17620966377501038file16495867280841002书6.jfif\"]', 5.00, '仅供学习参考，严禁商用', 3, 3, 'http://localhost:8080/image?imageName=file17758134094481004file16468992372591015f.jpg', '2026-04-10 17:33:18', 'res-1775813387924.zip', 'Java 课程.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (124, 269, '数据库建模工具特别版', '支持ER图绘制，正向反向工程，数据库开发神器。', '[\"http://localhost:8080/image?imageName=file177633651692310035484b4edba10077a.jpg\"]', 10.00, '仅供学习参考，严禁商用', 2, 3, 'http://localhost:8080/image?imageName=file17763365243631004file16469607146191005adsf.jpg', '2026-04-16 18:49:04', 'res-1776336244426.zip', '数据库建模工具特别版.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (125, 270, '极简个人博客网页源码', '基于HTML5+CSS3，响应式布局，支持自适应手机。', '[\"http://localhost:8080/image?imageName=file17763406462021006small9cd735583d4de20d7ac9d9634713f40a.jpg\"]', 15.00, '13333333333', 4, 3, 'http://localhost:8080/image?imageName=file17763406561751007file16518883325891006d0c8a786c9177f3e8f2b07bb76cf3bc79f3d566d.jpg', '2026-04-16 19:57:46', 'res-1776338600775.zip', '极简个人博客网页源码.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (126, 271, '高保真3D城市建筑模型', '适用于Unity或Blender，支持FBX格式。', '[\"http://localhost:8080/image?imageName=file17763414548711009e684ef137fbea84ed27ad7b4a6f91973.jpg\"]', 20.00, '仅供学习参考，严禁商用', 5, 3, 'http://localhost:8080/image?imageName=file17763414612041010file16468992372591015f.jpg', '2026-04-16 20:11:26', 'res-1776341076443.zip', '3D城市建筑模型.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (127, 272, '后台管理系统UI设计稿', '标准组件库，可直接用于项目。', '[\"http://localhost:8080/image?imageName=file17763422467081014123.PNG\"]', 15.00, '仅供学习参考，严禁商用', 4, 3, 'http://localhost:8080/image?imageName=file17763422613111015file16468958478231007d0c8a786c9177f3e8f2b07bb76cf3bc79f3d566d.jpg', '2026-04-16 20:24:32', 'res-1776341675518.zip', 'UI设计稿.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (128, 273, 'Vue3+TS全栈实战课', '从零开发企业级电商后台，包含全套讲义与代码。', '[\"http://localhost:8080/image?imageName=file177634255308910161725007210-95ba9988ca0df76.png\"]', 16.00, '仅供学习参考，严禁商用', 3, 3, '', '2026-04-16 20:29:24', 'res-1776342476885.zip', 'Vue3+TS全栈实战课.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (129, 274, 'SpringBoot微服务架构精讲', '深度解析SpringCloud组件，适合想要进阶的同学。', '[\"http://localhost:8080/image?imageName=file177634292046710172cf4c2618e9a40bdb81a1c8ac3e0572e.png\"]', 10.00, '仅供学习参考，严禁商用', 3, 3, '', '2026-04-16 20:35:48', 'res-1776342837246.zip', 'SpringBoot微服务架构精讲.zip', '123456');
INSERT INTO `sh_idle_item_history` VALUES (136, 283, '测试3', '测试3', '[\"http://localhost:8080/image?imageName=file17764779017601005file17759026141691005file16524436588441014书66.jfif\"]', 10.00, '测试3', 1, 3, 'http://localhost:8080/image?imageName=file17764779051131006file17763406561751007file16518883325891006d0c8a786c9177f3e8f2b07bb76cf3bc79f3d566d.jpg', '2026-04-18 10:05:15', 'res-1776477893257.zip', '测试3.zip', '123456');

-- ----------------------------
-- Table structure for sh_message
-- ----------------------------
DROP TABLE IF EXISTS `sh_message`;
CREATE TABLE `sh_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置主键id',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL COMMENT '留言时间',
  `to_user` bigint NOT NULL COMMENT '所回复的用户',
  `to_message` bigint NULL DEFAULT NULL COMMENT '所回复的留言',
  `message_status` tinyint NULL DEFAULT 0 COMMENT '0未读 1已读',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_index`(`user_id` ASC) USING BTREE,
  INDEX `idle_id_index`(`idle_id` ASC) USING BTREE,
  INDEX `to_user_index`(`to_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '留言表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_message
-- ----------------------------
INSERT INTO `sh_message` VALUES (161, 88, 265, '尊敬的用户，您的商品【测试1】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 00:50:49', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (162, 88, 265, '尊敬的用户，您的商品【测试1】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 00:52:11', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (163, 88, 265, '尊敬的用户，您的商品【测试1】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 00:53:44', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (164, 88, 265, '尊敬的用户，您的商品【测试1】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 15:16:03', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (165, 88, 265, '尊敬的用户，您的商品【测试1】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 15:37:28', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (166, 88, 266, '尊敬的用户，很遗憾，您的商品【测试2】未通过审核。原因：存在侵权行为。请修改后重新提交。', '2026-03-12 18:10:17', 37, NULL, 1);
INSERT INTO `sh_message` VALUES (167, 88, 266, '尊敬的用户，您的商品【测试2】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-03-12 19:56:53', 37, NULL, 1);
INSERT INTO `sh_message` VALUES (168, 37, 266, '【系统自动私信】\n感谢您的购买！\n📄 文件名：测试2.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-03-13 01:10:01', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (169, 88, 267, '尊敬的用户，很遗憾，您的商品【Java 课程】未通过审核。原因：商品信息不对等。请修改后重新提交。', '2026-04-10 17:32:25', 38, NULL, 1);
INSERT INTO `sh_message` VALUES (170, 88, 267, '尊敬的用户，您的商品【Java 课程】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-10 17:33:18', 38, NULL, 1);
INSERT INTO `sh_message` VALUES (171, 38, 267, '【系统自动私信】\n感谢您的购买！\n📄 文件名：Java 课程.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-13 20:27:29', 37, NULL, 1);
INSERT INTO `sh_message` VALUES (172, 36, 265, '【系统自动私信】\n感谢您的购买！\n📄 文件名：测试1.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-13 21:20:42', 39, NULL, 1);
INSERT INTO `sh_message` VALUES (173, 36, 265, '【系统自动私信】\n感谢您的购买！\n📄 文件名：测试1.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-13 21:23:55', 38, NULL, 1);
INSERT INTO `sh_message` VALUES (174, 37, 266, '【系统自动私信】\n感谢您的购买！\n📄 文件名：测试2.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-13 21:28:34', 38, NULL, 1);
INSERT INTO `sh_message` VALUES (175, 37, 266, '【系统自动私信】\n感谢您的购买！\n📄 文件名：测试2.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-13 23:05:25', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (176, 88, 269, '尊敬的用户，您的商品【数据库建模工具特别版】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 18:49:04', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (177, 88, 270, '尊敬的用户，您的商品【极简个人博客网页源码】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 19:57:46', 43, NULL, 1);
INSERT INTO `sh_message` VALUES (178, 88, 271, '尊敬的用户，您的商品【高保真3D城市建筑模型】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 20:11:27', 39, NULL, 0);
INSERT INTO `sh_message` VALUES (179, 88, 272, '尊敬的用户，您的商品【后台管理系统UI设计稿】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 20:24:32', 38, NULL, 1);
INSERT INTO `sh_message` VALUES (180, 88, 273, '尊敬的用户，您的商品【Vue3+TS全栈实战课】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 20:29:24', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (181, 88, 274, '尊敬的用户，您的商品【SpringBoot微服务架构精讲】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。', '2026-04-16 20:35:48', 36, NULL, 1);
INSERT INTO `sh_message` VALUES (197, 43, 270, '【系统自动私信】\n感谢您的购买！\n📄 文件名：极简个人博客网页源码.zip\n✅ 状态：已授权\n📥 下载方式：请直接前往【个人中心】-【购买记录】-【订单详情】页，点击“立即下载”按钮获取文件。\n⚠️ 安全提示：文件已注入您的专属交易指纹，请勿外传。\n解压密码：123456', '2026-04-18 13:27:09', 36, NULL, 1);

-- ----------------------------
-- Table structure for sh_order
-- ----------------------------
DROP TABLE IF EXISTS `sh_order`;
CREATE TABLE `sh_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_number` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户主键id',
  `idle_id` bigint NOT NULL COMMENT '闲置物品主键id',
  `order_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `payment_status` tinyint NULL DEFAULT 0,
  `payment_way` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '支付方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `order_status` tinyint NOT NULL COMMENT '订单状态',
  `is_deleted` tinyint NULL DEFAULT 0,
  `comment_flag` int NULL DEFAULT 0,
  `commission_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '平台抽成金额',
  `seller_income` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '卖家实收金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 220 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_order
-- ----------------------------
INSERT INTO `sh_order` VALUES (210, '177333539816810002', 36, 266, 10.00, 1, '支付宝支付', '2026-03-13 01:09:58', '2026-03-13 01:10:01', 3, 0, 1, 0.50, 9.50);
INSERT INTO `sh_order` VALUES (211, '177581357597510002', 38, 265, 20.00, 0, '支付宝支付', '2026-04-10 17:32:56', NULL, 4, 0, 0, 1.00, 19.00);
INSERT INTO `sh_order` VALUES (213, '177608324377110002', 37, 267, 5.00, 0, '支付宝支付', '2026-04-13 20:27:24', '2026-04-13 20:27:29', 4, 0, 1, 0.25, 4.75);
INSERT INTO `sh_order` VALUES (214, '177608643960710002', 39, 265, 20.00, 1, '支付宝支付', '2026-04-13 21:20:40', '2026-04-13 21:20:42', 3, 0, 1, 1.00, 19.00);
INSERT INTO `sh_order` VALUES (215, '177608691181010003', 38, 266, 10.00, 0, '支付宝支付', '2026-04-13 21:28:32', '2026-04-13 21:28:34', 4, 0, 0, 0.50, 9.50);
INSERT INTO `sh_order` VALUES (216, '177609272135710002', 36, 266, 10.00, 1, '支付宝支付', '2026-04-13 23:05:21', '2026-04-13 23:05:25', 3, 0, 0, 0.50, 9.50);
INSERT INTO `sh_order` VALUES (218, '177641272018310003', 36, 272, 15.00, 1, '支付宝支付', '2026-04-17 15:58:40', '2026-04-17 15:58:47', 3, 0, 1, 0.75, 14.25);
INSERT INTO `sh_order` VALUES (219, '177641333162510004', 36, 272, 15.00, 1, '支付宝支付', '2026-04-17 16:08:52', '2026-04-17 16:08:58', 3, 0, 1, 0.75, 14.25);
INSERT INTO `sh_order` VALUES (220, '177649002242210002', 36, 270, 15.00, 1, '支付宝支付', '2026-04-18 13:27:02', '2026-04-18 13:27:09', 3, 0, 0, 0.75, 14.25);

-- ----------------------------
-- Table structure for sh_order_address
-- ----------------------------
DROP TABLE IF EXISTS `sh_order_address`;
CREATE TABLE `sh_order_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `consignee_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人',
  `consignee_phone` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '电话',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `orderId`(`order_id` ASC) USING BTREE,
  INDEX `order_id_index`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_order_address
-- ----------------------------
INSERT INTO `sh_order_address` VALUES (152, 171, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (153, 172, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (154, 173, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (155, 174, '小张', '12345678910');
INSERT INTO `sh_order_address` VALUES (156, 175, '小张', '12345678910');
INSERT INTO `sh_order_address` VALUES (157, 176, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (158, 177, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (159, 178, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (160, 179, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (161, 180, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (162, 181, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (163, 182, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (164, 183, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (165, 184, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (166, 185, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (167, 186, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (168, 187, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (169, 188, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (170, 189, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (171, 190, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (172, 191, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (173, 192, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (174, 193, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (175, 194, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (176, 195, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (177, 196, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (178, 197, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (179, 198, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (180, 199, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (181, 200, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (182, 201, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (183, 202, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (184, 203, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (185, 204, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (186, 205, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (187, 206, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (188, 207, '李娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (189, 208, '娜娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (190, 209, '娜娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (191, 210, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (192, 211, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (193, 212, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (194, 213, '小伟', '18713790195');
INSERT INTO `sh_order_address` VALUES (195, 214, '娜娜', '15896565656');
INSERT INTO `sh_order_address` VALUES (196, 215, '美美', '15263698569');
INSERT INTO `sh_order_address` VALUES (197, 216, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (198, 217, '小明', '12345678911');
INSERT INTO `sh_order_address` VALUES (199, 218, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (200, 219, '小新', '98765432110');
INSERT INTO `sh_order_address` VALUES (201, 220, '小新', '98765432110');

-- ----------------------------
-- Table structure for sh_report
-- ----------------------------
DROP TABLE IF EXISTS `sh_report`;
CREATE TABLE `sh_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '投诉人ID',
  `idle_id` bigint NOT NULL COMMENT '相关商品ID',
  `reported_user_id` bigint NOT NULL COMMENT '被投诉商家ID',
  `reason` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '投诉原因',
  `content` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '详细说明(选填)',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态 0:未处理 1:已处理',
  `create_time` datetime NOT NULL COMMENT '投诉时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '投诉记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sh_report
-- ----------------------------
INSERT INTO `sh_report` VALUES (4, 38, 266, 37, '发布违禁品', '', 1, '2026-04-13 21:29:41');
INSERT INTO `sh_report` VALUES (5, 36, 271, 39, '诈骗/欺诈', '', 0, '2026-04-17 13:15:18');

-- ----------------------------
-- Table structure for sh_user
-- ----------------------------
DROP TABLE IF EXISTS `sh_user`;
CREATE TABLE `sh_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_number` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '账号（手机号）',
  `user_password` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '登录密码',
  `nickname` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '头像',
  `sign_in_time` datetime NOT NULL COMMENT '注册时间',
  `user_status` tinyint NULL DEFAULT NULL COMMENT '状态（1代表封禁）',
  `ban_time` datetime NULL DEFAULT NULL COMMENT '封禁时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_number`(`account_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sh_user
-- ----------------------------
INSERT INTO `sh_user` VALUES (36, '13333333333', '123456', '小新', 'http://localhost:8080/image?imageName=file17264683773801005OIP-C.jpg', '2025-10-11 00:53:55', 0, '2025-11-25 11:50:21');
INSERT INTO `sh_user` VALUES (37, '18888888888', '123456', '小星', 'http://localhost:8080/image?imageName=file17264682791321002R-C.jpg', '2025-09-25 11:20:58', 0, NULL);
INSERT INTO `sh_user` VALUES (38, '16666666666', '123456', '小华', 'http://localhost:8080/image?imageName=file1726468351394100420204823154758937.jpg', '2025-09-24 07:39:39', 0, NULL);
INSERT INTO `sh_user` VALUES (39, '17777777777', '123456', '娜娜', 'http://localhost:8080/image?imageName=file1726468312095100320230415081411_f2e46.thumb.400_0.jpg', '2025-09-24 08:43:04', 0, NULL);
INSERT INTO `sh_user` VALUES (40, '12345678910', '123456', '洪金宝', 'http://localhost:8080/image?imageName=file164689495865510061.png', '2025-10-10 06:23:41', 0, NULL);
INSERT INTO `sh_user` VALUES (43, '12312313212', '123456', '张亮', 'http://localhost:8080/image?imageName=file165244405499510191556115156156.jpg', '2025-10-14 10:09:40', 0, '2025-11-26 09:17:14');
INSERT INTO `sh_user` VALUES (88, 'system_admin', 'admin_internal', '官方客服', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '2025-11-25 00:12:05', 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;
