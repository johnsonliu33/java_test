package com.bitspace.food.mapper;

import com.bitspace.food.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    //账户信息修改
    int updateUser(User user);

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Insert("insert into t_user (name, user_img,mobile,login_pwd,pay_pwd,status,create_time,update_time,role,countryCode)" +
            "  values(#{userName}, #{userImg}, #{mobile}, #{loginPwd},#{payPwd}, #{status}, #{createTime}, #{updateTime}," +
            "#{role},#{countryCode}) "
    )
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "uid", before = false, resultType = Long.class)
    int insertUser(User user);

    @Select(
            " SELECT id uid ,NAME userName,user_img userImg,mobile mobile,login_pwd loginPwd,pay_pwd payPwd,status status," +
                    "create_time createTime,update_time updateTime," +
                    " role role "+
                    " from t_user "
    )
    List<User> selectAll();

    /**
     * 修改账户登录密码
     * @param user
     * @return
     */
    int updatePwd(User user);

    /**
     * 修改账户交易密码
     * @param user
     * @return
     */
    int updatePricePwd(User user);
    
    
}
