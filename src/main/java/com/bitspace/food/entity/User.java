package com.bitspace.food.entity;


import com.bitspace.food.util.FormatUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.controller
 *@class_name   JsonTransactionController
 *@auth         erik
 *@create_time  18-3-13 下午8:39
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class User {
    private Long id;
    private Long uid;
    private String userName;

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    private Integer role;
    private Byte userType;
    private String loginPwd;
    private String inviteCode;
    private Byte status;
    private Long createTime;
    private String pricePassword;
    private Long LoginTime;
    private Long updateTime;
    private String phone;
    private Byte type;
    private Long parentUid;
    private String countryCode;



    //add
    private String userImg;
    private String payPwd;
    private String mobile;
    private Byte source;


    public User(){
        this.uid=getUid();
    }
    public static final int R_ADMIN = 1;
    public static final int R_USER = 2;
    //add
    public static final String U_IMG= "0";
    public static final byte S_VERIFING = 0; //正常
    public static final byte S_FROZEN = 1;//冻结
    public static  final  String PAY_PWD ="0";

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Map<String, Object> toActualMap() {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("id", this.id);
        retMap.put("uid", this.uid);
        retMap.put("userName", this.userName);
        retMap.put("role", this.role);
        retMap.put("userType", this.userType);
        retMap.put("loginPwd", this.loginPwd);
        retMap.put("inviteCode", this.inviteCode);
        retMap.put("status", this.status);
        retMap.put("createTime", this.createTime);
        retMap.put("pricePassword", this.pricePassword);
        retMap.put("LoginTime", this.LoginTime);
        retMap.put("updateTime", this.updateTime);
        retMap.put("phone", this.phone);
        retMap.put("source", this.source);
        return retMap;
    }

    public Byte getSource() {
        return source;
    }

    public void setSource(Byte source) {
        this.source = source;
    }

    public boolean isAdmin(){
        return (this.role & R_ADMIN) > 0;
    }

    public boolean isUser(){
        return (this.role & R_USER) > 0;
    }
    public String getCountryUserName(){
        return FormatUtil.CountryUserName(this.userName);
    }
    public String getCountryPhone(){
        return FormatUtil.CountryPhone(this.phone);
    }
    public String getPhone() {
        return phone;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    
    public Byte getUserType() {
        return userType;
    }
    
    public void setUserType(Byte userType) {
        this.userType = userType;
    }


    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getPricePassword() {
        return pricePassword;
    }

    public void setPricePassword(String pricePassword) {
        this.pricePassword = pricePassword;
    }

    public Long getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(Long loginTime) {
        LoginTime = loginTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
    public Long getParentUid() {
        return parentUid;
    }

    public void setParentUid(Long parentUid) {
        this.parentUid = parentUid;
    }
    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
