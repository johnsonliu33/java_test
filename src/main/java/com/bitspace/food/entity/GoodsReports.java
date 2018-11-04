package com.bitspace.food.entity;

import java.io.Serializable;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.entity
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/30 10:30
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GoodsReports implements Serializable {

    private static final long serialVersionUID = -3818792254302026072L;
    
    private Long id;
    
    private Long gid;
    
    private String facade;
    
    private Double moisture;
    
    private Double impurity;
    
    private Double unsoundGrain;
    
    private Double oleaginousness;
    
    private Double aflatoxin;
    
    private Double gongContent;
    
    private Double hchResidual;
    
    private Double ddtResidual;
    
    private Double defectiveParticle;
    
    private String annualOutputOf;
    
    private String placeOfOrigin;
    
    private Long reportsId;
    
    private Long createTime;

    private String hashId;//hash号
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getReportsId() {
        return reportsId;
    }
    
    public void setReportsId(Long reportsId) {
        this.reportsId = reportsId;
    }
    
    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }
    
    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }
    
    public String getAnnualOutputOf() {
        return annualOutputOf;
    }
    
    public void setAnnualOutputOf(String annualOutputOf) {
        this.annualOutputOf = annualOutputOf;
    }
    
    public Double getDefectiveParticle() {
    
        return defectiveParticle;
    }
    
    public void setDefectiveParticle(Double defectiveParticle) {
        this.defectiveParticle = defectiveParticle;
    }
    
    public Double getDdtResidual() {
    
        return ddtResidual;
    }
    
    public void setDdtResidual(Double ddtResidual) {
        this.ddtResidual = ddtResidual;
    }
    
    public Double getHchResidual() {
    
        return hchResidual;
    }
    
    public void setHchResidual(Double hchResidual) {
        this.hchResidual = hchResidual;
    }
    
    public Double getGongContent() {
    
        return gongContent;
    }
    
    public void setGongContent(Double gongContent) {
        this.gongContent = gongContent;
    }
    
    public Double getAflatoxin() {
    
        return aflatoxin;
    }
    
    public void setAflatoxin(Double aflatoxin) {
        this.aflatoxin = aflatoxin;
    }
    
    public Double getOleaginousness() {
    
        return oleaginousness;
    }
    
    public void setOleaginousness(Double oleaginousness) {
        this.oleaginousness = oleaginousness;
    }
    
    public Double getUnsoundGrain() {
    
        return unsoundGrain;
    }
    
    public void setUnsoundGrain(Double unsoundGrain) {
        this.unsoundGrain = unsoundGrain;
    }
    
    public Double getImpurity() {
    
        return impurity;
    }
    
    public void setImpurity(Double impurity) {
        this.impurity = impurity;
    }
    
    public Double getMoisture() {
    
        return moisture;
    }
    
    public void setMoisture(Double moisture) {
        this.moisture = moisture;
    }
    
    public String getFacade() {
    
        return facade;
    }
    
    public void setFacade(String facade) {
        this.facade = facade;
    }
    
    public Long getGid() {
    
        return gid;
    }
    
    public void setGid(Long gid) {
        this.gid = gid;
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
