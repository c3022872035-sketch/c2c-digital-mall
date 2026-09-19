package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.util.Date;

public class CommentModel implements Serializable {
    private Long id;
    private Long orderId;
    private Long idleId;
    private Long buyerId;
    private Long sellerId;
    private Integer rating;
    private String content;
    private Date createTime;

    // ========= 新增关联字段 =========
    private UserModel buyer;        // 买家信息
    private IdleItemModel idleItem; // 商品信息
    // ==============================

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getIdleId() { return idleId; }
    public void setIdleId(Long idleId) { this.idleId = idleId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    // ========= 新增字段的 Getter/Setter =========
    public UserModel getBuyer() { return buyer; }
    public void setBuyer(UserModel buyer) { this.buyer = buyer; }

    public IdleItemModel getIdleItem() { return idleItem; }
    public void setIdleItem(IdleItemModel idleItem) { this.idleItem = idleItem; }
}