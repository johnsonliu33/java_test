package com.bitspace.food.mapper;

import com.bitspace.food.entity.AdminKey;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.mapper
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/29 11:22
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AdminKeyMapper {
    
    @Select("select aid as `aid`"
            + ",private_key as `privateKey`"
            + ",public_key as `publicKey`"
            + ",pay_pwd as `payPwd`"
            + ",create_time as `createTime`"
            + " from t_admin_key")
    public List<AdminKey> selectAll();
    
    
    @Select("select aid as `aid`"
            + ",public_key as `publicKey`"
            + " from t_admin_key")
    public List<AdminKey> selectPublicKey();
    
    
    @Select("select aid as `aid`"
            + ",private_key as `privateKey`"
            + " from t_admin_key")
    public List<AdminKey> selectPrivateKey();
    
    @Insert("insert into t_admin_key "
            + "(aid"
            + ",pay_pwd"
            + ",private_key"
            + ",public_key"
            + ",create_time)"
            + "values"
            + "(#{aid}"
            + ",#{payPwd}"
            + ",#{privateKey}"
            + ",#{publicKey}"
            + ",#{createTime})")
    int insertAdminKey(AdminKey adminKey);
    
    @Select("select aid from t_admin_key where private_key=#{privateKey}")
    Long getAdminPrivateKeyUid(String privateKey);
    
    
    @Select("select aid from t_admin_key where public_key=#{publicKey}")
    Long getAdminPublicKeyUid(String publicKey);
}
