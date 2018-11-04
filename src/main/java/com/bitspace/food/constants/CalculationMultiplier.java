package com.bitspace.food.constants;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 在实际数值（浮点数）和计算数值（长整数）之间转换时的精度乘数
 */
public class CalculationMultiplier {

    public static final int PRICISION_MONEY = 4;

    /** 金额的乘数 */
    public static final long MONEY = (long) Math.pow(10, PRICISION_MONEY);

    /** 金额的乘数 */
    public static final BigDecimal BIGDECIMAL_MONEY = new BigDecimal(MONEY, new MathContext(PRICISION_MONEY, RoundingMode.HALF_UP));
}