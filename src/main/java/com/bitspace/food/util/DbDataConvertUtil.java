package com.bitspace.food.util;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.util
 * @class_name
 * @auth erik
 * @create_time 18-3-14 下午9:42
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DbDataConvertUtil {
    public static Byte toByte(Object o) {
        try {
            if (o instanceof Number) {
                return ((Number) o).byteValue();
            }
            return Byte.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Integer toInteger(Object o) {
        try {
            if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            return Integer.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Long toLong(Object o) {
        try {
            if (o instanceof Number) {
                return ((Number) o).longValue();
            }
            return Long.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Double toDouble(Object o) {
        try {
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            }
            return Double.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String toString(Object o) {
        if (null == o)
            return null;
        return o.toString();
    }
}
