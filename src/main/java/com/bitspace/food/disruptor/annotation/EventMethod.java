package com.bitspace.food.disruptor.annotation;




import com.bitspace.food.base.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMethod {
    EventType value();
}
