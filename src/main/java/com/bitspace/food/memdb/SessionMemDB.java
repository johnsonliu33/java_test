package com.bitspace.food.memdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
public class SessionMemDB {
    private static Logger log = LoggerFactory.getLogger(SessionMemDB.class);

    private static Map<Long, String> sessionMap = new HashMap<>();

    public static String getSessionByUid(Long uid) {
        return sessionMap.get(uid);
    }

    public static void putSession(Long uid, String session) {
        sessionMap.put(uid, session);
    }
    
    
    public static String getSessionByRole(Long role) {
        return sessionMap.get(role);
    }
    
    public static void putRoleSession(Long role, String session) {
        sessionMap.put(role, session);
    }

    public static void removeSession(Long uid) {
        sessionMap.remove(uid);
    }
}
