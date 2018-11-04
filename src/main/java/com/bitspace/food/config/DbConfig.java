package com.bitspace.food.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food.config
 *@class_name   DbConfig
 *@auth         erik
 *@create_time  18-3-13 下午8:39
 *@company      香港币特空间交易平台有限公司
 *@comments     
 *@method_name  
 *@return       
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Configuration
public class DbConfig {
    
    private static Logger log=LoggerFactory.getLogger(DbConfig.class);
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String user;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    private static DbConfig instance;
    
    public String getUrl() {
        return url;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
//    public static DbConfig instance() {
//        if (instance == null) {
//            instance = new DbConfig();
//        }
//        return instance;
//    }
}
