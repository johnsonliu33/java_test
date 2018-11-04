package com.bitspace.food.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {

    private String cnAccount;
    private String cnPassword;
    private String hkAccount;
    private String hkPassword;


    public String getCnAccount() {
        return cnAccount;
    }

    public void setCnAccount(String cnAccount) {
        this.cnAccount = cnAccount;
    }

    public String getCnPassword() {
        return cnPassword;
    }

    public void setCnPassword(String cnPassword) {
        this.cnPassword = cnPassword;
    }

    public String getHkAccount() {
        return hkAccount;
    }

    public void setHkAccount(String hkAccount) {
        this.hkAccount = hkAccount;
    }

    public String getHkPassword() {
        return hkPassword;
    }

    public void setHkPassword(String hkPassword) {
        this.hkPassword = hkPassword;
    }
}
