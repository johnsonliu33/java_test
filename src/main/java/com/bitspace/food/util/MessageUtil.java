package com.bitspace.food.util;

import java.util.Collections;
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
public class MessageUtil {
    
    private static final Map<String, String> MESSAGES;
    public static final String CODE_SYS_ILLEGAL_PARAM = "SYSTEM.0002";
    public static final String CODE_SYS_UNEXPECTED_ERROR = "SYSTEM.0003";
    
    public static final String CODE_PRF_ACCOUNT_NOT_EXISTS_OR_PASSWD_INCORRECT = "PRF.0001";
    
    private MessageUtil() {
    }
    
    static {
        Map<String, String> workingMessage = new HashMap<>();
        workingMessage.put(CODE_SYS_ILLEGAL_PARAM, "Illegal parameter(s).");
        workingMessage.put(CODE_SYS_UNEXPECTED_ERROR, "System has encountered unexpected error(s).");
        workingMessage.put(CODE_PRF_ACCOUNT_NOT_EXISTS_OR_PASSWD_INCORRECT, "phone or password not correct.");
        MESSAGES = Collections.unmodifiableMap(workingMessage);
    }
    
    public static String getErrMsg(String code) {
        String msg = MESSAGES.get(code);
        if (msg == null) msg = code;
        return msg;
    }
    
}
