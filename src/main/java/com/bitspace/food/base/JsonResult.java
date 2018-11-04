package com.bitspace.food.base;


import com.bitspace.food.util.JsonUtil;
/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name  com.bitspace.food.base;
 *@class_name
 *@auth
 *@create_time
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JsonResult {
    public static final String SUCCESS_RESULT_JSON = JsonUtil.toJson(new JsonResult());

    private boolean success = true;

    private String errCode;

    private String errMsg;

    private String callbackSequence;

    private Long recordTotal;

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    private Long counts;

    private Long sysTime;

    public static String errResultString(String errCode) {
        return createErrResult(ErrMessage.getAsArray(errCode)).toJson();
    }

    public static String errResultString(String[] error) {
        return createErrResult(error).toJson();
    }

    public static JsonResult createErrResult(String errCode, String errMsg) {
        JsonResult result = new JsonResult();
        result.success = false;
        result.errCode = errCode;
        result.errMsg = errMsg;
        return result;
    }

    public static JsonResult createErrResult(String[] error) {
        JsonResult result = new JsonResult();
        result.success = false;
        if (null == error || 2 > error.length)
            return result;
        result.errCode = error[0];
        result.errMsg = error[1];
        return result;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCallbackSequence() {
        return callbackSequence;
    }

    public void setCallbackSequence(String callbackSequence) {
        this.callbackSequence = callbackSequence;
    }

    public void setError(String[] error, Boolean success) {
        if (null == error || 2 > error.length) return;
        this.errCode = error[0];
        this.errMsg = error[1];
        if (null == success) this.success = false;
        else this.success = success;
    }

    public void setError(String[] error) {
        setError(error, false);
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }

    public Long getRecordTotal() {
        return recordTotal;
    }

    public void setRecordTotal(Long recordTotal) {
        this.recordTotal = recordTotal;
    }

    public String setErrorAndReturnJson(String[] error) {
        this.setError(error);
        return this.toJson();
    }

    public String toJson() {
        this.sysTime = System.currentTimeMillis();
        return JsonUtil.toJson(this);
    }
}
