package com.bitspace.food.handler;




import com.bitspace.food.base.*;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ControllerHandler implements EventHandler<Event> {
    private static Logger log = LoggerFactory.getLogger(ControllerHandler.class);

    private ResponseHandler responseHandler;

    @Override
    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getData() instanceof ControllerRequest) {
            ControllerRequest promiseRequest = (ControllerRequest) event.getData();
            Request request = new Request(promiseRequest.getData());
            if (promiseRequest.getData() instanceof DescriptData)
                request = (Request) promiseRequest.getData();
            request.setReqId(promiseRequest.getReqId());
            request.setOriginatingProcessor(ProcessorType.CONTROLLER);
            responseHandler.getPromiseMap().put(promiseRequest.getReqId(), promiseRequest.getPromise());
            Dispatcher.getInstance().sendMsg(promiseRequest.getOriginatingProcessor(), event.getType(), request);
        }
    }

}
