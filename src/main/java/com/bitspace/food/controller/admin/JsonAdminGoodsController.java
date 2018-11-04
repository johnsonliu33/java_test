package com.bitspace.food.controller.admin;

import com.bitspace.food.base.*;
import com.bitspace.food.constants.Pager;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.entity.*;
import com.bitspace.food.memdb.AdminKeyMemDB;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.memdb.GoodsKeyMemDB;
import com.bitspace.food.memdb.GoodsMemDB;
import com.bitspace.food.util.EmptyUtil;
import com.bitspace.food.util.JwtUtil;
import com.bitspace.food.util.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.controller.admin
 * @class_name 商品流通控制器
 * @auth Administrator
 * @create_time 2018/5/25 14:52
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class JsonAdminGoodsController {
    
    /**
     * 商品列表
     * @param request
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/select", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> selectGoods(HttpServletRequest request,
                                               @RequestParam(value = "search", required = false) String search,
                                               @RequestParam(value = "status", required = false) Integer status,
                                               @RequestParam(value = "goodsId", required = false) Long goodsId,
                                               @RequestParam(value = "aName", required = false) String aName,
                                               @RequestParam(value = "sellName", required = false) String sellName,
                                               @RequestParam(value = "startTime", required = false) String startTime,
                                               @RequestParam(value = "endTime", required = false) String endTime,
                                               @RequestParam(value = "currentPage", required = false)Long currentPage,
                                               @RequestParam(value = "count", required = false)Long count) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        search = StringUtils.trimToNull(search);
        sellName = StringUtils.trimToNull(sellName);
        Map<String, Object> map = new HashMap<>();
        Admin admin = AdminMemDB.getAdminByAid(aid);
        if (currentPage == null) currentPage = Pager.START_PAGE;
        if (count == null) count =Pager.DEFAULT_COUNT;
        if (admin == null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        if(Admin.TYPE_SUPPLIER == admin.getType()){
            map.put("aid", aid);
        }else if(Admin.TYPE_DEALER == admin.getType()){
            map.put("sellAid", aid);
        }
        Long start = (currentPage - 1) * count;
        map.put("search", search);
        map.put("status", status);
        map.put("goodsId", goodsId);
        map.put("aName", aName);
        map.put("sellName", sellName);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("currentPage", start);
        map.put("count", count);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GOODS_LIST, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData(), rsp.getResult().getRecordTotal()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 商品详情
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/info", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> goodsInfoById(HttpServletRequest request,
                                                   @RequestParam(value = "goodsId", required = true) Long goodsId
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GOODS_INFO, goodsId)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 商品创建
     * @param request
     * @param name
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> add(HttpServletRequest request,
                                                @RequestParam(value = "name", required = true) String name,
                                                @RequestParam(value = "number", required = true) Integer number,
                                                @RequestParam(value = "payPwd", required = true) String payPwd
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Long aid = JwtUtil.getUid(request);
        name = StringUtils.trimToNull(name);
        payPwd = StringUtils.trimToNull(payPwd);
        //验证名称
        if (EmptyUtil.isNullOrEmpty(name)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_NAME_NULL_ERROR));
            return dr;
        }
        //验证数量
        if (number == null || number <= 0){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_NUMBER_ERROR));
            return dr;
        }
        //验证流通密码
        if(EmptyUtil.isNullOrEmpty(payPwd)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_BE_NULL));
            return dr;
        }
        Goods goods = new Goods();
        goods.setAid(aid);
        goods.setName(name);
        goods.setNumber(number);
        goods.setCreateTime(System.currentTimeMillis());
        goods.setStatus(Goods.STATUS_CORVIDAE);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GOODS_INSET, Pair.of(goods, payPwd))
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 完善商品
     * @param request
     * @param id
     * @param licence
     * @param brand
     * @param specification
     * @param burdenSheet
     * @param store
     * @param expirationDate
     * @param worksName
     * @param worksAddress
     * @param worksPhone
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/perfect", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> perfect(HttpServletRequest request,
                                          @RequestParam(value = "id", required = true) Long id,
                                          @RequestParam(value = "licence", required = true) String licence,
                                          @RequestParam(value = "brand", required = true) String brand,
                                          @RequestParam(value = "specification", required = true) String specification,
                                          @RequestParam(value = "burdenSheet", required = true) String burdenSheet,
                                          @RequestParam(value = "store", required = true) String store,
                                          @RequestParam(value = "expirationDate", required = true) String expirationDate,
                                          @RequestParam(value = "worksName", required = true) String worksName,
                                          @RequestParam(value = "worksAddress", required = true) String worksAddress,
                                          @RequestParam(value = "worksPhone", required = true) String worksPhone
                                          
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        licence = StringUtils.trimToNull(licence);
        brand = StringUtils.trimToNull(brand);
        specification = StringUtils.trimToNull(specification);
        burdenSheet = StringUtils.trimToNull(burdenSheet);
        store = StringUtils.trimToNull(store);
        expirationDate = StringUtils.trimToNull(expirationDate);
        worksName = StringUtils.trimToNull(worksName);
        worksAddress = StringUtils.trimToNull(worksAddress);
        worksPhone = StringUtils.trimToNull(worksPhone);
        //验证是否有空参数
        if (EmptyUtil.isNullOrEmpty(licence)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_LICENCE_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(brand)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_BRAND_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(specification)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_SPECIFICATION_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(burdenSheet)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_BURDEN_SHEET_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(store)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_STORE_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(expirationDate)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_EXPIRATION_DATE_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(worksName)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_WORKS_NAME_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(worksAddress)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_WORKS_ADDRESS_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(worksPhone)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PHONE_BE_NULL_ERROR));
            return dr;
        }
        Goods goods = GoodsMemDB.getGoodsById(id);
        //goods.setId(id);
        goods.setLicence(licence);
        goods.setBrand(brand);
        goods.setSpecification(specification);
        goods.setBurdenSheet(burdenSheet);
        goods.setStore(store);
        goods.setExpirationDate(expirationDate);
        goods.setWorksName(worksName);
        goods.setWorksAddress(worksAddress);
        goods.setWorksPhone(worksPhone);
        goods.setStatus(Goods.STATUS_CHECK_PENDING);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_PERFECT_GOODS, goods)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 查看商品私钥
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/checkPrivateKey", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> checkPrivateKey(HttpServletRequest request,
                                                  @RequestParam(value = "password", required = true) String password,
                                                  @RequestParam(value = "goodsId", required = true) Long goodsId
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        password = StringUtils.trimToNull(password);
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        Long aid = JwtUtil.getUid(request);
        Admin admin = AdminMemDB.getAdminByAid(aid);
        //验证密码
        if (!Objects.equals(password, admin.getLoginPwd())) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_PASSWD_INCORRECT));
            return dr;
        }
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
        Map<String, Object> dataMap = new HashMap<>();
        if (gk != null){
            dataMap.put("privateKey",gk.getPrivateKey());
            dr.setResult(JsonResultWrapper.succStr(dataMap));
        }else{
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
        }
        return dr;
    }
    
    /**
     * 验证私钥
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/verifyPrivateKey", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> verifyPrivateKey(HttpServletRequest request,
                                               @RequestParam(value = "goodsId", required = true) Long goodsId,
                                               @RequestParam(value = "privateKey", required = true) String privateKey
    ) {
        privateKey = StringUtils.trimToNull(privateKey);
        DeferredResult<String> dr = new DeferredResult<>();
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
        if(gk == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
        }
        if (!Objects.equals(privateKey, gk.getPrivateKey())) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PRIVATE_KEY_ERROR));
            return dr;
        }
        dr.setResult(JsonResultWrapper.succStr(null));
        return dr;
    }
    
    /**
     * 修改流通密码
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/updatePayPwd", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> updatePayPwd(HttpServletRequest request,
                                                  @RequestParam(value = "payPwd", required = true) String payPwd,
                                                  @RequestParam(value = "goodsId", required = true) Long goodsId
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        payPwd = StringUtils.trimToNull(payPwd);
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        //验证流通密码
        if(EmptyUtil.isNullOrEmpty(payPwd)){
            dr.setResult(JsonResultWrapper.errWithData(ErrMessage.CODE_PARAM_PAYPWD_BE_NULL,null));
            return dr;
        }
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
        gk.setPayPwd(payPwd);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GOODS_UPDATE_PAYPWD, gk)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 添加Memo
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/addMemo", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> addMemo(HttpServletRequest request,
                                               @RequestParam(value = "content", required = true) String content,
                                               @RequestParam(value = "goodsId", required = true) Long goodsId,
                                               @RequestParam(value = "payPwd", required = true) String payPwd
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        content = StringUtils.trimToNull(content);
        payPwd = StringUtils.trimToNull(payPwd);
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        //验证内容
        if(EmptyUtil.isNullOrEmpty(content)){
            dr.setResult(JsonResultWrapper.errWithData(ErrMessage.CODE_PARAM_CONTENT_BE_NULL,null));
            return dr;
        }
        Long aid = JwtUtil.getUid(request);
        Admin admin = AdminMemDB.getAdminByAid(aid);
        if (admin == null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        if(Admin.TYPE_DEALER == admin.getType()){
            AdminKey ak = AdminKeyMemDB.getAdminKeyByUid(aid);
            if (!Objects.equals(payPwd, ak.getPayPwd())) {
                dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_ERROR));
                return dr;
            }
        }else if(Admin.TYPE_SUPPLIER == admin.getType()){
            GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
            if (!Objects.equals(payPwd, gk.getPayPwd())) {
                dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_ERROR));
                return dr;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("goodsId",goodsId);
        map.put("content",content);
        map.put("aid",aid);
        map.put("type",admin.getType());
        map.put("createTime",System.currentTimeMillis());
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GOODS_INSET_MEMO, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
   /**
     * 审核商品
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/audit", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> auditGoods(HttpServletRequest request,
                                               @RequestParam(value = "status", required = true) Integer status,
                                               @RequestParam(value = "goodsId", required = true) Long goodsId,
                                               @RequestParam(value = "facade", required = true) String facade,
                                             @RequestParam(value = "moisture", required = true) Double moisture,
                                             @RequestParam(value = "impurity", required = true) Double impurity,
                                             @RequestParam(value = "unsoundGrain", required = true) Double unsoundGrain,
                                             @RequestParam(value = "oleaginousness", required = true) Double oleaginousness,
                                             @RequestParam(value = "aflatoxin", required = true) Double aflatoxin,
                                             @RequestParam(value = "gongContent", required = true) Double gongContent,
                                             @RequestParam(value = "hchResidual", required = true) Double hchResidual,
                                             @RequestParam(value = "ddtResidual", required = true) Double ddtResidual,
                                             @RequestParam(value = "defectiveParticle", required = true) Double defectiveParticle,
                                             @RequestParam(value = "annualOutputOf", required = true) String annualOutputOf,
                                             @RequestParam(value = "placeOfOrigin", required = true) String placeOfOrigin
                                             
    ) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        placeOfOrigin = StringUtils.trimToNull(placeOfOrigin);
        facade = StringUtils.trimToNull(facade);
        annualOutputOf = StringUtils.trimToNull(annualOutputOf);
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        if(status == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_STATUS_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(facade)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_FACADE_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(placeOfOrigin)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PLACEOFORIGIN_BE_NULL_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(annualOutputOf)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ANNUALOUTPUTOF_BE_NULL_ERROR));
            return dr;
        }
        GoodsReports goodsReports = new GoodsReports();
        goodsReports.setGid(goodsId);
        goodsReports.setFacade(facade);
        goodsReports.setMoisture(moisture);
        goodsReports.setImpurity(impurity);
        goodsReports.setUnsoundGrain(unsoundGrain);
        goodsReports.setOleaginousness(oleaginousness);
        goodsReports.setAflatoxin(aflatoxin);
        goodsReports.setGongContent(gongContent);
        goodsReports.setHchResidual(hchResidual);
        goodsReports.setDdtResidual(ddtResidual);
        goodsReports.setDefectiveParticle(defectiveParticle);
        goodsReports.setAnnualOutputOf(annualOutputOf);
        goodsReports.setPlaceOfOrigin(placeOfOrigin);
        goodsReports.setReportsId(aid);
        goodsReports.setCreateTime(System.currentTimeMillis());
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_AUDIT_GOODS,  Pair.of(goodsReports, status))
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 监管局召回
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/recall", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> recallGoods(HttpServletRequest request,
                                             @RequestParam(value = "content", required = true) String content,
                                             @RequestParam(value = "goodsId", required = true) Long goodsId
    ) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("goodsId",goodsId);
        map.put("content",content);
        map.put("status",Goods.STATUS_RECALL);
        map.put("createTime",System.currentTimeMillis());
        map.put("aid",aid);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_RECALL_GOODS, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 供应商转出
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/supplierOut", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> supplierOut(HttpServletRequest request,
                                              @RequestParam(value = "address", required = true) String address,
                                              @RequestParam(value = "number", required = true) Integer number,
                                              @RequestParam(value = "goodsId", required = true) Long goodsId,
                                              @RequestParam(value = "payPwd", required = true) String payPwd
    ) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        payPwd = StringUtils.trimToNull(payPwd);
        address = StringUtils.trimToNull(address);
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        //验证数量
        if(number != null && number<=0){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_NUMBER_ERROR));
            return dr;
        }
        //验证地址
        AdminKey ak = AdminKeyMemDB.getAdminKeyByPublicKey(address);
        if(ak == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ADDRESS_ERROR));
            return dr;
        }
        Admin supplier = AdminMemDB.getAdminByAid(ak.getAid());
        if(Admin.STATUS_NORMAL != supplier.getStatus()){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_STATUS_FROZEN));
            return dr;
        }
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
        if(gk == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
            return dr;
        }
        //验证流通密码
        if (!Objects.equals(payPwd, gk.getPayPwd())) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_ERROR));
            return dr;
        }
        CirculateHistory circulateHistory = new CirculateHistory();
        circulateHistory.setFromAid(aid);
        circulateHistory.setFromAddress(gk.getPublicKey());
        circulateHistory.setAmount(number);
        circulateHistory.setGoodsId(goodsId);
        circulateHistory.setCreateTime(System.currentTimeMillis());
        circulateHistory.setType(CirculateHistory.TYPE_OUT);
        circulateHistory.setPeerAid(ak.getAid());
        circulateHistory.setPeerAddress(ak.getPublicKey());
        circulateHistory.setPrivateKey(gk.getPrivateKey());
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_SUPPLIER_OUT, new Tuple(circulateHistory,payPwd))
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 经销商召回
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/dealerOut", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> dealerOut(HttpServletRequest request,
                                              @RequestParam(value = "number", required = true) Integer number,
                                              @RequestParam(value = "goodsId", required = true) Long goodsId,
                                              @RequestParam(value = "payPwd", required = true) String payPwd
    ) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        payPwd = StringUtils.trimToNull(payPwd);
        //验证经销商
        AdminKey ak = AdminKeyMemDB.getAdminKeyByUid(aid);
        if(ak == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ADDRESS_ERROR));
            return dr;
        }
        //验证流通密码
        if(EmptyUtil.isNullOrEmpty(payPwd) || !payPwd.equals(ak.getPayPwd())){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_ERROR));
            return dr;
        }
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        //验证数量
        if(number != null && number<=0){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_NUMBER_ERROR));
            return dr;
        }
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByGid(goodsId);
        if(gk == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
            return dr;
        }
        CirculateHistory circulateHistory = new CirculateHistory();
        circulateHistory.setFromAddress(gk.getPublicKey());
        circulateHistory.setAmount(number);
        circulateHistory.setGoodsId(goodsId);
        circulateHistory.setCreateTime(System.currentTimeMillis());
        circulateHistory.setType(CirculateHistory.TYPE_INTO);
        circulateHistory.setPeerAid(ak.getAid());
        circulateHistory.setPeerAddress(ak.getPublicKey());
        circulateHistory.setPrivateKey(ak.getPrivateKey());
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_DEALER_OUT, circulateHistory)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 流通记录
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/circulateHistory", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> circulateHistory(HttpServletRequest request,
                                                @RequestParam(value = "goodsId", required = true) Long goodsId
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        if(goodsId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_GOODSID, goodsId)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }

    /**
     * 流通记录（交易码查）
     * @param request
     * @param hashId
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/circulateHistoryByHash", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> circulateHistoryByHash(HttpServletRequest request,
                                                   @RequestParam(value = "hashId", required = true) String hashId
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        if(hashId == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ID_NULL_ERROR));
            return dr;
        }
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_HASHID, hashId)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }

    /**
     * 最新流通记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/circulateHistoryNew", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> circulateHistoryNew(HttpServletRequest request
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_NEW, null)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 查看举报
     * @param request
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/getAccusationHistoryList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getAccusationHistoryList(HttpServletRequest request,
                                                           @RequestParam(value = "status", required = false) Integer status,
                                                           @RequestParam(value = "type", required = false) Integer type,
                                                           @RequestParam(value = "goodsId", required = false) Integer goodsId,
                                                           @RequestParam(value = "startTime", required = false) String startTime,
                                                           @RequestParam(value = "endTime", required = false) String endTime,
                                                           @RequestParam(value = "currentPage", required = false)Long currentPage,
                                                           @RequestParam(value = "count", required = false)Long count) {
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (currentPage == null) currentPage = Pager.START_PAGE;
        if (count == null) count =Pager.DEFAULT_COUNT;
        Long start = (currentPage - 1) * count;
        map.put("type", type);
        map.put("status", status);
        map.put("goodsId", goodsId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("currentPage", start);
        map.put("count", count);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GET_ACCUSATION_HISTORY_LIST, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData(), rsp.getResult().getRecordTotal()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 批量处理举报
     *
     * @param request
     * @param content
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/banchDisposeAccusation", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> banchDisposeAccusation(HttpServletRequest request,
                                                    @RequestParam(value = "content", required = true) String content,
                                                    @RequestParam(value = "id", required = true) List<Long> id
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        content = StringUtils.trimToNull(content);
        Map<String, Object> map = new HashMap<>();
        map.put("disposeContent", content);
        map.put("list", id);
        map.put("status", AccusationHistory.STATUS_YET_DISPOSE);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_BANCH_DISPOSE_ACCUSATION, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 供应商首页统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/supplierIndexCount", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> supplierIndexCount(HttpServletRequest request
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Long aid = JwtUtil.getUid(request);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_SUPPLIER_INDEX_COUNT, aid)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 经销商首页统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/dealerIndexCount", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> dealerIndexCount(HttpServletRequest request
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Long aid = JwtUtil.getUid(request);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_DEALER_INDEX_COUNT, aid)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 监管部门首页统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/monitorIndexCount", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> monitorIndexCount(HttpServletRequest request
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_MONITOR_INDEX_COUNT, null)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }

    /**
     * 区块高度
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/goods/getBlockHeight", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getBlockHeight(HttpServletRequest request
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_BLOCK_HEIGHT, null)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
}
