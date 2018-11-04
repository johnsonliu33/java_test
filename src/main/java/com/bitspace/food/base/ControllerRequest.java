package com.bitspace.food.base;

import com.bitspace.food.disruptor.inf.Promise;

/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.base;
 *@class_name   ControllerRequest
 *@auth
 *@create_time  18-3-13 下午8:00
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ControllerRequest extends Request{
    private Promise<Event> promise;

    public ControllerRequest() {
    }

    public ControllerRequest(Object data) {
        super(data);
    }

    public Promise<Event> getPromise() {
        return promise;
    }

    public void setPromise(Promise<Event> promise) {
        this.promise = promise;
    }

}
