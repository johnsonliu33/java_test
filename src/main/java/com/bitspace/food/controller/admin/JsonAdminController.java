package com.bitspace.food.controller.admin;

import com.bitspace.food.base.*;
import com.bitspace.food.controller.AbstractController;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.entity.Admin;
import com.bitspace.food.entity.AdminKey;
import com.bitspace.food.entity.GoodsKey;
import com.bitspace.food.entity.User;
import com.bitspace.food.memdb.*;
import com.bitspace.food.util.*;
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
import java.util.Map;
import java.util.Objects;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.controller.admin
 * @class_name 后台管理
 * @auth Administrator
 * @create_time 2018/5/25 14:22
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class JsonAdminController extends AbstractController {

    /**
     * 注册
     * @param request
     * @param loginName
     * @param password
     * @param imgCode
     * @param type
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/register", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> register(HttpServletRequest request,
                                           @RequestParam(value = "name", required = true) String name,
                                           @RequestParam(value = "loginName", required = true) String loginName,
                                           @RequestParam(value = "password", required = true) String password,
                                           @RequestParam(value = "imgCode", required = true) String imgCode,
                                           @RequestParam(value = "type", required = true) Integer type) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        name = StringUtils.trimToNull(name);
        imgCode = StringUtils.trimToNull(imgCode);
        loginName = StringUtils.trimToNull(loginName);
        password = StringUtils.trimToNull(password);

        //密码验证
        if (password == null || password.length() < 4) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PWD_ERROR));
            return dr;
        }
        if(imgCode == null || imgCode.length() > 4){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_INVALID));
            return dr;
        }
        Admin admin = new Admin();
        admin.setName(name);
        admin.setLoginName(loginName);
        admin.setLoginPwd(password);
        admin.setType(type);
        admin.setStatus(Admin.STATUS_NORMAL);
        admin.setCreateTime(System.currentTimeMillis());

        //验证用户名是否已经存在
        if (AdminMemDB.getAdminByLoginName(admin.getLoginName()) != null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_EXISTS));
            return dr;
        }

        Dispatcher dispatcher = Dispatcher.getInstance();
        //录入
        dispatcher.controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_CHECK,
                new Tuple<>(FormatUtil.CountryPhone(loginName), imgCode))
                .thenCompose(e -> {
                    return dispatcher.controllerAppSendMsg(ProcessorType.ADMIN, EventType.ADMIN_REGISTER, admin);
                }).thenAccept(e -> {
            try {
                String session = JwtUtil.createJwtToken(admin.getAid());
                SessionMemDB.putSession(admin.getAid(), session);
                Map<String, Object> result = new HashMap<>();
                result.put("systemLoginKey", session);
                result.put("aid", admin.getAid());
                result.put("userName", admin.getLoginName());
                dr.setResult(JsonResultWrapper.succStr(result));
            } catch (Exception exception) {
                LoggerUtil.error(log, exception);
                //此处提示信息应改为 注册失败
                dr.setResult(JsonResult.errResultString(ErrMessage.CODE_SYS_UNKNOWN));
            }
        }) .fail(e -> {
            Response rsp = (Response) e.getData();
            dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
        });
        return dr;
    }
    
    /**
     * 登录
     *
     * @param request
     * @param loginName
     * @param password
     * @param imgCode
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> login(HttpServletRequest request,
                                        @RequestParam(value = "loginName", required = true) String loginName,
                                        @RequestParam(value = "password", required = true) String password,
                                        @RequestParam(value = "imgCode", required = true) String imgCode) {
        DeferredResult<String> dr = new DeferredResult<>();
        loginName = StringUtils.trimToNull(loginName);
        Admin admin = AdminMemDB.getAdminByLoginName(loginName);
        password =StringUtils.trimToNull(password);
        if(EmptyUtil.isNullOrEmpty(password)){
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PWD_ERROR));
            return dr;
        }
        if (EmptyUtil.isNullOrEmpty(loginName) || admin == null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        //判断是否被冻结
        if(Admin.STATUS_FROZEN.equals(admin.getStatus())){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_STATUS_FROZEN));
            return dr;
        }
        //验证密码
        if (!Objects.equals(password, admin.getLoginPwd())) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_PASSWD_INCORRECT));
            return dr;
        }
        if(imgCode == null || imgCode.length() > 4){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_INVALID));
            return dr;
        }
        //校验图片验证码
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_CHECK,
                new Tuple<>(FormatUtil.CountryPhone(loginName), imgCode))
                .thenAccept(e -> {
                    try {
                        String session = JwtUtil.createJwtToken(admin.getAid());
                        SessionMemDB.putSession(admin.getAid(), session);
                        Map<String, Object> result = new HashMap<>();
                        result.put("systemLoginKey", session);
                        result.put("uid", admin.getAid());
                        result.put("loginName", admin.getLoginName());
                        result.put("name", admin.getName());
                        result.put("type", admin.getType());
                        AdminKey ak = AdminKeyMemDB.getAdminKeyByUid(admin.getAid());
                        result.put("payPwdSet", ak != null);
                        dr.setResult(JsonResultWrapper.succStr(result));
                    } catch (Exception exception) {
                        LoggerUtil.error(log, exception);
                        dr.setResult(JsonResult.errResultString(ErrMessage.CODE_SYS_UNKNOWN));
                    }
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 获取图片验证码
     *
     * @param request
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/imgCode", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getCaptcha(HttpServletRequest request,
                                             @RequestParam(value = "loginName", required = false) String loginName) {

        DeferredResult<String> dr = new DeferredResult<>();

        loginName = StringUtils.trimToNull(loginName);

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_GENERATE,
                FormatUtil.CountryPhone(loginName))
                .thenAccept(e -> {

                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 冻结
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/frozenAdmin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> register(HttpServletRequest request,
                                           @RequestParam(value = "aid", required = true) Long aid) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("aid", aid);
        map.put("updateTime",System.currentTimeMillis());
        map.put("status",Admin.STATUS_FROZEN);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.ADMIN, EventType.ADMIN_FROZEN_ADMIN, map)
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
     * 设置流通密码
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/setPayPwd", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> setPayPwd(HttpServletRequest request,
                                           @RequestParam(value = "payPwd", required = true) String payPwd) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        payPwd = StringUtils.trimToNull(payPwd);
        if(EmptyUtil.isNullOrEmpty(payPwd)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PAYPWD_BE_NULL));
            return dr;
        }
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.ADMIN, EventType.ADMIN_UPDATE_PAYPWD, Pair.of(aid, payPwd) )
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
     * 获取商品仓地址
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/admin/getAddress", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getAddress(HttpServletRequest request) {
        Long aid = JwtUtil.getUid(request);
        DeferredResult<String> dr = new DeferredResult<>();
        AdminKey adminKey = AdminKeyMemDB.getAdminKeyByUid(aid);
        Map<String,Object> dataMap = new HashMap<>();
        if(adminKey == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
        }else{
            dataMap.put("address",adminKey.getPublicKey());
        }
        dr.setResult(JsonResultWrapper.succStr(dataMap));
        return dr;
    }

}
