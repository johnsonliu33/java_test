package com.bitspace.food.util;

import java.util.Objects;
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
public class TupleOf3<T1, T2, T3> extends Tuple<T1, T2> {
    private T3 third;
    
    public TupleOf3(T1 first, T2 second, T3 third) {
        super(first, second);
        this.third = third;
    }
    
    public T3 getThird() {
        return third;
    }
    
    public void setThird(T3 third) {
        this.third = third;
    }
    
    @Override
    public String toString() {
        return "TupleOf3 [first=" + getFirst() + ", second=" + getSecond() + ", third=" + third + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof TupleOf3) {
            TupleOf3<T1, T2, T3> other = (TupleOf3<T1, T2, T3>) obj;
            if (!Objects.equals(this.getFirst(), other.getFirst())) return false;
            if (!Objects.equals(this.getSecond(), other.getSecond())) return false;
            if (!Objects.equals(this.third, other.third)) return false;
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.getFirst() == null ? 0 : this.getFirst().hashCode()) ^ (this.getSecond() == null ? 0 : this.getSecond().hashCode()) ^ (this.third == null ? 0 : this.third.hashCode());
    }
}