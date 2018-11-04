package com.bitspace.food.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtCnf {
	private String tokenHeader;
	private String secretKey;

	private String keyPrefix;
	private Long expireTime;


	private String registerRpcUrl;
	
	private String sendxrpUrl;
	private String sourceAddress;
	private String sourcePassword;
	private String addGoodsToChainUrl;
	
	public String getSourcePassword() {
		return sourcePassword;
	}
	
	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}
	
	public String getSourceAddress() {
		return sourceAddress;
	}
	
	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	
	public String getTokenHeader() {
		return tokenHeader;
	}

	public void setTokenHeader(String tokenHeader) {
		this.tokenHeader = tokenHeader;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	
	public String getSendxrpUrl() {
		return sendxrpUrl;
	}
	
	public void setSendxrpUrl(String sendxrpUrl) {
		this.sendxrpUrl = sendxrpUrl;
	}
	
	public String getRegisterRpcUrl() {
	
		return registerRpcUrl;
	}
	
	public void setRegisterRpcUrl(String registerRpcUrl) {
		this.registerRpcUrl = registerRpcUrl;
	}

	public String getAddGoodsToChainUrl() {
		return addGoodsToChainUrl;
	}

	public void setAddGoodsToChainUrl(String addGoodsToChainUrl) {
		this.addGoodsToChainUrl = addGoodsToChainUrl;
	}
}
