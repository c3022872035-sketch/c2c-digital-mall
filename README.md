# C2C 数字交易平台

## 项目简介

本项目是一个面向数字资源交易场景的 C2C（Customer to Customer）平台。用户可以注册登录、浏览和搜索商品、发布数字资源、收藏商品、创建订单、在线支付、下载资源、发送私信、评价交易并提交举报。管理员可以登录后台，完成商品审核、用户管理、订单管理、评论管理、举报处理、数据统计和后台客服沟通。

项目采用前后端分离结构。前端使用 Vue 2、Vue Router、Element UI、Axios 和 ECharts；后端使用 Spring Boot、MyBatis、MySQL，并集成 JWT 登录、支付宝沙箱支付、百度版权识别、资源文件上传下载和 ZIP 文件处理。

## 目录结构

```text
C2C数字交易平台/
├── README.md
├── 接口文档.md
├── 配置示例/
│   └── application-example.properties
├── 项目截图/
├── .gitignore
├── 数据库文件/
│   └── second_hand_trading.sql
└── 代码/
    ├── backend/       # Spring Boot 后端，默认 8080
    └── frontend/      # Vue 2 前端开发服务器，默认 8081
```

## 技术栈

| 层次 | 技术 |
|---|---|
| 前端 | Vue 2.6、Vue Router 3、Element UI、Axios、ECharts、Vue CLI |
| 后端 | Spring Boot 2.2.5、MyBatis、Lombok、JWT、FastJSON |
| 数据库 | MySQL 8.0，数据库名 `second_hand_trading` |
| 外部能力 | 支付宝沙箱支付、百度 AI 版权检测 |
| 文件能力 | 图片上传、资源 ZIP 上传、下载、解压密码、文件哈希和水印 |

## 环境要求

建议使用以下环境：

- JDK 8 或更高版本；
- Maven 3.6 或更高版本；
- Node.js 12+ 与 npm；
- MySQL 8.0；
- 可用的 8080 和 8081 端口。

## 数据库初始化

先创建数据库，再执行 SQL 脚本：

```sql
CREATE DATABASE second_hand_trading
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -uroot -p second_hand_trading \
  < 数据库文件/second_hand_trading.sql
```

脚本包含 11 张业务表以及开发测试数据。生产环境建议只执行表结构，并重新创建管理员和普通用户账号。

## 后端启动

进入后端目录，复制配置示例并修改数据库连接：

```bash
cd 代码/backend
cp ../../配置示例/application-example.properties src/main/resources/application.properties
mvn clean spring-boot:run
```

后端默认地址为 `http://localhost:8080`。如果使用 IDE，可以直接运行 `com.second.hand.trading.server.ServerApplication`。

## 前端启动

前端请求封装默认把后端地址设置为 `http://localhost:8080`。确认后端启动后执行：

```bash
cd 代码/frontend
npm install
npm run serve
```

开发服务器默认地址为 `http://localhost:8081`。生产构建命令如下：

```bash
npm run build
```

构建产物位于 `代码/frontend/dist/`。

## 默认开发账号

数据库样例中包含以下开发账号。正式部署前请删除或修改这些账号：

| 类型 | 账号 | 密码 |
|---|---|---|
| 管理员 | `admin` | `123456` |
| 管理员 | `admin1` | `123456` |

样例普通用户账号及密码请以 SQL 中的 `sh_user` 数据为准，不建议继续使用样例密码。

## 主要功能

### 用户端

用户端首页展示推荐商品和商品分类。用户可通过关键词搜索商品，查看商品详情、版权证明和评分，收藏商品，与卖家沟通，提交订单并完成支付。个人中心提供个人资料、地址、订单、已售商品和收藏管理。数字资源在订单完成后通过受保护的下载接口获取。

### 卖家端

登录用户可以发布数字商品，上传商品图片、版权证明和资源压缩包，并设置资源解压密码。商品发布后进入审核流程，管理员审核通过后才会在平台展示。卖家可在订单完成后查看评价和销售数据。

### 管理端

管理员后台包含数据统计、商品审核、商品管理、订单管理、用户管理、评论管理、举报处理、资源哈希修复和统计数据同步等功能。后台还提供面向用户的客服聊天接口。

## 业务状态说明

- 商品状态：`0` 删除，`1` 发布/正常，`2` 下架；
- 订单状态：具体状态值以 `sh_order` 表和后端 `OrderService` 实现为准；
- 评论评分：1 至 5 分；
- 收藏唯一性：同一用户不能重复收藏同一商品；
- 登录凭证：后端通过 JWT 生成 Token，前端将 Token 保存在 `localStorage`，请求时放入 `token` 请求头。

## 常见问题

**前端页面无法加载商品？** 请确认后端运行在 8080 端口，并检查 MySQL 数据库名、账号和密码。

**上传资源失败？** 检查 `userFilePath` 指向的目录是否可写，并确认单文件大小不超过 200 MB。

**支付页面无法返回？** 支付宝接口属于沙箱配置，需在后端配置有效的沙箱应用参数；本地开发时也可以只测试下单和支付结果页面。

**管理员页面如何进入？** 访问 `/login-admin`，使用数据库中的管理员账号登录。

## 交付文件

- [接口文档](接口文档.md)
- [配置示例](配置示例/)
- [数据库脚本](数据库文件/second_hand_trading.sql)
- [项目截图](项目截图/)

