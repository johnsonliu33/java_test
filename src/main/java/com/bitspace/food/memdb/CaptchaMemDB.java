package com.bitspace.food.memdb;


import com.bitspace.food.util.TupleOf3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food
 *@class_name   TransactionApplication
 *@auth         Administrator
 *@create_time  18-3-13 下午8:00
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CaptchaMemDB {
    private static Map<String, TupleOf3<Long, AtomicInteger, String>> captchaMap = new HashMap<>();//type_userName, time, ,retrytimes, code
    private static Map<String, TupleOf3<Long, Long, String>> emailVerifyCodeMap = new HashMap<>();//email,uid, time, captcha


    public static final Long canResendTime = 2 * 60 * 1000L;
    public static final Long expireTime = 10 * 60 * 1000L;
    public static final int retryTimes = 5;
    public static final long emailVerifyExpireTime = 24 * 60 * 60 * 1000L;

    public static final byte TP_REG = 1;//1:注册, 2:支付, 3-找回登录密码，4-找回支付密码， 5-解绑手机号，6-绑定新手机号
    public static final byte TP_PAY = 2;
    public static final byte TP_FORGET_PWD_LOGIN = 3;
    public static final byte TP_FORGET_PWD_PAYMENT = 4;
    public static final byte TP_UNBIND = 5;
    public static final byte TP_BIND = 6;
    public static final byte TP_CURRENCY_ADDR_BIND = 7;
    public static final byte TP_WITHDRAW = 8;

    public static Boolean isValidType(Byte type){
        switch (type){
            case TP_REG:
            case TP_PAY:
            case TP_FORGET_PWD_LOGIN:
            case TP_FORGET_PWD_PAYMENT:
            case TP_UNBIND:
            case TP_BIND:
            case TP_CURRENCY_ADDR_BIND:
            case TP_WITHDRAW:
                return true;
        }
        return false;
    }
    public static TupleOf3<Long, AtomicInteger, String> getCaptcha(String typeUserName){
        return captchaMap.get(typeUserName);
    }

    public static void putCaptcha(String typePhone, TupleOf3<Long, AtomicInteger, String> tuple){
        captchaMap.put(typePhone, tuple);
    }

    public static void removeCaptcha(String phone){
        captchaMap.remove(phone);
    }

    public static TupleOf3<Long, Long, String> getEmailVerifyCode(String email){
        return emailVerifyCodeMap.get(email);
    }

    public static void putEmailVerifyCode(String email, TupleOf3<Long, Long, String> code){
        emailVerifyCodeMap.put(email, code);

    }


}
