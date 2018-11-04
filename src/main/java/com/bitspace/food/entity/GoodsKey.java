package com.bitspace.food.entity;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/29 11:08
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GoodsKey {
    private Long id;
    
    private Long gid;
    
    private String payPwd;
    
    private String privateKey;
    
    private String publicKey;
    
    private Long createTime;
    
    public GoodsKey() {
    }
    
    public GoodsKey(Long gid, String payPwd, String privateKey, String publicKey) {
        this.gid = gid;
        this.payPwd = payPwd;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.createTime = System.currentTimeMillis();
    }
    
    public GoodsKey(String publicKey, Long id) {
        this.gid = id;
        this.publicKey = publicKey;
    }
    
    public GoodsKey(Long id, String privateKey) {
        this.id = id;
        this.privateKey = privateKey;
    }
    
    public String getPayPwd() {
        return payPwd;
    }
    
    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public String getPublicKey() {
    
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public String getPrivateKey() {
    
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public Long getGid() {
    
        return gid;
    }
    
    public void setAid(Long gid) {
        this.gid = gid;
    }
    
    public Long getId() {
    
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
