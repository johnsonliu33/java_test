package com.bitspace.food.util;

import org.asynchttpclient.Param;

import java.util.Collection;
import java.util.List;
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
public class HttpParam {
    public Map<String, Collection<String>> headerMap ;
    public String method;
    public String url;
    public String bodyParams;
    public List<Param> queryParam;
    public List<Param> formParam;
    public HttpParam(){}
    public HttpParam(Map<String, Collection<String>> headerMap, String method, String url, String bodyParams,
                     List<Param> queryParam, List<Param> formParam){
        this.headerMap = headerMap;
        this.method = method;
        this.url = url;
        this.bodyParams = bodyParams;
        this.queryParam = queryParam;
        this.formParam = formParam;
    }

    @Override
    public String toString(){
        return JsonUtil.toJson(this);
    }
}
