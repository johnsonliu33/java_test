package com.bitspace.food.entity;

import java.io.Serializable;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/26 16:37
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CirculateHistory implements Serializable {

    private static final long serialVersionUID = -3818792254302026072L;
    
    private Long id;
    
    private Long goodsId;
    
    private Long fromAid;
    
    private String fromAddress;
    
    private Long peerAid;
    
    private String peerAddress;
    
    private Integer amount;
    
    private Integer type;
    
    private Long createTime;
    
    private String privateKey;

    private String hashId;//hash号
    
    //转出
    public static final Integer TYPE_OUT = 1;
    //转入
    public static final Integer TYPE_INTO = 2;
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Integer getType() {
    
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getAmount() {
    
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public String getPeerAddress() {
    
        return peerAddress;
    }
    
    public void setPeerAddress(String peerAddress) {
        this.peerAddress = peerAddress;
    }
    
    public Long getPeerAid() {
    
        return peerAid;
    }
    
    public void setPeerAid(Long peerAid) {
        this.peerAid = peerAid;
    }
    
    public String getFromAddress() {
    
        return fromAddress;
    }
    
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public Long getFromAid() {
    
        return fromAid;
    }
    
    public void setFromAid(Long fromAid) {
        this.fromAid = fromAid;
    }
    
    public Long getGoodsId() {
    
        return goodsId;
    }
    
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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
}
