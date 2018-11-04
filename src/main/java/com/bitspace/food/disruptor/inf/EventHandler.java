package com.bitspace.food.disruptor.inf;




import com.bitspace.food.base.EventType;
import com.bitspace.food.base.Request;
import com.bitspace.food.base.Response;
import com.bitspace.food.base.Result;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.impl.ResponseHandler;

import java.util.Map;
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
public interface EventHandler<T> {
    default void onEvent(final T event) {
    }

    default void setPromiseMap(Map<String, Promise<T>> promiseMap) {
    }

    default void setResponseHandler(ResponseHandler responseHandler){
    }

    default void sendResponse(Request request, Result result) {
        sendResponse(request, result, null);
    }

    default void sendResponse(Request request, Result result, Object data) {
        if (request.getOriginatingProcessor() != null) {
            Response response = new Response();
            response.setReqId(request.getReqId());
            response.setResult(result);
            response.setData(data);
            Dispatcher.getInstance().sendMsg(request.getOriginatingProcessor(), EventType.RESPONSE, response);
        }
    }

    default String getIdentifier(){
        String name = this.getClass().getSimpleName();
        if("".equals(name)){
            name=this.getClass().getName();
            name=name.substring(name.lastIndexOf(".")+1);
        }
        return name+"@"+this.hashCode();
    }
}
