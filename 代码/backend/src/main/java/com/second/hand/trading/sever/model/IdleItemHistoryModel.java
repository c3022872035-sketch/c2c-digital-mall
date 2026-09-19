package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class IdleItemHistoryModel implements Serializable {
    private Long id;
    private Long idleId;
    private String idleName;
    private String idleDetails;
    private String pictureList;
    private BigDecimal idlePrice;
    private String idlePlace;
    private Integer idleLabel;
    private Byte idleStatus;
    private String proofImage;
    private Date createTime;
    private String resourcePath;
    private String originalFileName;
    private String unzipPassword;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdleId() { return idleId; }
    public void setIdleId(Long idleId) { this.idleId = idleId; }
    public String getIdleName() { return idleName; }
    public void setIdleName(String idleName) { this.idleName = idleName; }
    public String getIdleDetails() { return idleDetails; }
    public void setIdleDetails(String idleDetails) { this.idleDetails = idleDetails; }
    public String getPictureList() { return pictureList; }
    public void setPictureList(String pictureList) { this.pictureList = pictureList; }
    public BigDecimal getIdlePrice() { return idlePrice; }
    public void setIdlePrice(BigDecimal idlePrice) { this.idlePrice = idlePrice; }
    public String getIdlePlace() { return idlePlace; }
    public void setIdlePlace(String idlePlace) { this.idlePlace = idlePlace; }
    public Integer getIdleLabel() { return idleLabel; }
    public void setIdleLabel(Integer idleLabel) { this.idleLabel = idleLabel; }
    public Byte getIdleStatus() { return idleStatus; }
    public void setIdleStatus(Byte idleStatus) { this.idleStatus = idleStatus; }
    public String getProofImage() { return proofImage; }
    public void setProofImage(String proofImage) { this.proofImage = proofImage; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String resourcePath) { this.resourcePath = resourcePath; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getUnzipPassword() { return unzipPassword; }
    public void setUnzipPassword(String unzipPassword) { this.unzipPassword = unzipPassword; }
}