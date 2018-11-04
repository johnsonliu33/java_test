package com.bitspace.food.base;

import com.bitspace.food.util.JsonUtil;

import java.util.concurrent.atomic.AtomicLong;
/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name  com.bitspace.food.base;
 *@class_name   Event
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
public class Event {
    private EventType type;
    private Object data;
    private Byte toProcessor;
    private Byte fromProcessor;
    private Long timeStamp;
    private boolean remote = false;
    private long sequenceNo;
    private AtomicLong handlerIdentifier = new AtomicLong(-1L);

    public void resetHandlerIdentifier(){
        this.handlerIdentifier.set(-1L);
    }

    public boolean takeBy(Long handlerIdentifier){
        return this.handlerIdentifier.compareAndSet(-1L,handlerIdentifier);
    }

    public Event(EventType type, Object data, Byte toProcessor, Byte fromProcessor) {
        super();
        this.type = type;
        this.data = data;
        this.toProcessor = toProcessor;
        this.fromProcessor = fromProcessor;
        this.timeStamp = System.currentTimeMillis();
    }

    public Event() {
        this.timeStamp = System.currentTimeMillis();
    }

    public long getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(long sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Object getData() {
        return data;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Byte getToProcessor() {
        return toProcessor;
    }

    public void setToProcessor(Byte toProcessor) {
        this.toProcessor = toProcessor;
    }

    public Byte getFromProcessor() {
        return fromProcessor;
    }

    public void setFromProcessor(Byte fromProcessor) {
        this.fromProcessor = fromProcessor;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }


}
