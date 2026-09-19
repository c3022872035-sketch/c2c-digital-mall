package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 订单联系人快照模型
 */
public class OrderAddressModel implements Serializable {
    private Long id;
    private Long orderId;
    private String consigneeName;
    private String consigneePhone;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getConsigneeName() { return consigneeName; }
    public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }

    public String getConsigneePhone() { return consigneePhone; }
    public void setConsigneePhone(String consigneePhone) { this.consigneePhone = consigneePhone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAddressModel that = (OrderAddressModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(consigneeName, that.consigneeName) &&
                Objects.equals(consigneePhone, that.consigneePhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, consigneeName, consigneePhone);
    }

    @Override
    public String toString() {
        return "OrderAddressModel{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", consigneeName='" + consigneeName + '\'' +
                ", consigneePhone='" + consigneePhone + '\'' +
                '}';
    }
}