package com.bitspace.food.base;

/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.base;
 *@class_name   DescriptData
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
/**
 * 当设置为此req时表示要使uid的事件整个完成才做这个user的下一个
 */
public class DescriptData extends  Request{
    public static class Meta {
        public Long uid;
    }
    public Meta meta;
    public DescriptData(Long uid, Object data){
        super(data);
        this.meta = new Meta();
        this.meta.uid = uid;
    }
    private DescriptData(){}
}
