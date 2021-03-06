package com.bitspace.food.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties(prefix = "common")
public class CommonCnf {

	private Boolean openDoc;



	public Boolean getOpenDoc() {
		return openDoc;
	}

	public void setOpenDoc(Boolean openDoc) {
		this.openDoc = openDoc;
	}

}
