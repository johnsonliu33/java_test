package com.bitspace.food.base;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.base;
 *@class_name   ErrMessage
 *@auth
 *@create_time
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ErrMessage {
    public static final String CODE_SUCCESS = "SUCCESS";

    // 系统错误码
    public static final String CODE_SYS_UNKNOWN = "SYS.0001";
    public static final String CODE_PARAM_ERROR = "SYS.0002";//1.参数为空

    //user error code
    public static final String CODE_USER_NOT_EXISTS = "USER.0001";//当前无此用户
    public static final String CODE_USER_UNAUTH = "USER.0002";
    public static final String CODE_USER_PASSWD_INCORRECT = "USER.0003";//登录密码不正确
    public static final String CODE_USER_EXISTS = "USER.0004";//已存在
    public static final String CODE_USER_INVITECODE_NOT_EXISTS = "USER.0005";
    public static final String USER_NAME_ERROR = "USER.0006";//用户名已存在
    public static final String USER_PWD_ERROR = "USER.0007";//密码错误
    public static final String CODE_USER_NOTSET_PRICEPASSWD = "USER.0008";//未设置资金密码
    public static final String CODE_USER_NOTSET_PRICEPASSWD_ERROR = "USER.0009";//资金密码错误
//    public static final String CODE_USER_MAXIMUM_QUANTITY_LIMIT = "USER.0010";//超过用户数量限制
    public static final String CODE_USER_STATUS_FROZEN = "USER.0011";//用户账户被冻结
   //user application
    public static final String CODE_USER_APPLICATION_NULL = "APPLICATION.0001";  //提现余额不足
    public static final String CODE_USER_APPLICATION_BALANCE  = "APPLICATION.0002";  //可用余额不足
    public static final String CODE_USER_APPLICATION_BALANCE_ERR  = "APPLICATION.0003";  //未绑定积分账户
    public static final String CODE_USER_APPLICATION_HAS_BEEN_BING_ERR  = "APPLICATION.0004";  //该积分账号已被绑定

    //admin error code
    public static final String CODE_ROLE_NAME_EXISTS = "ADMIN.0001";//已经存在
    //account
    public static final String CODE_ACCT_EXISTS = "ACCT.0001";
    public static final String CODE_ACCT_UNSUFFIENT_BALACNCE = "ACCT.0002";
    public static final String CODE_ACCT_NOT_EXISTS = "ACCT.0003";
    public static final String CODE_ACCT_TRANSFER_SELF = "ACCT.0004";
    public static final String CODE_ACCT_WALLET_ADDRESS_REQUEST_ERROR = "ACCT.0005";
    public static final String CODE_ACCT_Min_WITHDRAW_AMOUNT = "ACCT.0004";
    public static final String CODE_ACCT_Max_WITHDRAW_AMOUNT = "ACCT.0005";
    
    
    // captcha
    public static final String CODE_CAPTCHA_TIMES_LIMIT = "CAPTCHA.0001";
    public static final String CODE_CAPTCHA_INVALID = "CAPTCHA.0002";//短信验证码格式不正确
    public static final String CODE_CAPTCHA_EXPIRED = "CAPTCHA.0003";
    public static final String CODE_CAPTCHA_RETRY_OUT = "CAPTCHA.0004";
    public static final String CODE_CAPTCHA_IMAGE_CODE_INVALID = "CAPTCHA.0005";//图片验证码不正确
    public static final String CODE_CAPTCHA_IMAGE_CODE_EXPIRED = "CAPTCHA.0006";//图片验证码为空

    //db
    public static final String CODE_DB_ERROR = "DB.0001";
    public static final String CODE_DB_MONEY_ERROR = "MONEY.0001";
    public static final String CODE_DB_IDEMPOTENT_ERR = "DB.0002";
    
    public static final String CODE_DB_ORDER_ERR = "ORDER.0001";  //订单异常
    public static final String CODE_DB_ORDER_PAY_ON_ERR = "ORDER.0002";  //已付款
    public static final String CODE_DB_ORDER_FINSH_ERR = "ORDER.0003";  //已完成
    public static final String CODE_DB_ORDER_PAY_NO_ERR = "ORDER.0004";  //已取消
    //http
    public static final String CODE_HTTP_EXCEPTION = "HTTP.0001";
    public static final String CODE_HTTP_UNKNOWN_METHOD = "HTTP.0002";
    public static final String CODE_HTTP_REQUEST_INVALIDATION = "HTTP.0003";
    public static final String CODE_HTTP_WITHDRAW_FAIL = "HTTP.0004";//创建账户失败
    public static final String CODE_HTTP_TRANSFER_FAIL = "HTTP.0005";//请求转账失败
    public static final String CODE_HTTP_GOODS_FAIL = "HTTP.0006";//商品上链失败
    public static final String CODE_HTTP_GOODS_REPORTS_FAIL = "HTTP.0007";//商品记录上链失败
    public static final String CODE_HTTP_GOODS_HISTORY_FAIL = "HTTP.0008";//商品流通记录上链失败
    public static final String CODE_HTTP_GOODS_MEMO_FAIL = "HTTP.0009";//商品memo上链失败
    public static final String CODE_HTTP_GOODS_SELECT_FAIL = "HTTP.0010";//链上查询数据失败

    public static final String CODE_IMG_TYPE_ERR = "IMG.0001";
    public static final String CODE_IMG_SAVE_ERR = "IMG.0002";
    public static final String CODE_SMS_SEND_FAIL = "SMS.0001";
    //wallet
    public static final String CODE_WALLET_OPERATION_FAILED="WALLET.0001";
    public static final String CODE_WALLET_INSUFFICIENT_BALANCE="WALLET.0002";
    public static final String CODE_WALLET_NOT_EXISTS="WALLET.0003";
    //announcement
    public static final String ANNOUNCEMENT_NOT_EXISTS="ANNOUNCEMENT.0003";
    //add
    public static final String CODE_INSERT_ERR="INSERT.0001";//插入失败
    public static final String CODE_SELECT_ERR="SELECT.0001";//查询失败
    public static final String USER_PHONE_NOT = "APP_USER.0010";//手机号不能为空
    public static final String USER_CARD_NOT = "APP_USER.0011";//身份证不能为空
    public static final String USER_USER_CODE_NOT = "APP_USER.0012";//商品地址不存在
    public static final String USER_USER_CODE_NOT_REPORTS = "APP_USER.0013";//TXID不存在
    public static final String USER_USER_CODE_NOT_REPORTS_HISTORY = "APP_USER.0014";//商品暂无流通记录
    
    //goods
    public static final String CODE_GOODS_KEY_NOT_EXISTS = "GOODS.0001"; //商品仓库信息不存在

    
    public static final String CODE_PARAM_NAME_NULL_ERROR = "PARAM.0001";//name为空
    public static final String CODE_PARAM_ID_NULL_ERROR = "PARAM.0002";//id不能为空
    public static final String CODE_PARAM_PRIVATE_KEY_ERROR = "PARAM.0003";//私钥错误
    public static final String CODE_PARAM_PAYPWD_BE_NULL = "PARAM.0004";//流通密码为空
    public static final String CODE_PARAM_PRIVATE_KEY_BE_NULL = "PARAM.0005";//私钥为空
    public static final String CODE_PARAM_CONTENT_BE_NULL = "PARAM.0006";//内容为空
    public static final String CODE_PARAM_GOODS_NULL = "PARAM.0007";//商品不存在
    public static final String CODE_PARAM_GOODS_YET_OUT = "PARAM.0008";//商品已转出
    public static final String CODE_PARAM_GOODS_NOT_OUT = "PARAM.0009";//商品未转出
    public static final String CODE_PARAM_NUMBER_ERROR = "PARAM.0010";//数量错误
    public static final String CODE_PARAM_STATUS_NULL_ERROR = "PARAM.0011";//状态为空
    public static final String CODE_PARAM_CAUSE_NULL_ERROR = "PARAM.0012";//原因为空
    public static final String CODE_PARAM_NUMBER_ALL_ERROR = "PARAM.0013";//数量必须为全部数量
    public static final String CODE_PARAM_PAYPWD_ERROR = "PARAM.0014";//流通密码错误
    public static final String CODE_PARAM_ADDRESS_ERROR = "PARAM.0015";//地址错误
    public static final String CODE_PARAM_LICENCE_BE_NULL_ERROR = "PARAM.0016";//生产许可证为空
    public static final String CODE_PARAM_BRAND_BE_NULL_ERROR = "PARAM.0017";//品牌为空
    public static final String CODE_PARAM_SPECIFICATION_BE_NULL_ERROR = "PARAM.0018";//规格为空
    public static final String CODE_PARAM_BURDEN_SHEET_BE_NULL_ERROR = "PARAM.0019";//配料表为空
    public static final String CODE_PARAM_STORE_BE_NULL_ERROR = "PARAM.0020";//存储方式为空
    public static final String CODE_PARAM_EXPIRATION_DATE_BE_NULL_ERROR = "PARAM.0021";//保质期为空
    public static final String CODE_PARAM_WORKS_NAME_BE_NULL_ERROR = "PARAM.0022";//厂名为空
    public static final String CODE_PARAM_WORKS_ADDRESS_BE_NULL_ERROR = "PARAM.0023";//厂址为空
    public static final String CODE_PARAM_PHONE_BE_NULL_ERROR = "PARAM.0024";//手机号为空
    public static final String CODE_PARAM_FACADE_BE_NULL_ERROR = "PARAM.0025";//外观、色泽为空
    public static final String CODE_PARAM_PLACEOFORIGIN_BE_NULL_ERROR = "PARAM.0026";//年产度为空
    public static final String CODE_PARAM_ANNUALOUTPUTOF_BE_NULL_ERROR = "PARAM.0027";//产地为空
    public static final Map<String, String> MESSAGES;
    static {
        Map<String, String> workingMessage = new HashMap<>();
        workingMessage.put(CODE_SYS_UNKNOWN, "System error.");
        workingMessage.put(CODE_PARAM_ERROR, "Param error.");

        //user
        workingMessage.put(CODE_USER_NOT_EXISTS, "User not exists.");
        workingMessage.put(CODE_USER_UNAUTH, "User not authorized.");
        workingMessage.put(CODE_USER_PASSWD_INCORRECT, "User password incorrect.");
        workingMessage.put(CODE_USER_EXISTS, "User already exists.");
        workingMessage.put(CODE_USER_INVITECODE_NOT_EXISTS, "User inviteCode non-existent.");
        workingMessage.put(CODE_USER_STATUS_FROZEN,"User already freeze");
        //account
        workingMessage.put(CODE_ACCT_EXISTS, "User already opened account.");
        workingMessage.put(CODE_ACCT_UNSUFFIENT_BALACNCE, "Unsuffient balance.");
        workingMessage.put(CODE_ACCT_NOT_EXISTS, "User account not exists.");
        workingMessage.put(CODE_ACCT_TRANSFER_SELF, "cannot transfer to yourself.");
        workingMessage.put(CODE_ACCT_WALLET_ADDRESS_REQUEST_ERROR, "request wallet address failed.");

        //captcha
        workingMessage.put(CODE_CAPTCHA_TIMES_LIMIT, "Verify code times limit.");
        workingMessage.put(CODE_CAPTCHA_INVALID, "Verify code invalid.");
        workingMessage.put(CODE_CAPTCHA_EXPIRED, "Verify code expired.");
        workingMessage.put(CODE_CAPTCHA_RETRY_OUT, "Verify code retry out.");
        workingMessage.put(CODE_CAPTCHA_IMAGE_CODE_INVALID, "Image code invalid.");

        //db
        workingMessage.put(CODE_DB_ERROR, "Db error.");
        workingMessage.put(CODE_DB_IDEMPOTENT_ERR, "Update db idempotent failed ");

        //http
        workingMessage.put(CODE_HTTP_EXCEPTION, "Http exception");
        workingMessage.put(CODE_HTTP_UNKNOWN_METHOD, "Http unknown method");
        workingMessage.put(CODE_HTTP_REQUEST_INVALIDATION, "Http request is invalid");
        workingMessage.put(CODE_HTTP_GOODS_FAIL, "Commodity chain failure");//
        workingMessage.put(CODE_HTTP_GOODS_REPORTS_FAIL, "Commodity record chain failure");
        workingMessage.put(CODE_HTTP_GOODS_HISTORY_FAIL, "Commodity circulation record chain failure");
        workingMessage.put(CODE_HTTP_GOODS_MEMO_FAIL, "Commodity circulation memo chain failure");
        workingMessage.put(CODE_HTTP_GOODS_SELECT_FAIL, "Query data failure");

        //add
        workingMessage.put(USER_PHONE_NOT, "Not Set phone");
        workingMessage.put(USER_CARD_NOT, "Not Set i.d.card");
        workingMessage.put(USER_USER_CODE_NOT, "Commodity code does not exist");

        workingMessage.put(CODE_IMG_TYPE_ERR, "Image type error");
        workingMessage.put(CODE_IMG_SAVE_ERR, "Picture save failure");
        workingMessage.put(CODE_SMS_SEND_FAIL, "Send verify code failed");
        //wallet
        workingMessage.put(CODE_WALLET_OPERATION_FAILED, "Wallet operation failed.");
        workingMessage.put(CODE_WALLET_INSUFFICIENT_BALANCE, "Insufficient balance.");
        workingMessage.put(CODE_WALLET_NOT_EXISTS, "Wallet does not exist.");
        //PARAM
        workingMessage.put(CODE_PARAM_NAME_NULL_ERROR, "Name be null.");
        workingMessage.put(CODE_PARAM_ID_NULL_ERROR, "ID be null.");
        workingMessage.put(CODE_PARAM_PRIVATE_KEY_ERROR, "Private key error.");
        workingMessage.put(CODE_PARAM_PAYPWD_BE_NULL, "Pay password error.");
        workingMessage.put(CODE_PARAM_PRIVATE_KEY_BE_NULL, "Private key be null.");
        workingMessage.put(CODE_PARAM_CONTENT_BE_NULL, "Content be null.");
        workingMessage.put(CODE_PARAM_GOODS_NULL, "Goods be null.");
        workingMessage.put(CODE_PARAM_GOODS_YET_OUT, "Goods yet out.");
        workingMessage.put(CODE_PARAM_GOODS_NOT_OUT, "Goods not out.");
        workingMessage.put(CODE_PARAM_NUMBER_ERROR, "Number error.");
        workingMessage.put(CODE_PARAM_STATUS_NULL_ERROR, "Status be null.");
        workingMessage.put(CODE_PARAM_CAUSE_NULL_ERROR, "Cause be null.");
        workingMessage.put(CODE_PARAM_NUMBER_ALL_ERROR, "Number be all.");
        workingMessage.put(CODE_PARAM_PAYPWD_ERROR,"Pay Pwd error.");
        workingMessage.put(CODE_PARAM_ADDRESS_ERROR,"Address error");
        workingMessage.put(CODE_PARAM_LICENCE_BE_NULL_ERROR,"licence be null.");
        workingMessage.put(CODE_PARAM_BRAND_BE_NULL_ERROR,"brand be null.");
        workingMessage.put(CODE_PARAM_SPECIFICATION_BE_NULL_ERROR,"specification be null.");
        workingMessage.put(CODE_PARAM_BURDEN_SHEET_BE_NULL_ERROR,"burdenSheet be null.");
        workingMessage.put(CODE_PARAM_STORE_BE_NULL_ERROR,"store be null.");
        workingMessage.put(CODE_PARAM_EXPIRATION_DATE_BE_NULL_ERROR,"expiration Date be null.");
        workingMessage.put(CODE_PARAM_WORKS_NAME_BE_NULL_ERROR,"works Name be null.");
        workingMessage.put(CODE_PARAM_WORKS_ADDRESS_BE_NULL_ERROR,"works Address be null.");
        workingMessage.put(CODE_PARAM_PHONE_BE_NULL_ERROR,"works Phone be null.");
        workingMessage.put(CODE_PARAM_FACADE_BE_NULL_ERROR, "facade be null.");
        workingMessage.put(CODE_PARAM_PLACEOFORIGIN_BE_NULL_ERROR, "placeOfOrigin be null.");
        workingMessage.put(CODE_PARAM_ANNUALOUTPUTOF_BE_NULL_ERROR, "annualOutputOf be null.");
        MESSAGES = Collections.unmodifiableMap(workingMessage);
    }

    public static String get(String code, String... tokens) {
        String message = MESSAGES.get(code);
        if (message == null) {
            return code;
        } else {
            return MessageFormat.format(message, (Object[]) tokens);
        }
    }

    public static String[] getAsArray(String code) {
        String message = MESSAGES.get(code);
        if (message == null) {
            return new String[]{code, null};
        }

        return new String[]{code, message};
    }
    
    
    public class CODE_HTTP_WITHDRAW_FAIL {
    }
}
