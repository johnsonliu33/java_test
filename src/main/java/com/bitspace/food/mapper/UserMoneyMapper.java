package com.bitspace.food.mapper;

import com.bitspace.food.entity.UserMoney;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth erik
 * @create_time 18-3-15 下午8:46
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public interface UserMoneyMapper {


    @Insert({"insert into t_user_money(uid,balance,type,update_time,user_name)values(#{uid},#{balance},#{type}," +
            "#{updateTime},#{userName})"})
    int insertUserMoney(UserMoney userMoney);

    @Select(" SELECT t.uid uid,t.integral_name integralName,t.balance balance,t.integral integral,t.type type," +
            "t.update_time updateTime," +
            "   t.rechargeintegral rechargeIntegral,t.intointegral intoIntegral FROM t_user_money t")
    List<UserMoney> selectAll();
}


