package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.util.Objects;

public class AddressModel implements Serializable {
    private Long id;
    private String consigneeName;
    private String consigneePhone;
    private Boolean defaultFlag;
    private Long userId;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConsigneeName() { return consigneeName; }
    public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }

    public String getConsigneePhone() { return consigneePhone; }
    public void setConsigneePhone(String consigneePhone) { this.consigneePhone = consigneePhone; }

    public Boolean getDefaultFlag() { return defaultFlag; }
    public void setDefaultFlag(Boolean defaultFlag) { this.defaultFlag = defaultFlag; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressModel that = (AddressModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(consigneeName, that.consigneeName) &&
                Objects.equals(consigneePhone, that.consigneePhone) &&
                Objects.equals(defaultFlag, that.defaultFlag) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, consigneeName, consigneePhone, defaultFlag, userId);
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "id=" + id +
                ", consigneeName='" + consigneeName + '\'' +
                ", consigneePhone='" + consigneePhone + '\'' +
                ", defaultFlag=" + defaultFlag +
                ", userId=" + userId +
                '}';
    }
}