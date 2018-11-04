package com.bitspace.food.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class KeyUtil {
    private static Logger log = LoggerFactory.getLogger(KeyUtil.class);

    private static final String ENCODING = "UTF-8";

    private static String key_code = "kO3dYQP2rfD9GuSG";

    private KeyUtil() {
    }

    public static String generatePrivateKey(String text) {
        if (text == null) {
            return null;
        }
        try {
            return AESUtil.encryptToBase64(MD5Util.digest(text+key_code), key_code);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                LoggerUtil.error(log, e);
            }
        }
        return "";
    }

    public static String getRawPrivateKey(String aesPrivateKey) {
        if (aesPrivateKey == null) {
            return null;
        }
        try {
            return AESUtil.decryptFromBase64(aesPrivateKey, key_code);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                LoggerUtil.error(log, e);
            }
        }
        return "";
    }
}
