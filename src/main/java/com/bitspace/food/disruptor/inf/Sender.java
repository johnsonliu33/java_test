package com.bitspace.food.disruptor.inf;


import com.bitspace.food.base.EventType;

/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.controller
 *@class_name   JsonTransactionController
 *@auth         erik
 *@create_time  18-3-13 下午8:39
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface Sender<T> {
    default Promise<T> sendWithResponse(EventType eventType, Object data) {
        return null;
    }

    default void send(EventType eventType, Object data) {
    }

    default void send(EventType eventType, Object data, String id, Long milliSecond, Long interval) {
    }

    default void stop(String id) {
    }

    default Long[] getStatistics() {
        return null;
    }


}
