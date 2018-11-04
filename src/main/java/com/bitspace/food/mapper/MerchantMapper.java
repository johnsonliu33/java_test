package com.bitspace.food.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 15:04
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MerchantMapper {

    /**
     * 统计查询商品列表
     * @param param
     * @return
     */
    Integer getMerchantListCount(Map<String, Object> param);

    /**
     * 查询商品列表
     * @param param
     * @return
     */
    List<LinkedHashMap<String,Object>> getMerchantList(Map<String, Object> param);

}
