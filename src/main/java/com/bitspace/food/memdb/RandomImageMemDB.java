package com.bitspace.food.memdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.memdb
 * @class_name
 * @auth erik
 * @create_time 18-3-14 下午5:27
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RandomImageMemDB {
    private static Logger log = LoggerFactory.getLogger(RandomImageMemDB.class);
    
    private static Map<String, String> imgCodeMap = new HashMap<>();
    
    public static String getImgCode(String phone){
        return imgCodeMap.get(phone);
    }
    
    public static void putImgCode(String userName, String code){
        imgCodeMap.put(userName, code);
    }
    
    public static void removeCaptcha(String userName){
        imgCodeMap.remove(userName);
    }
}
