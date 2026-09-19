package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderModel implements Serializable {
    private Long id;
    private String orderNumber;
    private Long userId;
    private UserModel user;
    private Long idleId;
    private IdleItemModel idleItem;
    private BigDecimal orderPrice;
    private Byte paymentStatus;
    private String paymentWay;
    private Date createTime;
    private Date paymentTime;
    private Byte orderStatus;
    private Byte isDeleted;
    private Integer commentFlag;
    private BigDecimal commissionPrice; 
    private BigDecimal sellerIncome;

    private static final long serialVersionUID = 1L;

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }
    public Long getIdleId() { return idleId; }
    public void setIdleId(Long idleId) { this.idleId = idleId; }
    public IdleItemModel getIdleItem() { return idleItem; }
    public void setIdleItem(IdleItemModel idleItem) { this.idleItem = idleItem; }
    public BigDecimal getOrderPrice() { return orderPrice; }
    public void setOrderPrice(BigDecimal orderPrice) { this.orderPrice = orderPrice; }
    public Byte getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Byte paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentWay() { return paymentWay; }
    public void setPaymentWay(String paymentWay) { this.paymentWay = paymentWay; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }
    public Byte getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Byte orderStatus) { this.orderStatus = orderStatus; }
    public Byte getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Byte isDeleted) { this.isDeleted = isDeleted; }
    public Integer getCommentFlag() { return commentFlag; }
    public void setCommentFlag(Integer commentFlag) { this.commentFlag = commentFlag; }

    public BigDecimal getCommissionPrice() { return commissionPrice; }
    public void setCommissionPrice(BigDecimal commissionPrice) { this.commissionPrice = commissionPrice; }
    public BigDecimal getSellerIncome() { return sellerIncome; }
    public void setSellerIncome(BigDecimal sellerIncome) { this.sellerIncome = sellerIncome; }
}