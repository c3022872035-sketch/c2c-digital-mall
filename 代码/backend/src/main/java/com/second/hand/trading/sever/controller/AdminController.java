package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.model.*;
import com.second.hand.trading.server.service.*;
import com.second.hand.trading.server.utils.ImageWatermarkUtil;
import com.second.hand.trading.server.vo.ResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final IdleItemService idleItemService;

    private final OrderService orderService;

    private final UserService userService;

    private final ReportService reportService;

    private final CommentService commentService;

    private final MessageService messageService;

    @Value("${userFilePath}")
    private String userFilePath; // 获取文件存储路径

    @Value("${baseUrl}")
    private String baseUrl; // http://localhost:8080

    @GetMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotNull @NotEmpty String accountNumber,
                          @RequestParam("adminPassword") @NotNull @NotEmpty String adminPassword,
                          HttpSession session) {
        AdminModel adminModel = adminService.login(accountNumber, adminPassword);
        if (null == adminModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }
        session.setAttribute("admin", adminModel);
        return ResultVo.success(adminModel);
    }

    @GetMapping("loginOut")
    public ResultVo loginOut(HttpSession session) {
        session.removeAttribute("admin");
        return ResultVo.success();
    }

    @GetMapping("list")
    public ResultVo getAdminList(HttpSession session,
                                 @RequestParam(value = "searchValue", required = false) String searchValue, // 增加参数接收
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "nums", required = false) Integer nums) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p = (page == null || page < 1) ? 1 : page;
        int n = (nums == null || nums < 1) ? 8 : nums;
        if (searchValue == null) searchValue = "";

        return ResultVo.success(adminService.getAdminList(searchValue, p, n));
    }

    @PostMapping("add")
    public ResultVo addAdmin(HttpSession session,
                             @RequestBody AdminModel adminModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if (adminService.addAdmin(adminModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PARAM_ERROR);
    }

    @GetMapping("idleList")
    public ResultVo idleList(HttpSession session,
                             @RequestParam("status") @NotNull @NotEmpty Integer status,
                             @RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "nums", required = false) Integer nums) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p = 1;
        int n = 8;
        if (null != page) {
            p = page > 0 ? page : 1;
        }
        if (null != nums) {
            n = nums > 0 ? nums : 8;
        }
        return ResultVo.success(idleItemService.adminGetIdleList(status, p, n));
    }

    @GetMapping("updateIdleStatus")
    public ResultVo updateIdleStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        IdleItemModel idleItemModel = new IdleItemModel();
        idleItemModel.setId(id);
        idleItemModel.setIdleStatus(status.byteValue());
        if (idleItemService.updateIdleItem(idleItemModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("queryIdle")
    public ResultVo queryIdle(@RequestParam(value = "findValue", required = false) String findValue,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "nums", required = false) Integer nums,
                              @RequestParam("status") @NotNull @NotEmpty Integer status) {
        if (null == findValue) {
            findValue = "";
        }
        int p = 1;
        int n = 8;
        if (null != page) {
            p = page > 0 ? page : 1;
        }
        if (null != nums) {
            n = nums > 0 ? nums : 8;
        }

        if (status == 1)
            return ResultVo.success(idleItemService.findIdleItemForAdmin(findValue, p, n));

        return ResultVo.success(idleItemService.findIdleItem1(findValue, status, p, n));
    }

    @GetMapping("orderList")
    public ResultVo orderList(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        int p = (page==null || page<1) ? 1 : page;
        int n = (nums==null || nums<1) ? 8 : nums;

        return ResultVo.success(orderService.findOrderByNumber(null, p, n));
    }

    @GetMapping("deleteOrder")
    public ResultVo deleteOrder(HttpSession session,
                                @RequestParam("id") @NotNull @NotEmpty Long id) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if (orderService.deleteOrder(id)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("queryOrder")
    public ResultVo queryOrder(HttpSession session,
                               @RequestParam(value = "page",required = false) Integer page,
                               @RequestParam(value = "nums",required = false) Integer nums,
                               @RequestParam(value = "searchValue",required = false) String searchValue){
        if(session.getAttribute("admin")==null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        int p = (page==null || page<1) ? 1 : page;
        int n = (nums==null || nums<1) ? 8 : nums;
        return ResultVo.success(orderService.findOrderByNumber(searchValue, p, n));
    }

    @GetMapping("userList")
    public ResultVo userList(HttpSession session,
                             @RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "nums", required = false) Integer nums,
                             @RequestParam("status") @NotNull @NotEmpty Integer status) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p = 1;
        int n = 8;
        if (null != page) {
            p = page > 0 ? page : 1;
        }
        if (null != nums) {
            n = nums > 0 ? nums : 8;
        }
        return ResultVo.success(userService.getUserByStatus(status, p, n));
    }

    @GetMapping("updateUserStatus")
    public ResultVo updateUserStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        UserModel userModel = new UserModel();
        userModel.setId(id);
        userModel.setUserStatus(status.byteValue());

        if (status == 1) {
            userModel.setBanTime(new Date());
        }
        if (userService.updateUserInfo(userModel))
            return ResultVo.success();
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("queryUser")
    public ResultVo queryUser(HttpSession session,
                              @RequestParam(value = "searchValue", required = false) String searchValue,
                              @RequestParam(value = "mode", required = false) Integer mode,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "nums", required = false) Integer nums) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p = (page == null || page < 1) ? 1 : page;
        int n = (nums == null || nums < 1) ? 8 : nums;

        if (searchValue == null) searchValue = "";

        if (mode == 1) {
            if ("".equals(searchValue)) {
                return ResultVo.success(userService.getUserByStatus(0, p, n));
            } else {
                return ResultVo.success(userService.getUserByNumber(searchValue, mode));
            }
        } else if (mode == 2) {
            if ("".equals(searchValue)) {
                return ResultVo.success(userService.getUserByStatus(1, p, n));
            } else {
                return ResultVo.success(userService.getUserByNumber(searchValue, mode));
            }
        } else {

            return ResultVo.success(adminService.getAdminList(searchValue, p, n));
        }
    }

    // 获取审核列表
    @GetMapping("auditList")
    public ResultVo auditList(HttpSession session,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "searchValue", required = false) String searchValue, // 新增
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "nums", required = false) Integer nums) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        int p = (page == null || page < 1) ? 1 : page;
        int n = (nums == null || nums < 1) ? 8 : nums;
        return ResultVo.success(idleItemService.getAuditList(status, searchValue, p, n));
    }

    // 审核操作 (通过/拒绝)
    @PostMapping("audit")
    public ResultVo auditItem(HttpSession session,
                              @RequestParam Long id,
                              @RequestParam Integer status,
                              @RequestParam(required = false) String reason) {
        // 1. 权限校验
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        // 2. 获取商品原信息
        IdleItemModel item = idleItemService.getIdleItem(id);
        if (item == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }

        // 3. 准备更新对象
        IdleItemModel update = new IdleItemModel();
        update.setId(id);
        update.setIdleStatus(status.byteValue());
        update.setAuditTime(new Date());

        // 4. 【核心逻辑】审核通过(status=1)时，自动给图片添加水印
        if (status == 1) {
            try {
                String pictureListStr = item.getPictureList();
                if (pictureListStr != null && !pictureListStr.isEmpty() && !"[]".equals(pictureListStr)) {
                    // 解析图片列表 (假设使用 FastJSON)
                    List<String> imgUrls = com.alibaba.fastjson.JSON.parseArray(pictureListStr, String.class);
                    List<String> newImgUrls = new ArrayList<>();

                    // 设置水印文字：发布者昵称 @ C2C平台
                    String watermarkText = item.getUser().getNickname() + " @ C2C平台";
                    File fileDir = new File(userFilePath);

                    for (String url : imgUrls) {
                        // 仅处理本地存储的图片 (url包含 imageName=)
                        if (url.contains("imageName=")) {
                            // 提取文件名
                            String fileName = url.substring(url.lastIndexOf("=") + 1);
                            String localPath = fileDir.getAbsolutePath() + "/" + fileName;

                            // 生成新文件名 (添加 _wm 后缀，防止浏览器缓存旧图)
                            // 示例: file123.jpg -> file123_wm.jpg
                            String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : ".jpg";
                            String nameWithoutExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
                            String newFileName = nameWithoutExt + "_wm" + ext;
                            String newLocalPath = fileDir.getAbsolutePath() + "/" + newFileName;

                            File srcFile = new File(localPath);
                            if (srcFile.exists()) {
                                // 调用工具类添加水印
                                ImageWatermarkUtil.addWatermark(localPath, newLocalPath, watermarkText);
                                // 将新图片的URL加入列表
                                newImgUrls.add(baseUrl + "/image?imageName=" + newFileName);
                            } else {
                                // 原图物理文件丢失，保留原链接
                                newImgUrls.add(url);
                            }
                        } else {
                            // 网络图片或其他格式，不做处理
                            newImgUrls.add(url);
                        }
                    }

                    // 将新的图片列表序列化存入数据库
                    update.setPictureList(com.alibaba.fastjson.JSON.toJSONString(newImgUrls));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("水印处理异常，但仍然继续通过审核: " + e.getMessage());
            }
        }

        // 5. 执行更新并发送通知
        if (idleItemService.updateIdleItem(update)) {
            // 发送私信通知
            MessageModel msg = new MessageModel();
            msg.setUserId(88L); // 官方客服ID
            msg.setToUser(item.getUserId());
            msg.setIdleId(item.getId());
            msg.setCreateTime(new Date());
            msg.setMessageStatus((byte) 0); // 未读

            if (status == 1) {
                msg.setContent("尊敬的用户，您的商品【" + item.getIdleName() + "】已通过审核，系统已自动为您的预览图添加版权水印，现已上架售卖。");
            } else if (status == 4) {
                msg.setContent("尊敬的用户，很遗憾，您的商品【" + item.getIdleName() + "】未通过审核。原因：" + (reason != null ? reason : "不符合规范") + "。请修改后重新提交。");
            }

            messageService.addMessage(msg);
            return ResultVo.success();
        }

        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 获取商品修改历史
    @GetMapping("idle/history")
    public ResultVo getIdleHistory(HttpSession session, @RequestParam Long idleId) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        return ResultVo.success(idleItemService.getHistoryList(idleId));
    }


    // 1. 获取待处理投诉数量
    @GetMapping("report/pendingCount")
    public ResultVo getPendingReportCount(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        return ResultVo.success(reportService.countPending());
    }

    // 2. 获取投诉列表
    @GetMapping("reportList")
    public ResultVo getReportList(HttpSession session,
                                  @RequestParam(value = "status", required = false) Integer status,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "nums", required = false) Integer nums,
                                  @RequestParam(value = "searchValue", required = false) String searchValue) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        int p = (page == null || page < 1) ? 1 : page;
        int n = (nums == null || nums < 1) ? 8 : nums;
        // 传入 searchValue
        return ResultVo.success(reportService.getReportList(status, searchValue, p, n));
    }

    // 3. 处理投诉
    @PostMapping("handleReport")
    public ResultVo handleReport(HttpSession session, @RequestBody ReportModel report) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        if (reportService.handleReport(report.getId())) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 4. 获取评价列表 (增加搜索参数)
    @GetMapping("commentList")
    public ResultVo getCommentList(HttpSession session,
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "nums", required = false) Integer nums,
                                   @RequestParam(value = "searchValue", required = false) String searchValue) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        int p = (page == null || page < 1) ? 1 : page;
        int n = (nums == null || nums < 1) ? 8 : nums;
        return ResultVo.success(commentService.getAllComments(searchValue, p, n));
    }

    // 5. 管理员新增评价
    @PostMapping("addComment")
    public ResultVo addAdminComment(HttpSession session, @RequestBody CommentModel comment) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        comment.setCreateTime(new Date());
        // 根据idleId 查 sellerId
        IdleItemModel idle = idleItemService.getIdleItem(comment.getIdleId());
        if (idle != null) {
            comment.setSellerId(idle.getUserId());
        }
        comment.setOrderId(0L); // 管理员添加的默认为0，或者是虚拟订单

        if (commentService.addAdminComment(comment)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 6. 管理员修改评价
    @PostMapping("updateComment")
    public ResultVo updateAdminComment(HttpSession session, @RequestBody CommentModel comment) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        if (commentService.updateAdminComment(comment)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 7. 删除评价
    @GetMapping("deleteComment")
    public ResultVo deleteComment(HttpSession session, @RequestParam Long id) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        if (commentService.deleteComment(id)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 8. 管理员添加商品
    @PostMapping("addIdle")
    public ResultVo addIdle(HttpSession session, @RequestBody IdleItemModel idleItemModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        if (idleItemModel.getUserId() == null) {
            idleItemModel.setUserId(88L); // 确保这里是你用的官方ID (88 或 888888)
        }

        idleItemModel.setIdleStatus((byte) 1);
        idleItemModel.setReleaseTime(new Date());
        processWatermark(idleItemModel);

        if (idleItemService.addIdleItem(idleItemModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 9. 管理员修改商品信息
    @PostMapping("updateIdle")
    public ResultVo updateIdle(HttpSession session, @RequestBody IdleItemModel idleItemModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        IdleItemModel originalItem = idleItemService.getIdleItem(idleItemModel.getId());
        if (originalItem != null) {
            idleItemModel.setUserId(originalItem.getUserId());
        }

        processWatermark(idleItemModel);
        // 执行更新
        if (idleItemService.updateIdleItem(idleItemModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 10. 管理员新增用户
    @PostMapping("addUser")
    public ResultVo addUser(HttpSession session, @RequestBody UserModel userModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 设置默认头像和注册时间
        if (userModel.getAvatar() == null || "".equals(userModel.getAvatar())) {
            userModel.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }
        userModel.setSignInTime(new java.util.Date());
        userModel.setUserStatus((byte) 0); // 默认正常状态

        if (userService.addUser(userModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.REGISTER_ERROR); // 或者是账号重复错误
    }

    // 11. 管理员修改用户信息 (昵称、密码等)
    @PostMapping("updateUser")
    public ResultVo updateUser(HttpSession session, @RequestBody UserModel userModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 直接调用更新接口，MyBatis 动态SQL会忽略 null 字段
        if (userService.updateUserInfo(userModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 12. 管理员删除用户 (永久封号)
    @GetMapping("deleteUser")
    public ResultVo deleteUser(HttpSession session, @RequestParam Long id) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if (userService.deleteUser(id)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 13. 修改管理员信息
    @PostMapping("update")
    public ResultVo updateAdmin(HttpSession session, @RequestBody AdminModel adminModel) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if (adminService.updateAdmin(adminModel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    // 14. 删除管理员
    @GetMapping("delete")
    public ResultVo deleteAdmin(HttpSession session, @RequestParam Long id) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        // 防止删除自己（可选逻辑）
        AdminModel currentAdmin = (AdminModel) session.getAttribute("admin");
        if (currentAdmin.getId().equals(id)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }

        if (adminService.deleteAdmin(id)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    //15. 内部辅助方法：给商品图片列表添加水印
    private void processWatermark(IdleItemModel item) {
        try {
            String pictureListStr = item.getPictureList();
            if (pictureListStr != null && !pictureListStr.isEmpty() && !"[]".equals(pictureListStr)) {
                List<String> imgUrls = com.alibaba.fastjson.JSON.parseArray(pictureListStr, String.class);
                List<String> newImgUrls = new ArrayList<>();

                // 1. 确定水印显示的昵称
                String nickname = "平台官方"; // 兜底默认值
                if (item.getUserId() != null) {
                    // 查询该ID对应的用户昵称
                    UserModel u = userService.getUser(item.getUserId());
                    if (u != null) {
                        nickname = u.getNickname();
                    }
                }
                String watermarkText = nickname + " @ C2C平台";

                File fileDir = new File(userFilePath);

                for (String url : imgUrls) {
                    if (url.contains("imageName=")) {
                        String fileName = url.substring(url.lastIndexOf("=") + 1);

                        // 2. 防止重复添加水印
                        if (fileName.contains("_wm.")) {
                            newImgUrls.add(url);
                            continue;
                        }

                        String localPath = fileDir.getAbsolutePath() + "/" + fileName;

                        // 生成带 _wm 后缀的新文件名
                        String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : ".jpg";
                        String nameWithoutExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
                        String newFileName = nameWithoutExt + "_wm" + ext;
                        String newLocalPath = fileDir.getAbsolutePath() + "/" + newFileName;

                        File srcFile = new File(localPath);
                        if (srcFile.exists()) {
                            // 3. 执行添加水印
                            ImageWatermarkUtil.addWatermark(localPath, newLocalPath, watermarkText);
                            newImgUrls.add(baseUrl + "/image?imageName=" + newFileName);
                        } else {
                            newImgUrls.add(url);
                        }
                    } else {
                        newImgUrls.add(url);
                    }
                }
                item.setPictureList(com.alibaba.fastjson.JSON.toJSONString(newImgUrls));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("水印处理异常: " + e.getMessage());
        }
    }

    @GetMapping("idle/pendingCount")
    public ResultVo getPendingIdleCount(HttpSession session) {
        if(session.getAttribute("admin") == null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        return ResultVo.success(idleItemService.getAuditList(3, null, 1, 1).getCount());
    }

    @GetMapping("/repair-hash")
    public ResultVo repairHash(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        int count = idleItemService.repairFileHash(userFilePath);

        return ResultVo.success("修复成功，共处理 " + count + " 条数据");
    }

    @GetMapping("/sync-stats")
    public ResultVo syncStats(HttpSession session) {
        if (session.getAttribute("admin") == null) return ResultVo.fail(ErrorMsg.COOKIE_ERROR);

        idleItemService.syncAllItemStats();

        return ResultVo.success("全站销量及评分数据同步刷新成功！");
    }

    @PostMapping("/refundOrder")
    public ResultVo refundOrder(HttpSession session, @RequestParam Long id) {
        // 1. 权限校验
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        // 2. 构造更新对象
        OrderModel order = new OrderModel();
        order.setId(id);
        order.setOrderStatus((byte) 4);
        order.setPaymentStatus((byte) 0);

        // 3. 执行更新
        if (orderService.updateOrder(order)) {
            return ResultVo.success("订单退款成功");
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}