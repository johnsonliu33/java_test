package com.bitspace.food.webconfig;

import com.bitspace.food.webconfig.interceptor.AdminInterceptor;
import com.bitspace.food.webconfig.interceptor.UserInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebInterceptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new UserInterceptor()).addPathPatterns("/api/json/food/user/**").excludePathPatterns(userExclude());
        registry.addInterceptor(new AdminInterceptor()).addPathPatterns("/api/json/food/admin/**").excludePathPatterns(exclude());
        super.addInterceptors(registry);
    }
    
    private String[] userExclude() {
        List<String> ret = new ArrayList<>();
        ret.add("/api/json/food/user/register**");
        ret.add("/api/json/food/user/login**");
        ret.add("/api/json/food/user/imgCode**");
        ret.add("/api/json/food/user/logout**");
        ret.add("/api/json/food/user/captcha**");
        ret.add("/api/json/food/user/updatePwd*");
        return ret.toArray(new String[1]);
    }
    private String[] exclude(){
        List<String> ret = new ArrayList<>();
        ret.add("/api/json/food/admin/register**");
        ret.add("/api/json/food/admin/login**");
        ret.add("/api/json/food/admin/imgCode**");
        ret.add("/api/json/food/admin/goods/circulateHistoryByHash");
        ret.add("/api/json/food/admin/goods/circulateHistoryNew");
        ret.add("/api/json/food/admin/goods/getBlockHeight");
        return ret.toArray(new String[1]);
    }

}
