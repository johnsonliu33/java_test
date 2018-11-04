package com.bitspace.food.controller.admin;

import com.bitspace.food.base.*;
import com.bitspace.food.constants.Pager;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.util.JwtUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
public class JsonAdminManagerController {

    /**
     * 商户列表
     * @param request
     * @param merchantName
     * @param type
     * @param status
     * @param currentPage
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/manager/getMerchantList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getMerchantList(HttpServletRequest request,
                                               @RequestParam(value = "merchantName", required = false) String merchantName,
                                               @RequestParam(value = "type", required = false) Integer type,
                                               @RequestParam(value = "status", required = false) Integer status,
                                               @RequestParam(value = "currentPage", required = false)Long currentPage,
                                               @RequestParam(value = "count", required = false)Long count) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<>();
        Admin admin = AdminMemDB.getAdminByAid(aid);
        if (currentPage == null) currentPage = Pager.START_PAGE;
        if (count == null) count = Pager.DEFAULT_COUNT;
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
        map.put("merchantName", merchantName);
        map.put("type", type);
        map.put("status", status);
        map.put("currentPage", start);
        map.put("count", count);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.MERCHANT, EventType.ADMIN_MERCHANT_MANAGE_LIST, map)
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
     * 冻结/解冻
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/manager/frozenAdmin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> frozenAdmin(HttpServletRequest request,
                                           @RequestParam(value = "aid", required = true) Long aid,
                                           @RequestParam(value = "status", required = true) Long status) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("aid", aid);
        map.put("updateTime",System.currentTimeMillis());
        map.put("status",status);

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.MERCHANT, EventType.ADMIN_MERCHANT_FROZEN, map)
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
     * 审核通过/审核不通过
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/manager/auditAdmin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> auditAdmin(HttpServletRequest request,
                                           @RequestParam(value = "aid", required = true) Long aid,
                                           @RequestParam(value = "status", required = true) Long status,
                                           @RequestParam(value = "auditOpinion", required = false) String auditOpinion) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("aid", aid);
        map.put("updateTime",System.currentTimeMillis());
        map.put("status",status);
        if(auditOpinion == null){
            auditOpinion = "";
        }
        map.put("auditOpinion",auditOpinion);//审核意见

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.MERCHANT, EventType.ADMIN_MERCHANT_AUDIT, map)
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
}
