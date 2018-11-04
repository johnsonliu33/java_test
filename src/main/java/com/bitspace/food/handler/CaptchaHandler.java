package com.bitspace.food.handler;


import com.bitspace.food.base.*;
import com.bitspace.food.config.SystemConfig;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;
import com.bitspace.food.memdb.CaptchaMemDB;
import com.bitspace.food.memdb.RandomImageMemDB;
import com.bitspace.food.util.*;
import com.offbynull.coroutines.user.Continuation;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name
 * @class_name
 * @auth
 * @create_time
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CaptchaHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(CaptchaHandler.class);

    /**
     * 获取短信验证码
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.CAPTCHA_GENERATE)
    private void getCaptcha(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Pair<Byte, String> pair = (Pair<Byte, String>) req.getData();
        Byte type = pair.getLeft();
        String userName = pair.getRight();
        String key = type + userName;
        if (!CaptchaMemDB.isValidType(type)) {
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_PARAM_ERROR));
            return;
        }
        TupleOf3<Long, AtomicInteger, String> tupleOf3 = CaptchaMemDB.getCaptcha(key);
        long now = System.currentTimeMillis();
        if (tupleOf3 == null || now - tupleOf3.getFirst() > CaptchaMemDB.canResendTime) {
            String captcha = RandomUtil.generateCaptcha();
            tupleOf3 = new TupleOf3<>(now, new AtomicInteger(0), captcha);
            CaptchaMemDB.putCaptcha(key, tupleOf3);
            Dispatcher.getInstance().sendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_SMS_SEND, Pair.of(userName, captcha));
            responseHandler.sendResponse(req, Result.success("generate success"), captcha);
            return;
        }
        responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_TIMES_LIMIT), (CaptchaMemDB.canResendTime - now + tupleOf3.getFirst()) / 1000);
    }

    /**
     * 验证短信验证码是否正确
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.CAPTCHA_CHECK)
    private void checkCaptcha(Request req, ResponseHandler responseHandler, Continuation continuation) {
        TupleOf3<Byte, String, String> phoneCaptcha = (TupleOf3<Byte, String, String>) req.getData();
        Byte type = phoneCaptcha.getFirst();
        String phone = phoneCaptcha.getSecond();
        String captcha = phoneCaptcha.getThird();
        String key = type + phone;
        TupleOf3<Long, AtomicInteger, String> tupleOf3 = CaptchaMemDB.getCaptcha(key);
        long now = System.currentTimeMillis();
        if (null == tupleOf3 || now - tupleOf3.getFirst() > CaptchaMemDB.expireTime) {
            CaptchaMemDB.removeCaptcha(key);
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_EXPIRED));
            return;
        }
        if (tupleOf3.getSecond().incrementAndGet() > CaptchaMemDB.retryTimes) {
            CaptchaMemDB.removeCaptcha(key);
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_RETRY_OUT));
            return;
        }

        if (!tupleOf3.getThird().equals(captcha)) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("retryTimes", tupleOf3.getSecond().get() > CaptchaMemDB.retryTimes ? 0 : CaptchaMemDB.retryTimes - tupleOf3.getSecond().get());
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_INVALID), data);
            return;
        }
        responseHandler.sendResponse(req, Result.success());
    }

    /**
     * 获取短信验证码
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.CAPTCHA_SMS_SEND)
    private void sendCodeSMS(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Pair<String, String> pair = (Pair<String, String>) req.getData();
        String phone = pair.getLeft();
        String code = pair.getRight();

//        String smsAccount = SystemConfig.getSmsConfig().getHkAccount();
//        String smsAccountPwd = SystemConfig.getSmsConfig().getHkPassword();
        String smsAccount = "CI0036445";
        String smsAccountPwd = "gidzYCOrWG394d";

        if (smsAccount == null) {
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_SMS_SEND_FAIL));
            LoggerUtil.error(log, " Send sms error, phone=" + phone + ",captcha=" + code);
            return;
        }
        String smsContent = code;
        try {
            smsContent = "Your security code is:" + code + " , please verify in time. To ensure your account security, please do not show this code to others.";
        } catch (Exception e) {
            LoggerUtil.error(log, " URLEncoder error, phone=" + phone + ",captcha=" + code);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("account", smsAccount);
        map.put("password", smsAccountPwd);
        map.put("msg", smsContent);
        map.put("phone", phone);
        String params = JsonUtil.toJson(map);

        String httpurl = "http://intapi.253.com/send/json";

        HttpParam httpParam = new HttpParam(null, "POST", httpurl, params, null, null);
        Event event = responseHandler.sendRequest(ProcessorType.HTTP, EventType.HTTP_COMMON, httpParam, req, continuation);
        Response rsp = (Response) event.getData();
        if (!rsp.getResult().isSuccess()) {
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_SMS_SEND_FAIL));
            LoggerUtil.error(log, " Send sms error, phone=" + phone + ",captcha=" + code);
        } else {
            try {
                org.asynchttpclient.Response d = (org.asynchttpclient.Response) rsp.getData();
                String body = d.getResponseBody();

                HashMap obj = JsonUtil.fromJson(body, HashMap.class);

                if ("0".equals(obj.get("code"))) {
                    responseHandler.sendResponse(req, Result.success());
                    LoggerUtil.debug(log, " Send sms success, phone=" + phone + ",captcha=" + code);
                } else {
                    responseHandler.sendResponse(req, new Result(ErrMessage.CODE_SMS_SEND_FAIL));
                    LoggerUtil.error(log, " Send sms error, phone=" + phone + ",captcha=" + code + ",errorCode=" + obj.get("code"));
                }
            } catch (Exception exception) {
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_SMS_SEND_FAIL));
                LoggerUtil.error(log, " Send sms error, phone=" + phone + ",captcha=" + code);
            }
        }
    }

    /**
     * 获取登录图片验证码
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.CAPTCHA_IMGCODE_GENERATE)
    private void getCaptchaImgCode(Request req, ResponseHandler responseHandler, Continuation continuation) {
        String userName = (String) req.getData();

        String code = RandomImageUtil.generateRandomString(4);
        String imgBase64 = RandomImageUtil.generateBase64Img(code);
        RandomImageMemDB.putImgCode(userName, code);

        LoggerUtil.debug(log, "userName:" + userName + ", img code:" + code);

        responseHandler.sendResponse(req, Result.success("generate success"), imgBase64);
        return;
    }

    /**
     * 验证用户输入图片验证码是否正确
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.CAPTCHA_IMGCODE_CHECK)
    private void checkCaptchaImgCode(Request req, ResponseHandler responseHandler, Continuation continuation) {
        Tuple<String, String> userNameCaptcha = (Tuple<String, String>) req.getData();
        String phone = userNameCaptcha.getFirst();
        String captcha = userNameCaptcha.getSecond();

        String code = RandomImageMemDB.getImgCode(phone);

        if (code == null) {
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_EXPIRED));
            return;
        }

        if (!code.equalsIgnoreCase(captcha)) {
            responseHandler.sendResponse(req, new Result(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_INVALID));
            return;
        }

        responseHandler.sendResponse(req, Result.success());
    }
}
