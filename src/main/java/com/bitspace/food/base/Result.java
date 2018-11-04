package com.bitspace.food.base;

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
public class Result {
    public final static String SUCCESS = "SUCCESS";
    private String code;
    private String message;
    private Long recordTotal;

    public static Result success() {
        return new Result(SUCCESS, null);
    }

    public static Result success(String message) {
        return new Result(SUCCESS, message);
    }
    public static Result success(Long recordTotal) {
        return new Result(SUCCESS, null,recordTotal);
    }

    public Result(String code) {
        this.code = code;
    }


    public Result(String code, String message) {
        this.code = code;
        this.message = message;

    }

    public Result(String code, String message,Long recordTotal) {
        this.code = code;
        this.message = message;
        this.recordTotal = recordTotal;
    }



    public boolean isSuccess() {
        return Objects.equals(this.code, Result.SUCCESS);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRecordTotal() {
        return recordTotal;
    }

    public void setRecordTotal(Long recordTotal) {
        this.recordTotal = recordTotal;
    }

    @Override
    public String toString() {
        return "Result [code=" + code + ",recordTotal="+recordTotal+",message=" + message + "]";
    }

    public void set(String code, String message,Long recordTotal) {
        this.code = code;
        this.message = message;
        this.recordTotal = recordTotal;
    }
}
