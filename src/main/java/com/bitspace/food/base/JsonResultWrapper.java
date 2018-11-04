package com.bitspace.food.base;

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
public class JsonResultWrapper extends JsonResult{
    private Object data;

    public Object getData() {
        return data;
    }

    public JsonResultWrapper(String errCode, String errMsg, boolean success, Object data,Long recordTotal,Long counts) {
        super();
        this.data = data;
        this.setErrCode(errCode);
        this.setErrMsg(errMsg);
        this.setSuccess(success);
        this.setRecordTotal(recordTotal);
        this.setCounts(counts);
    }

    public JsonResultWrapper(String errCode, String errMsg, boolean success, Object data,Long recordTotal) {
        super();
        this.data = data;
        this.setErrCode(errCode);
        this.setErrMsg(errMsg);
        this.setSuccess(success);
        this.setRecordTotal(recordTotal);
    }
    public JsonResultWrapper(String errCode, String errMsg, boolean success, Object data) {
        super();
        this.data = data;
        this.setErrCode(errCode);
        this.setErrMsg(errMsg);
        this.setSuccess(success);
    }

    private static JsonResultWrapper success(Object data,Long recordTotal,Long counts) {
        return new JsonResultWrapper("", "", true, data,recordTotal,counts);
    }

    private static JsonResultWrapper success(Object data,Long recordTotal) {
        return new JsonResultWrapper("", "", true, data,recordTotal);
    }
    private static JsonResultWrapper success(Object data) {
        return new JsonResultWrapper("", "", true, data);
    }

    public static String succStr(Object data) {
        return success(data).toJson();
    }
    public static String succStr(Object data,Long recordTotal,Long counts) {
        return success(data,recordTotal,counts).toJson();
    }
    public static String succStr(Object data,Long recordTotal) {
        return success(data,recordTotal).toJson();
    }
    public static String errWithData(String errCode, Object data) {
        return new JsonResultWrapper(errCode, ErrMessage.get(errCode), false, data).toJson();
    }
}
