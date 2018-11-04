package com.bitspace.food.entity;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/28 18:10
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AccusationHistory {

    private Long id;

    private Long aid;

    private Long goodsid;

    private String name;

    private String identityCcard;

    private String phone;

    private String content;

    private String urlA;

    private String urlB;

    private String urlC;

    private String urlD;

    private String urlE;

    private String urlF;

    private String urlG;

    private String urlH;

    private String disposeContent;

    private Integer type;

    private Integer status;

    private Long createTime;

    private Long updateTime;
    //未处理
    public static final Integer STATUS_NOT_DISPOSE = 1;
    //已处理
    public static final Integer STATUS_YET_DISPOSE = 2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Long getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Long goodsid) {
        this.goodsid = goodsid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityCcard() {
        return identityCcard;
    }

    public void setIdentityCcard(String identityCcard) {
        this.identityCcard = identityCcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrlA() {
        return urlA;
    }

    public void setUrlA(String urlA) {
        this.urlA = urlA;
    }

    public String getUrlB() {
        return urlB;
    }

    public void setUrlB(String urlB) {
        this.urlB = urlB;
    }

    public String getUrlC() {
        return urlC;
    }

    public void setUrlC(String urlC) {
        this.urlC = urlC;
    }

    public String getUrlD() {
        return urlD;
    }

    public void setUrlD(String urlD) {
        this.urlD = urlD;
    }

    public String getUrlE() {
        return urlE;
    }

    public void setUrlE(String urlE) {
        this.urlE = urlE;
    }

    public String getUrlF() {
        return urlF;
    }

    public void setUrlF(String urlF) {
        this.urlF = urlF;
    }

    public String getUrlG() {
        return urlG;
    }

    public void setUrlG(String urlG) {
        this.urlG = urlG;
    }

    public String getUrlH() {
        return urlH;
    }

    public void setUrlH(String urlH) {
        this.urlH = urlH;
    }

    public String getDisposeContent() {
        return disposeContent;
    }

    public void setDisposeContent(String disposeContent) {
        this.disposeContent = disposeContent;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
