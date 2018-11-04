package com.bitspace.food.entity;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth erik
 * @create_time 18-3-15 下午9:05
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UserMoney {
    private Long uid;
    private Double balance;
    private Byte  type;
    private Long  updateTime;
    private Double fee;
    private String userName;
    private Double rechargeIntegral;//充值积分
    private Double intoIntegral; //A商城转入积分
    private Double integral;
    private String integralName;

    public static final Byte MONEY_TYPE_VAC = 105;
    public static final Byte USER_IN_MONEY=1;
    public static final Byte USER_OFF_MONEY=2;
    public static final Byte SOURCE_A=1;//A商城
    public static final Byte SOURCE_B=2;//B商城
    //add
    public static final Byte C_ONE=1;
    public static final Byte C_TWO=2;
    public static final Byte C_THREE=3;
    public static final Byte C_FOUE=4;
    public static final Byte C_FIVE=5;
    public static final Byte C_SIX=6;
    public static final Byte GOODS_ID=0;

    public String getIntegralName() {
        return integralName;
    }

    public void setIntegralName(String integralName) {
        this.integralName = integralName;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public Double getIntoIntegral() {

        return intoIntegral;
    }

    public void setIntoIntegral(Double intoIntegral) {
        this.intoIntegral = intoIntegral;
    }

    public void setUpdateTime(Long updateTime) {

        this.updateTime = updateTime;
    }

    public Double getRechargeIntegral() {
        return rechargeIntegral;
    }

    public void setRechargeIntegral(Double rechargeIntegral) {
        this.rechargeIntegral = rechargeIntegral;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUid() {
        return uid;
    }
    public Long UserMoney(Long uid){
        return this.uid=uid;
    }
    public Double getBalance() {
        return balance;
    }
    
    public Double getFee() {
        return fee;
    }
    
    public Byte getType() {
        return type;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public void setUid(Long uid) {
        this.uid = uid;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public void setFee(Double fee) {
        this.fee = fee;
    }
    
    public void setType(Byte type) {
        this.type = type;
    }
    
    public static void setUpdateTime(UserMoney userMoney, Long updateTime) {
        userMoney.updateTime = updateTime;
    }
}
