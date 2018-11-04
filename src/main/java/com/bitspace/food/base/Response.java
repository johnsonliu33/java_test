package com.bitspace.food.base;


import com.bitspace.food.util.JsonUtil;
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
public class Response implements RequestInf{
    private String reqId;
    private Object data;
    private Result result;

    public Response() {
        super();
    }

    public Response(Result result) {
        super();
        setResult(result);
    }

    @Override
    public String getReqId() {
        return reqId;
    }

    @Override
    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
