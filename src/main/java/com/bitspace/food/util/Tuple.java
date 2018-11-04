package com.bitspace.food.util;

import java.io.Serializable;
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
public class Tuple<T1, T2> implements Serializable {
    private static final long serialVersionUID = -5675188866350979146L;
    
    private T1 first;
    private T2 second;
    
    public Tuple(T1 first, T2 second) {
        super();
        this.first = first;
        this.second = second;
    }
    
    public Tuple() {
    }
    
    public T1 getFirst() {
        return first;
    }
    
    public void setFirst(T1 first) {
        this.first = first;
    }
    
    public T2 getSecond() {
        return second;
    }
    
    public void setSecond(T2 second) {
        this.second = second;
    }
    
    @Override
    public String toString() {
        return "Tuple [first=" + first + ", second=" + second + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Tuple<T1, T2> other = (Tuple<T1, T2>) obj;
        if (!Objects.equals(this.first, other.first))
            return false;
        if (!Objects.equals(this.second, other.second))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (null != first ? first.hashCode() : 0);
        result = 37 * result + (null != second ? second.hashCode() : 0);
        return result;
    }
    
}