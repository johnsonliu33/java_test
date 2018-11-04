package com.bitspace.food;
/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food
 * @class_name StoreApplication
 * @auth erik
 * @create_time
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

import com.bitspace.food.disruptor.CoreContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class FoodApplication {
    @EventListener(ContextRefreshedEvent.class)
    void init(){
        CoreContainer.getInstance().init();
    }

    public static void main(String[] args) {
        SpringApplication.run(FoodApplication.class, args);
    }
}
