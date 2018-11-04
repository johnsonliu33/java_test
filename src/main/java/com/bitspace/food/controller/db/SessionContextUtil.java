package com.bitspace.food.controller.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.controller.db
 * @class_name
 * @auth erik
 * @create_time 18-3-14 下午4:12
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SessionContextUtil {
    private static Logger log = LoggerFactory.getLogger(SessionContextUtil.class);
    
    private SessionContextUtil() { }
    
    public static SessionContext createSession() {
        return new SessionContext();
    }
    
    public static void closeSilently(SessionContext context) {
        if (context == null) {
            return;
        }
        
        rollbackSilently(context);
        
        try {
            context.close();
        } catch (Throwable e) {
            log.error("closeSilently", e);
        }
    }
    
    public static void rollbackSilently(SessionContext context) {
        if (context == null) {
            return;
        }
        
        try {
            context.rollback();
        } catch (Throwable e) {
            log.error("rollbackSilently", e);
        }
    }
}
