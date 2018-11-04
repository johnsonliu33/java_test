package com.bitspace.food.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class MD5Util {
    private static Logger log = LoggerFactory.getLogger(MD5Util.class);
    
    private static final String ENCODING = "UTF-8";
    
    private static String safe_code = "2KSFGPsXf#N93mqbIkqG9B@ga&7oR@M";
    
    private MD5Util() {
    }
    
    public static String digest(String text) {
        if (text == null) {
            return null;
        }
        try {
            return digest(text.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                LoggerUtil.error(log, e);
            }
        }
        return "";
    }
    
    public static String digestPassword(String text) {
        if (text == null) {
            return null;
        }
        text = text + safe_code;
        try {
            return digest(text.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                LoggerUtil.error(log, e);
            }
        }
        return "";
    }
    
    public static String digest(byte[] bytes) {
        try {
            byte[] digestedBytes = MessageDigest.getInstance("MD5").digest(bytes);
            return bytesToString(digestedBytes);
        } catch (NoSuchAlgorithmException e) {
            if (log.isErrorEnabled()) {
                LoggerUtil.error(log, e);
            }
        }
        return "";
    }
    
    private static String bytesToString(byte[] digest) {
        
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(
                    (digest[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }
        return hexString.toString();
    }
    
    public static byte[] digestToByte(byte[] bytes) throws NoSuchAlgorithmException {
        byte[] digestedBytes = MessageDigest.getInstance("MD5").digest(bytes);
        return digestedBytes;
    }
    
    public static String digestToBase64Url(String text)
            throws NoSuchAlgorithmException {
        if (text == null) {
            return null;
        }
        return Base64.encodeBase64URLSafeString(digestToByte(text.getBytes()));
    }
}
