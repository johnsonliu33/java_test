package com.bitspace.food.util;
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
public class TupleOf4<T1, T2, T3, T4> extends TupleOf3<T1, T2, T3> {
    private T4 four;
    
    public TupleOf4(T1 first, T2 second, T3 third, T4 four) {
        super(first, second, third);
        this.four = four;
    }
    
    public T4 getFour() {
        return four;
    }
    
    public void setFour(T4 four) {
        this.four = four;
    }
    
    @Override
    public String toString() {
        return "TupleOf4 [first=" + super.getFirst() + ", second=" + super.getSecond()
                + ", third=" + super.getThird() + ", four=" + four + "]";
    }
    
}
