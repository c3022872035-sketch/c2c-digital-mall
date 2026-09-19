package com.second.hand.trading.server.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class IdleItemModel implements Serializable {
    private Long id;
    private String idleName;
    private String idleDetails;
    private String pictureList;
    private BigDecimal idlePrice;
    private String idlePlace;
    private Integer idleLabel;
    private Date releaseTime;
    private Byte idleStatus;
    private Long userId;

    // 新增字段
    private String proofImage;      // 版权证明图
    private Date auditTime;         // 审核时间
    private Integer salesCount;     // 销量(统计)
    private Double avgRating;       // 评分(统计)

    // 平台托管资源字段
    private String resourcePath;      // 文件存储路径
    private String originalFileName;  // 原始文件名
    private String unzipPassword; // 解压密码
    private String fileHash;

    private String imgUrl;

    private UserModel user; // 关联用户

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Date getReleaseTime() { return releaseTime; }
    public void setReleaseTime(Date releaseTime) { this.releaseTime = releaseTime; }
    public Byte getIdleStatus() { return idleStatus; }
    public void setIdleStatus(Byte idleStatus) { this.idleStatus = idleStatus; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProofImage() { return proofImage; }
    public void setProofImage(String proofImage) { this.proofImage = proofImage; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    public Integer getSalesCount() { return salesCount; }
    public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String resourcePath) { this.resourcePath = resourcePath; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getUnzipPassword() { return unzipPassword; }
    public void setUnzipPassword(String unzipPassword) { this.unzipPassword = unzipPassword; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }
}