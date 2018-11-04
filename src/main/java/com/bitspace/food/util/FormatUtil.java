package com.bitspace.food.util;


import com.bitspace.food.constants.CalculationMultiplier;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
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
public class FormatUtil {

    private FormatUtil() {
    }

    public static double toActualMoney(Long storageMoney) {
        if(null == storageMoney) return 0.0;
        return toActualMoneyBigDecimal(storageMoney).doubleValue();
    }

    public static BigDecimal toActualMoneyBigDecimal(Long storageMoney) {
        if (storageMoney == null)
            storageMoney = 0L;
        return new BigDecimal(storageMoney).divide(CalculationMultiplier.BIGDECIMAL_MONEY);
    }

    public static Long toStorageMoney(Double actualMoney) {
        if(null == actualMoney) return 0L;
        return Double.valueOf(new BigDecimal(actualMoney * CalculationMultiplier.MONEY).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue()).longValue();
    }
    public static Long doubleToLong(double money) {
        return Double.valueOf(new BigDecimal(money).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue()).longValue();
    }
    public static Long toStorageMoney(BigDecimal actualMoney) {
        return actualMoney.multiply(CalculationMultiplier.BIGDECIMAL_MONEY).longValue();
    }


    public static String CountryUserName(String userName) {
        return StringUtils.trimToEmpty(userName);
    }

    public static String CountryPhone(String phone) {
        return StringUtils.trimToEmpty(phone);
    }
    public static String CountryCodePhone(String countryCode, String phone) {
        return StringUtils.trimToEmpty(countryCode) + "_" + StringUtils.trimToEmpty(phone);
    }

}
