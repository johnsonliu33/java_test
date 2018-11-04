package com.bitspace.food.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SystemConfig {

	private static DbConfig dbConfig;


	private static CommonCnf commonCnf;


	private static JwtCnf jwtCnf;

	private static SmsConfig smsConfig;

	@Autowired
	private void setDbConfig(DbConfig config){
		dbConfig = config;
	}


	@Autowired
	private void setCommonCnf(CommonCnf config){
		commonCnf = config;
	}

	@Autowired
	private void setJwtCn(JwtCnf config){
		jwtCnf = config;
	}

	@Autowired
	private void setSmsConfig(SmsConfig config){
		smsConfig = config;
	}
	public static DbConfig getDbConfig() {
		return dbConfig;
	}



	public static CommonCnf getCommonCnf(){
		return commonCnf;
	}

	public static JwtCnf getJwtCnf() {
		return jwtCnf;
	}

	public static SmsConfig getSmsConfig(){
		return smsConfig;
	}
}
