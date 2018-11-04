package com.bitspace.food.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;
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
public class RandomUtil {
    static final char[] digitCharset = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9'};


    public static String generateCaptcha() {
        return RandomStringUtils.random(5, digitCharset);
    }


    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static double getRandomGaussian() {
        Random random = new Random();
        double result = random.nextGaussian();
        result = result < 0 ? -result : result;
        if (result > 3) {
            return getRandomGaussian();
        }
        return result;
    }


}
