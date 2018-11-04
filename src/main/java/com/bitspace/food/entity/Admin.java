package com.bitspace.food.entity;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 14:25
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Admin {
    
    private Long aid;
    
    private String name;
    
    private String loginName;
    
    private String loginPwd;
    
    private Integer type;
    
    private Integer status;
    
    private Integer recallNum;
    
    private Integer reportNum;
    
    private Long createTime;
    
    private Long lastLoginTime;
    
    private Long updateTime;

    private String auditOpinion;
    
    //监察员
    public static final Integer TYPE_MONITOR = 1;
    //供应商
    public static final Integer TYPE_SUPPLIER = 2;
    //经销商
    public static final Integer TYPE_DEALER = 3;

    //待审核
    public static final Integer STATUS_EXAMINE = 1;
    //正常
    public static final Integer STATUS_NORMAL = 2;
    //禁用
    public static final Integer STATUS_FROZEN = 3;
    //审核未通过
    public static final Integer STATUS_NOT_EXAMINE = 4;
    
    public Admin() {
    }
    
    public Admin(Long aid, String loginName) {
        this.aid = aid;
        this.loginName = loginName;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getLastLoginTime() {
    
        return lastLoginTime;
    }
    
    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public Long getCreateTime() {
    
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Integer getReportNum() {
    
        return reportNum;
    }
    
    public void setReportNum(Integer reportNum) {
        this.reportNum = reportNum;
    }
    
    public Integer getRecallNum() {
    
        return recallNum;
    }
    
    public void setRecallNum(Integer recallNum) {
        this.recallNum = recallNum;
    }
    
    public Integer getStatus() {
    
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getType() {
    
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public String getLoginPwd() {
    
        return loginPwd;
    }
    
    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }
    
    public String getLoginName() {
    
        return loginName;
    }
    
    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }
}
