package com.bitspace.food.entity;

import java.io.Serializable;

/**
 * 商品类
 */
public class Goods implements Serializable {

    private static final long serialVersionUID = -3818792254302026072L;

    private Long id ;
    private Long aid;
    private String name;
    private Integer number;
    private Integer circulateNum;
    private Integer recallNum;
    private Integer status;
    private Long sellAid;
    private Long createTime;
    private String cause;
    
    private String licence;//生产许可证
    private String brand;//品牌
    private String specification;//规格
    private String burdenSheet;//配料表
    private String store;//存储方式
    private String expirationDate;//保质期
    private String worksName; //厂名
    private String worksAddress;//厂址
    private String worksPhone;//厂家联系方式
    private String hashId;//hash号

    private Long reportNum;
    private Long disposeNum;
    private String privateKey;//厂址
    private String moneyAddress;//厂址
    
    
    //待完善
    public static final Integer STATUS_CORVIDAE = 1;
    //待审核
    public static final Integer STATUS_CHECK_PENDING = 2;
    //审核未通过
    public static final Integer STATUS_NOT_PASS = 3;
    //正常
    public static final Integer STATUS_NORMAL = 4;
    //召回
    public static final Integer STATUS_RECALL = 5;
    
    public String getWorksPhone() {
        return worksPhone;
    }
    
    public void setWorksPhone(String worksPhone) {
        this.worksPhone = worksPhone;
    }
    
    public String getWorksAddress() {
    
        return worksAddress;
    }
    
    public void setWorksAddress(String worksAddress) {
        this.worksAddress = worksAddress;
    }
    
    public String getWorksName() {
    
        return worksName;
    }
    
    public void setWorksName(String worksName) {
        this.worksName = worksName;
    }
    
    public String getExpirationDate() {
    
        return expirationDate;
    }
    
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getStore() {
    
        return store;
    }
    
    public void setStore(String store) {
        this.store = store;
    }
    
    public String getBurdenSheet() {
    
        return burdenSheet;
    }
    
    public void setBurdenSheet(String burdenSheet) {
        this.burdenSheet = burdenSheet;
    }
    
    public String getSpecification() {
    
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public String getBrand() {
    
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getLicence() {
    
        return licence;
    }
    
    public void setLicence(String licence) {
        this.licence = licence;
    }
    
    public String getCause() {
        return cause;
    }
    
    public void setCause(String cause) {
        this.cause = cause;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getSellAid() {
        return sellAid;
    }
    
    public void setSellAid(Long sellAid) {
        this.sellAid = sellAid;
    }
    
    public Integer getStatus() {
    
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    
    public Integer getRecallNum() {
    
        return recallNum;
    }
    
    public void setRecallNum(Integer recallNum) {
        this.recallNum = recallNum;
    }
    
    public Integer getCirculateNum() {
    
        return circulateNum;
    }
    
    public void setCirculateNum(Integer circulateNum) {
        this.circulateNum = circulateNum;
    }
    
    public Integer getNumber() {
    
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public String getName() {
    
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getAid() {
        return aid;
    }
    
    public void setAid(Long aid) {
        this.aid = aid;
    }
    
    public Long getId() {
    
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public Long getReportNum() {
        return reportNum;
    }

    public void setReportNum(Long reportNum) {
        this.reportNum = reportNum;
    }

    public Long getDisposeNum() {
        return disposeNum;
    }

    public void setDisposeNum(Long disposeNum) {
        this.disposeNum = disposeNum;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getMoneyAddress() {
        return moneyAddress;
    }

    public void setMoneyAddress(String moneyAddress) {
        this.moneyAddress = moneyAddress;
    }
}
