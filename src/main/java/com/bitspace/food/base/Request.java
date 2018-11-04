package com.bitspace.food.base;

import java.util.concurrent.atomic.AtomicLong;
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
public class Request implements RequestInf{
    private static AtomicLong id = new AtomicLong(0);
    private String reqId;
    private Object data;
    private boolean replayed = false;
    private transient Byte originatingProcessor;

    public Request() {
    }

    public Request(Object data) {
        super();
        this.data = data;
        this.reqId = System.currentTimeMillis()+":"+id.getAndIncrement();
    }

    public Request(String reqId, Object data) {
        super();
        this.reqId = reqId;
        this.data = data;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Byte getOriginatingProcessor() {
        return originatingProcessor;
    }

    public void setOriginatingProcessor(Byte originatingProcessor) {
        this.originatingProcessor = originatingProcessor;
    }

    public boolean isReplayed() {
        return replayed;
    }

    public void setReplayed(boolean replayed) {
        this.replayed = replayed;
    }

    @Override
    public String toString() {
        return "Request [reqId=" + reqId + ", data=" + data + ", replayed=" + replayed + "]";
    }

}
