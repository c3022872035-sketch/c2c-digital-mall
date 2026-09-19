package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.util.Date;

public class ReportModel implements Serializable {
    private Long id;
    private Long userId;
    private Long idleId;
    private Long reportedUserId;
    private String reason;
    private String content;
    private Byte status;
    private Date createTime;

    // ========= 新增关联字段 =========
    private UserModel user;         // 投诉人信息
    private UserModel reportedUser; // 被投诉人信息
    private IdleItemModel idleItem; // 相关商品信息
    // ==============================

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getIdleId() { return idleId; }
    public void setIdleId(Long idleId) { this.idleId = idleId; }

    public Long getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    // ========= 新增字段的 Getter/Setter =========
    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

    public UserModel getReportedUser() { return reportedUser; }
    public void setReportedUser(UserModel reportedUser) { this.reportedUser = reportedUser; }

    public IdleItemModel getIdleItem() { return idleItem; }
    public void setIdleItem(IdleItemModel idleItem) { this.idleItem = idleItem; }
}