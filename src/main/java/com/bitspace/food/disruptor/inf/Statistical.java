package com.bitspace.food.disruptor.inf;
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
public interface Statistical {
    Long[] getStatistics();
    static Long[] newStat(){
        return new Long[]{0L, 0L, 0L, 0L, 0L, 0L};
    }
    int RECV_CNT=0,         //received count
            PROC_CNT=1,     //processed count
            PROC_TIME=2,    //total processed time (microsecond)
            START_AT=3,     //start at (millisecond)
            TTL_LAT=4,      //total latency (millisecond)
            MAX_LAT=5;      //max latency (millisecond)
}
