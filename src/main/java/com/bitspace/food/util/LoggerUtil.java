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
public class LoggerUtil {

    private static Logger LOG = LoggerFactory.getLogger("email");
    private static final String COLON_PREFIX = ":";

    private static String sourcePosi(int depth) {
        if (0 > depth) {
            return "";
        }
        Exception e = new Exception();
        StackTraceElement[] l = e.getStackTrace();
        if (null == l || depth >= l.length)
            return "";
        return "[" + l[depth].getFileName() + ":" + l[depth].getLineNumber() + " " + l[depth].getMethodName() + "]";
    }

    public static void debug(Logger log, String logMsg) {
        if (log.isDebugEnabled()) {
            log.debug(sourcePosi(2) + logMsg);
        }
    }

    public static void warn(Logger log, String logMsg) {
        if (log.isWarnEnabled()) {
            log.warn(sourcePosi(2) + logMsg);
        }
    }

    public static void warn(Logger log, Throwable e) {
        if (log.isWarnEnabled()) {
            log.warn(sourcePosi(2), e);
        }
    }

    public static void warn(Logger log, String logMsg, Throwable e) {
        if (log.isWarnEnabled()) {
            log.warn(sourcePosi(2) + logMsg, e);
        }
    }

    public static void error(Logger log, String logMsg) {
        if (LOG.isErrorEnabled()) {
            LOG.error(sourcePosi(2) + logMsg);
        }
    }

    public static void error(Logger log, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(sourcePosi(2) + e.getMessage(), e);
        }
    }

    public static void error(Logger log, String logMsg, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(sourcePosi(2) + logMsg + ". " + e.getMessage(), e);
        }
    }

}
